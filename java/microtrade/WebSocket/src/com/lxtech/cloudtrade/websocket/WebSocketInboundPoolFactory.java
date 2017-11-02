package com.lxtech.cloudtrade.websocket;

import java.util.HashMap;
import java.util.Map;

public class WebSocketInboundPoolFactory {

	private static Map<String, WebSocketMessageInboundPool> poolMap = new HashMap<String, WebSocketMessageInboundPool>();
	
	public synchronized static WebSocketMessageInboundPool getWebSocketMessageInboundPool(String name) { 
		if (!poolMap.containsKey(name)) {
			WebSocketMessageInboundPool pool = new WebSocketMessageInboundPool(name);
			poolMap.put(name, pool);
			return pool;
		} else {
			return poolMap.get(name); 
		}
	}
	
}
