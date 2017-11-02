package com.lxtech.cloudtrade.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sun.media.jfxmedia.logging.Logger;

public class WebSocketMessageInboundPool {

	private String poolName;
	
	public WebSocketMessageInboundPool(String name) {
		this.poolName = name;
		this.es = Executors.newFixedThreadPool(10);
	}
	
	private ExecutorService es;
	//保存连接的MAP容器
	private final Map<String, WebSocketMessageInbound > connections = new ConcurrentHashMap<String,WebSocketMessageInbound>();
	//保存用户id和连接id的映射
	private final Map<String, Integer> userConnMap = new HashMap<String, Integer>(); 
	
	public Integer getConnIdForUser(String userId) {
		return userConnMap.get(userId);
	}
	
	public void setUserConnMapping(String wxid, int connId) {
		userConnMap.put(wxid, connId);
	}
	
	//向连接池中添加连接
	public void addMessageInbound(WebSocketMessageInbound inbound){
		//添加连接
		System.out.println("client : " + inbound.getConnId() + " joined..");
		connections.put(inbound.getConnId()+"", inbound);
	}
	
	//获取所有的在线用户
	public Set<String> getOnlineUser(){
		return connections.keySet();
	}
	
	public void removeMessageInbound(WebSocketMessageInbound inbound){
		//移除连接
		System.out.println("user : " + inbound.getConnId() + " exit..");
		connections.remove(inbound.getConnId()+"");
		userConnMap.remove(inbound.getUserId());
	}
	
	public void sendMessageToClient(int connId, String message){
		//向特定的用户发送数据
		WebSocketMessageInbound inbound = connections.get(connId + "");
		if(inbound != null){
//				inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
			try {
				inbound.getWsOutbound().writeBinaryMessage(ByteBuffer.wrap(message.getBytes()));
				inbound.getWsOutbound().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//向所有的用户发送消息
	public void sendMessage(String message){
		Set<String> keySet = connections.keySet();
		for (String key : keySet) {
			WebSocketMessageInbound inbound = connections.get(key);
			if(inbound != null) {
/*				Future<Boolean> futureTask = this.es.submit(new WriteTask(inbound, message));
				try {
					//if a task hasn't finished in 5secs, then cancel it
					Boolean result = futureTask.get(5000, TimeUnit.MILLISECONDS);
				} catch (TimeoutException | InterruptedException | ExecutionException e) {
					System.out.println("No response after one second");
					futureTask.cancel(true);
				} */
				this.es.submit(new WriteTask(inbound, message));
			}
		}
	}
	
	private class WriteTask implements Runnable {
		private WebSocketMessageInbound inbound;
		
		private String message;
		
		public WriteTask(WebSocketMessageInbound inbound, String message) {
			this.inbound = inbound;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				this.inbound.getWsOutbound().writeBinaryMessage(ByteBuffer.wrap(message.getBytes()));
				this.inbound.getWsOutbound().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
