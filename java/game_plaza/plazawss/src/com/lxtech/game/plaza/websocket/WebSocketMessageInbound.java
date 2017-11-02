package com.lxtech.game.plaza.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.game.plaza.net.NetConstants;
import com.lxtech.game.plaza.protocol.AbstractGamePacketHandler;
import com.lxtech.game.plaza.protocol.impl.AnimalPacketHandler;
import com.lxtech.game.plaza.protocol.impl.CarPacketHandler;
import com.lxtech.game.plaza.protocol.impl.DicePacketHandler;

public class WebSocketMessageInbound extends MessageInbound {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageInbound.class);
	
	private String name;
	
	private final int connId;
	
	//user identifier for current connection
	private long userId;
	
	public void setUserId(long userId) {
		this.userId = userId;
		this.wsPool.setUserConnMapping(userId, connId);
	}
	
	public long getUserId() {
		return this.userId;
	}
	
	public String getName() {
		return this.name;
	}
	
	private WebSocketMessageInboundPool wsPool;
	
	private AbstractGamePacketHandler packetHandler;

	public WebSocketMessageInbound(String name, int connId) {
		this.name = name;
		this.connId = connId;
		this.wsPool = WebSocketInboundPoolFactory.getWebSocketMessageInboundPool(this.name);
		this.packetHandler = this.getPacketHandler();//new DicePacketHandler(this.wsPool, this);
	}
	
	public int getConnId() {
		return this.connId;
	}
	
	private AbstractGamePacketHandler getPacketHandler() {
		if (this.name.equals(NetConstants.CHIP_QUEUE)) { //dice game
			return new DicePacketHandler(this.wsPool, this);
		} else if(this.name.equals(NetConstants.CAR_CHIP_QUEUE)) {
			return new CarPacketHandler(this.wsPool, this);
		} else if(this.name.equals(NetConstants.ANIMAL_CHIP_QUEUE)){
			return new AnimalPacketHandler(this.wsPool, this);
		} else {
			return null;
		}
	}

	//建立连接的触发的事件
	@Override
	protected void onOpen(WsOutbound outbound) {
		JSONObject result = new JSONObject();
		//向连接池添加当前的连接对象
		this.wsPool.addMessageInbound(this);
	}

	@Override
	protected void onClose(int status) {
		// 触发关闭事件，在连接池中移除连接
		this.wsPool.removeMessageInbound(this);
	}

	@Override
	protected void onBinaryMessage(ByteBuffer message) throws IOException {
		String msg = new String(message.array());
		this.wsPool.sendMessageToClient(this.connId, "");
	}

	//客户端发送消息到服务器时触发事件
	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
		//向所有在线用户发送消息
		String msg = message.toString();
		//this.wsPool.sendMessage(msg);
		List<String> responseList = this.packetHandler.handleGameRequest(msg);
		if (responseList != null && responseList.size() > 0) {
			for (String response : responseList) {
				logger.info("send response:"+response + "   "  + System.currentTimeMillis());
				this.wsPool.sendMessageToClient(this.connId, response);
			}
		}
	}
}
