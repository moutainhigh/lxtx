package com.lxtech.cloudtrade.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import com.lxtech.cloudtrade.net.CloudNetPackage;
import com.lxtech.cloudtrade.net.CloudPackageHandler;
import com.lxtech.cloudtrade.net.CloudPackageHeader;

public class WebSocketMessageInbound extends MessageInbound {

	private String name;
	//当前连接的用户名称
//	private final String user;
	
	private final int connId;
	
	//user identifier for current connection
	private String userId;
	
	public void setUserId(String userId) {
		this.userId = userId;
		this.wsPool.setUserConnMapping(this.userId, this.connId);
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public String getName() {
		return this.name;
	}
	
	private CloudPackageHandler packageHandler;
	
	private WebSocketMessageInboundPool wsPool;

	public WebSocketMessageInbound(String name, int connId) {
		this.name = name;
		this.connId = connId;
		this.packageHandler = new CloudPackageHandler(this);
		this.wsPool = WebSocketInboundPoolFactory.getWebSocketMessageInboundPool(this.name);
	}

	public int getConnId() {
		return this.connId;
	}

	//建立连接的触发的事件
	@Override
	protected void onOpen(WsOutbound outbound) {
		JSONObject result = new JSONObject();
		result.element("type", "get_online_user");
		result.element("list", this.wsPool.getOnlineUser());
		//向连接池添加当前的连接对象
		this.wsPool.addMessageInbound(this);
		//向当前连接发送当前在线用户的列表
		this.wsPool.sendMessageToClient(this.connId, result.toString());
	}

	@Override
	protected void onClose(int status) {
		// 触发关闭事件，在连接池中移除连接
		this.wsPool.removeMessageInbound(this);
	}

	@Override
	protected void onBinaryMessage(ByteBuffer message) throws IOException {
//		throw new UnsupportedOperationException("Binary message not supported.");
		String msg = new String(message.array());
		CloudPackageHeader header = CloudPackageHeader.parseRequestHeader(msg);
		String response = this.packageHandler.getResponsePackage(CloudNetPackage.parseRequest(msg));
		this.wsPool.sendMessageToClient(this.connId, response);
	}

	//客户端发送消息到服务器时触发事件
	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
		//向所有在线用户发送消息
		this.wsPool.sendMessage(message.toString());
	}
}
