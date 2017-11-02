package com.lxtech.cloud.main;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lxtech.cloud.db.CloudTargetHandler;
import com.lxtech.cloud.db.IndexChangeHandler;
import com.lxtech.cloud.db.model.CloudTargetIndexChange;
import com.lxtech.cloud.net.CloudPackageHeader;
import com.lxtech.cloud.net.CloudPackageType;
import com.lxtech.cloud.net.KestrelConnector;
import com.lxtech.cloud.util.CrawlerConfig;
import com.lxtech.cloud.util.GCache;
import com.lxtech.cloud.util.HttpClient;
import com.lxtech.cloud.util.JsonUtil;
import com.lxtech.cloud.util.algorithm.IndexChangeAdjuster;
import com.lxtech.cloud.util.mail.SendMail;

public class WebSocketEndpoint extends WebSocketClient {
	public static final String HEART_BEAT_MSG = "3,19,0,1,%s,0,0,0,0,,0,0\r\n\r\n{\"ConnectionID\":11}";

	public static final String REQUEST_UPDATE_PUSH_EMPTY = "3,2,0,202,%s,0,0,0,0,,0,0\r\n\r\n[]";
	public static final String REQUEST_UPDATE_PUSH = "3,244,0,202,%s,0,0,0,0,,0,0\r\n\r\n[{\"Market\":17000,\"Code\":\"BU\",\"Type\":1,\"BrokerCount\":20,\"Language\":0,\"PushFlag\":1},{\"Market\":17000,\"Code\":\"AG\",\"Type\":1,\"BrokerCount\":20,\"Language\":0,\"PushFlag\":1},{\"Market\":17000,\"Code\":\"CU\",\"Type\":1,\"BrokerCount\":20,\"Language\":0,\"PushFlag\":1}]";

	private String addr;
	private int port;

	private boolean isClosed = false;
	
	private GCache<String, String> cache;

	public WebSocketEndpoint(String addr, int port) throws URISyntaxException {
		super(new URI(String.format("ws://%s:%s", addr, port)));
		this.addr = addr;
		this.port = port;
		this.cache = new GCache<String, String>(100, 30);
	}

	private final static Logger logger = LoggerFactory.getLogger(WebSocketEndpoint.class);
	private final static String BODY_SPLITTER = "\r\n\r\n";

	public boolean isClosed() {
		return this.isClosed;
	}

	public WebSocketEndpoint(URI uri) {
		super(uri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println(handshakedata.getHttpStatusMessage());

		List<String> commandList = new ArrayList<String>();
		commandList.add("3,18,0,50,1,0,0,0,0,,0,0\r\n\r\n[{\"Market\":17000}]");
		commandList.add(
				"3,127,0,153,2,0,0,0,0,,0,0\r\n\r\n{\"PushFlag\":0,\"GetSymInfo\":1,\"Symbol\":[{\"Market\":17000,\"Code\":\"BU\"},{\"Market\":17000,\"Code\":\"AG\"},{\"Market\":17000,\"Code\":\"CU\"}]}");
		commandList.add("3,2,0,202,3,0,0,0,0,,0,0\r\n\r\n[]");
		commandList.add(
				"3,244,0,202,4,0,0,0,0,,0,0\r\n\r\n[{\"Market\":17000,\"Code\":\"BU\",\"Type\":1,\"BrokerCount\":20,\"Language\":0,\"PushFlag\":1},{\"Market\":17000,\"Code\":\"AG\",\"Type\":1,\"BrokerCount\":20,\"Language\":0,\"PushFlag\":1},{\"Market\":17000,\"Code\":\"CU\",\"Type\":1,\"BrokerCount\":20,\"Language\":0,\"PushFlag\":1}]");

		String updateRequestFormat = "3,%s,0,152,5,0,0,0,0,,0,0\r\n\r\n%s";
		String requestBody = "{\"Market\":17000, \"Code\":\"%s\", \"PushFlag\":0, \"TimeType\":0,\"TimeValue0\":60,\"TimeValue1\":1200,\"Day\":\"%s 0:0:0\"}";
		String day = new SimpleDateFormat("yyyy-M-d").format(new Date(System.currentTimeMillis()));
		
		List<String> codeList = ImmutableList.of("BU", "AG", "CU");
		for (String code: codeList) {
			String body = String.format(requestBody, "BU", day);
			String req = String.format(updateRequestFormat, body.length(), body);
			commandList.add(req);
		}
		
//		commandList.add(String.format(updateRequestFormat, "BU", day));
//		commandList.add(String.format(updateRequestFormat, "AG", day));
//		commandList.add(String.format(updateRequestFormat, "CU", day));
		// commandList.add("3,115,0,152,5,0,0,0,0,,0,0\r\n\r\n{\"Market\":17000,
		// \"Code\":\"BU\", \"PushFlag\":0,
		// \"TimeType\":0,\"TimeValue0\":60,\"TimeValue1\":1200,\"Day\":\"2016-11-7
		// 0:0:0\"}");
		// commandList.add("3,115,0,152,6,0,0,0,0,,0,0\r\n\r\n{\"Market\":17000,
		// \"Code\":\"AG\", \"PushFlag\":0,
		// \"TimeType\":0,\"TimeValue0\":60,\"TimeValue1\":1200,\"Day\":\"2016-11-7
		// 0:0:0\"}");
		// commandList.add("3,115,0,152,7,0,0,0,0,,0,0\r\n\r\n{\"Market\":17000,
		// \"Code\":\"CU\", \"PushFlag\":0,
		// \"TimeType\":0,\"TimeValue0\":60,\"TimeValue1\":1200,\"Day\":\"2016-11-7
		// 0:0:0\"}");

		for (String command : commandList) {
			byte[] byteArr = getByteArray(command);
			this.send(byteArr);
		}

		// now create a thread to send heart beat message to server
		Thread t = new Thread(new HeartBeatSync(this));
		t.start();

		Thread t2 = new Thread(new PushReqSync(this));
		t2.start();
	}

	// this is used to send heart beat information to the socket server
	private static class HeartBeatSync implements Runnable {
		private WebSocketEndpoint endpoint;
		private int hb_id = 1;

		public HeartBeatSync(WebSocketEndpoint endpoint) {
			this.endpoint = endpoint;
		}

		@Override
		public void run() {
			while (true) {
				if (this.endpoint.isClosed) {
					break;
				}
				String msg = String.format(HEART_BEAT_MSG, this.hb_id);
				this.endpoint.send(msg);
				logger.info("send heart beat request" + msg);
				this.hb_id++;
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class PushReqSync implements Runnable {
		private WebSocketEndpoint endpoint;
		private int hb_id = 11;

		public PushReqSync(WebSocketEndpoint endpoint) {
			this.endpoint = endpoint;
		}

		@Override
		public void run() {
			while (true) {
				if (this.endpoint.isClosed) {
					break;
				}
				String msg = String.format(REQUEST_UPDATE_PUSH_EMPTY, this.hb_id);
				this.endpoint.send(msg);

				msg = String.format(REQUEST_UPDATE_PUSH, (this.hb_id + 1));
				logger.info("send update request" + msg);
				this.endpoint.send(msg);
				this.hb_id += 5;
				try {
					Thread.sleep(180 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onMessage(ByteBuffer bytes) {
		String str = new String(bytes.array());
		logger.info("on message:" + str);
		System.out.println(" on message : " + str);
		
		//put it in memcache queue
//		KestrelConnector.enqueue(str);

		int index = str.indexOf(BODY_SPLITTER);
		if (index > 0) { // valid message
			String header = str.substring(0, index);
			String body = str.substring(index + 4);
			if (header.indexOf("xml") >= 0) { // the web socket handshake
				logger.info("received handshake message:" + header);
			} else {
				CloudPackageHeader head = CloudPackageHeader.parse(header);
				int type = Integer.valueOf(head.getType());
				if (type == CloudPackageType.MSG_TYPE_SERPushPrice) {
					if (Strings.isNullOrEmpty(this.cache.get(str))) {
						//directly enqueue the data
						KestrelConnector.enqueue(str);
						this.cache.put(str, "OK");
						//invoke http service
						//HttpClient.get(CrawlerConfig.get("price.update.url") + "?data="+body);
						
						// instead of invoking the http service, we save the data into db
						Map<String, Object> map = (Map<String, Object>) JsonUtil.convertStringToObject(body);
						List<Map<String, Object>> mapList = (List<Map<String, Object>>) map.get("Symbol");
						for (Map<String, Object> m : mapList) {
							String code = m.get("Code") + "";
							String now = ((Double) m.get("Now")).intValue() + "";
							String time = m.get("Time") + "";
							DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-M-d H:m:s");
							DateTime dt = DateTime.parse(time, format);
							// server is using GMT time zone
							Timestamp adjustedTime = new Timestamp(dt.getMillis() + 8 * 3600 * 1000);

							try {
								int oldIndex = CloudTargetHandler.retrieveTargetIndex(code);
								if (oldIndex < 10 && code.equals("BU")) {
									oldIndex = 3555;//just for BU
								}
								
								//now calculate the new index value
								int originalIndex = Integer.valueOf(now).intValue();
								if (originalIndex < 10) {
									originalIndex = oldIndex + originalIndex;
								}
								
								int calculatedIndex = IndexChangeAdjuster.calculateNewIndex(code, originalIndex);
								CloudTargetIndexChange change = new CloudTargetIndexChange();
								change.setSubject(code);
								change.setTime(adjustedTime);
								change.setIndex(calculatedIndex);
								
								IndexChangeHandler.saveData(change);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} else {
			logger.error("received invalid package : " + str);
		}
	}

	@Override
	public void onMessage(String message) {
		System.out.println(message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println(code + reason + remote);
		String template = "Socket has been closed, code:%s, reason:%s, remote:%s";
		String closeInfo = String.format(template, code, reason, remote);
		logger.info(closeInfo);
		this.isClosed = true;
		// String emails = CrawlerConfig.get("list.email");
		// SendMail.send_163("Socket client closed", closeInfo,
		// emails.split(","));
	}

	@Override
	public void onError(Exception ex) {
		logger.error(ex.getMessage());
		String emails = CrawlerConfig.get("list.email");
		try {
			SendMail.send_163("Socket client has error", ex.getMessage(), emails.split(","));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] getByteArray(String source) {
		return source.getBytes();
	}

	public static void main(String[] args) throws URISyntaxException {
		//WebSocketEndpoint c = new WebSocketEndpoint("114.55.63.183", 8502);
		WebSocketEndpoint c = new WebSocketEndpoint("123.56.222.166", 8502);
		c.connect();
		System.out.println("after connect");

		// String str = "{\"Market\":17000, \"Code\":\"%s\", \"PushFlag\":0,
		// \"TimeType\":0,\"TimeValue0\":60,\"TimeValue1\":1200,\"Day\":\"%s
		// 0:0:0\"}";
		//
		// System.out.println((String.format(str, "BU",
		// "2016-11-10")).length());

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		// System.out.println(sdf.format(new Date()));
		
/*		String body = "{\"Symbol\":[{\"Market\":17000,\"Code\":\"BU\",\"Time\":\"2016-11-7 4:7:2\",\"Now\":2931}]}";
		String response = HttpClient.post(CrawlerConfig.get("price.update.url"), ImmutableMap.of("data", body));
		System.out.println(response);*/
	}

}
