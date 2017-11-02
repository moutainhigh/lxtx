package com.lxtech.cloud.bitcoin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.lxtech.cloud.db.CloudTargetHandler;
import com.lxtech.cloud.db.IndexChangeHandler;
import com.lxtech.cloud.db.model.CloudTargetIndexChange;
import com.lxtech.cloud.net.CloudPackageHeader;
import com.lxtech.cloud.net.CloudPackageType;
import com.lxtech.cloud.net.KestrelConnector;
import com.lxtech.cloud.util.GCache;
import com.lxtech.cloud.util.JsonUtil;
import com.lxtech.cloud.util.algorithm.IndexChangeAdjuster;

public class WebSocketUtils extends WebSocketClient {

//	private static final String url = "wss://be.huobi.com/ws";
	private static final String url = "wss://api.huobi.com/ws";
	
	private static final String detail_ltccny = "market.ltccny.detail";
	
	private static final String detail_btccny = "market.btccny.detail";
	
	private static final String MARKET_DATA_PREFIX = "market:";
	
	private GCache<String, String> cache;

	private static WebSocketUtils chatclient = null;
	
	public static class BitcoinHQResponse {
		private String ch;
		
		private String code;
		
		private long ts;
		
		private double amount;
		
		private long open;
		
		private long close;
		
		private long high;
		
		private long low;
		
		private long tick_ts;
		
		private long id;
		
		private long count;
		
		private double vol;
		
		public String getCh() {
			return ch;
		}

		public long getTs() {
			return ts;
		}

		public double getAmount() {
			return amount;
		}

		public long getOpen() {
			return open;
		}

		public long getClose() {
			return close;
		}

		public long getHigh() {
			return high;
		}

		public long getLow() {
			return low;
		}

		public long getTick_ts() {
			return tick_ts;
		}

		public long getId() {
			return id;
		}

		public long getCount() {
			return count;
		}

		public double getVol() {
			return vol;
		}
		
		public String getCode() {
			return this.code;
		}

		static String getCodeByCh(String ch) {
			if (ch.equals(detail_ltccny)) {
				return "LTC";
			} else if (ch.equals(detail_btccny)){
				return "BTC";
			} else {
				return "unknown";
			}
		}
		
		static int getTimesByTarget(String target) {
			if (target.equals("BTC")) {
				return 1;
			} else {
				return 100;
			}
		}
		
		public static BitcoinHQResponse parseFromJsonSource(String jsonStr) {
			Map<String, Object> map = (Map<String, Object>) JsonUtil.convertStringToObject(jsonStr);
			if (map == null) {
				return null;
			} else {
				BitcoinHQResponse response = new BitcoinHQResponse();
				String ch = (String)map.get("ch");
				long ts = ((Double)map.get("ts")).longValue();
				response.ch = ch;
				response.ts = ts;
				
				Map<String, Object> tick = (Map)map.get("tick");
				response.code = getCodeByCh(response.ch);
				int times = getTimesByTarget(response.code);
				response.amount = (double)tick.get("amount");
				response.open = (int)((double)tick.get("open") * times);
				response.close = (int)((double)tick.get("close") * times);
				response.high = (int)((double)tick.get("high") * times);
				response.tick_ts = ((Double)tick.get("ts")).longValue();
				response.id = ((Double)tick.get("id")).longValue();
				response.count = ((Double)tick.get("count")).longValue();
				response.low = (int)((double)tick.get("low") * times);
				response.vol = (double)tick.get("vol");
				
				return response;
			}
		}
	}

	public WebSocketUtils(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public WebSocketUtils(URI serverURI) {
		super(serverURI);
		this.cache = new GCache<String, String>(100, 30);
	}

	public WebSocketUtils(URI serverUri, Map<String, String> headers, int connecttimeout) {
		super(serverUri, new Draft_17(), headers, connecttimeout);
		this.cache = new GCache<String, String>(100, 30);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("开流--opened connection");
	}

	@Override
	public void onMessage(ByteBuffer socketBuffer) {
		try {
			String marketStr = CommonUtils.byteBufferToString(socketBuffer);
			String market = CommonUtils.uncompress(marketStr);
			if (market.contains("ping")) {
				System.out.println(new Date().toString() + " " + market.replace("ping", "pong"));
				chatclient.send(market.replace("ping", "pong"));
			} else {
				System.out.println(new Date().toString() + " market:" + market);
				saveIndexData(market);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveIndexData(String str) {
//		if (Strings.isNullOrEmpty(this.cache.get(str))) {
			//directly enqueue the data
			KestrelConnector.enqueue(str);
			this.cache.put(str, "OK");
			//invoke http service
			//HttpClient.get(CrawlerConfig.get("price.update.url") + "?data="+body);
			
			// instead of invoking the http service, we save the data into db
//			Map<String, Object> map = (Map<String, Object>) JsonUtil.convertStringToObject(str);
			BitcoinHQResponse response = BitcoinHQResponse.parseFromJsonSource(str);
			if (response != null) {
				CloudTargetIndexChange change = new CloudTargetIndexChange();
				
				change.setSubject(response.getCode());
				change.setTime(new Timestamp(response.getTick_ts()));
				change.setIndex(response.getClose());
				
				try {
					IndexChangeHandler.saveData(change);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
//		}
	}
	

	@Override
	public void onMessage(String message) {
		System.out.println("接收--received: " + message);
	}

	public void onFragment(Framedata fragment) {
		System.out.println("片段--received fragment: " + new String(fragment.getPayloadData().array()));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("关流--Connection closed by " + (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception ex) {
		System.out.println("WebSocket 连接异常: " + ex);
	}

	public static Map<String, String> getWebSocketHeaders() throws IOException {
		Map<String, String> headers = new HashMap<String, String>();
		return headers;
	}

	private static void trustAllHosts(WebSocketUtils appClient) {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			appClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sc));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class SyncTask extends TimerTask {
		@Override
		public void run() {
				try {
					if (chatclient != null) {
						System.out.println(new Date().toString() + " closing the current websocket connection.");
						chatclient.closeBlocking();
					}
					chatclient = new WebSocketUtils(new URI(url), getWebSocketHeaders(), 1000);
					trustAllHosts(chatclient);					
					chatclient.connectBlocking();
					// 订阅数据深度
					SubModel subModel1 = new SubModel();
					subModel1.setSub("market.btccny.detail");
					subModel1.setId(10001L);
					chatclient.send(JSONObject.toJSONString(subModel1));

					SubModel subModel2 = new SubModel();
					subModel2.setSub("market.ltccny.detail");
					subModel2.setId(10002L);
					chatclient.send(JSONObject.toJSONString(subModel2));	
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		} 
	}
	
	public static void executeWebSocket() throws Exception {
		Timer timer = new Timer();
		SyncTask task = new WebSocketUtils.SyncTask();
		timer.scheduleAtFixedRate(task, 100, 180 * 1000);
/*		chatclient = new WebSocketUtils(new URI(url), getWebSocketHeaders(), 1000);
		trustAllHosts(chatclient);
		chatclient.connectBlocking();

		SubModel subModel1 = new SubModel();
		subModel1.setSub("market.btccny.detail");
		subModel1.setId(10001L);
		chatclient.send(JSONObject.toJSONString(subModel1));
		
		SubModel subModel2 = new SubModel();
		subModel2.setSub("market.ltccny.detail");
		subModel2.setId(10002L);
		chatclient.send(JSONObject.toJSONString(subModel2));	*/
	}
	
	public static void main(String[] args) {
		System.out.println(new Date().toString());
	}
	
	
}