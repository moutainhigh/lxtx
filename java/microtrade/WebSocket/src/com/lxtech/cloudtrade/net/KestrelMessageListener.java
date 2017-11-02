package com.lxtech.cloudtrade.net;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lxtech.cloudtrade.util.JsonUtil;
import com.lxtech.cloudtrade.websocket.WebSocketInboundPoolFactory;
import com.lxtech.cloudtrade.websocket.WebSocketMessageInboundPool;

public class KestrelMessageListener implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(KestrelMessageListener.class);
	
	private String queueName;
	
	private String webSocketPoolName;
	
	private WebSocketMessageInboundPool wsPool;
	
	public KestrelMessageListener(String queueName) {
		this.queueName = queueName;
		this.wsPool = WebSocketInboundPoolFactory.getWebSocketMessageInboundPool(this.queueName);
	}
	
	public KestrelMessageListener(String queueName, String wsPoolName) {
		// TODO Auto-generated constructor stub
		this.webSocketPoolName = wsPoolName;
		this.queueName = queueName;
		this.wsPool = WebSocketInboundPoolFactory.getWebSocketMessageInboundPool(this.webSocketPoolName);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				String message = KestrelConnector.dequeue(this.queueName);
				if (!Strings.isNullOrEmpty(message)) {
					logger.info("retrieved message:" + message);
				}
				
				if (!Strings.isNullOrEmpty(message) && message.contains(CloudNetPackage.PACKAGE_SPLITTER)) {
					String[] splitted = message.split(CloudNetPackage.PACKAGE_SPLITTER);
					
					CloudPackageHeader header = CloudPackageHeader.parseResponseHeader(splitted[0]);
					if (!Strings.isNullOrEmpty(header.getType()) &&Integer.valueOf(header.getType()).intValue() == CloudPackageType.MSG_TYPE_SERPushPrice && !Strings.isNullOrEmpty(message) && !message.contains("ErrMsg")) {
						this.wsPool.sendMessage(message);
					}
					logger.info("finished sending message to ws clients.");
					/*else if (!Strings.isNullOrEmpty(header.getType()) &&Integer.valueOf(header.getType()).intValue() == CloudPackageType.MSG_TYPE_News) {
						Map requestObj = (Map)JsonUtil.convertStringToObject(splitted[1]);
						String wxid = (String)requestObj.get("wxid"); 
						if (!Strings.isNullOrEmpty(wxid) && !wxid.equals("undefined")) {
							Integer connId = wsPool.getConnIdForUser(wxid);
							if (connId != null) {
								wsPool.sendMessageToClient(connId, message);
							}
						}
					}*/
				} 
				Thread.sleep(200);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}
