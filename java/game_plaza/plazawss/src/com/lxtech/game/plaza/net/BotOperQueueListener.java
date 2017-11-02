package com.lxtech.game.plaza.net;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lxtech.game.plaza.protocol.AbstractGamePacketHandler;
import com.lxtech.game.plaza.protocol.impl.BotPacketHandler;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.websocket.WebSocketInboundPoolFactory;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

public class BotOperQueueListener extends AbstractKestrelMessageListener{

	private static final Logger logger = LoggerFactory.getLogger(BotOperQueueListener.class);
	
	private String webSocketPoolName;
	
	private WebSocketMessageInboundPool wsPool;
	
	private AbstractGamePacketHandler packetHandler;
	
	public BotOperQueueListener(String queueName) {
		super(queueName);
		this.wsPool = WebSocketInboundPoolFactory.getWebSocketMessageInboundPool(this.queueName);
		//this.packetHandler = new BotPacketHandler(this.wsPool);
		this.packetHandler = GameUtil.getGamePacketHandler(this.wsPool);
	}
	
	public BotOperQueueListener(String queueName, String wsPoolName) {
		super(queueName);
		this.webSocketPoolName = wsPoolName;
		this.wsPool = WebSocketInboundPoolFactory.getWebSocketMessageInboundPool(this.webSocketPoolName);
		this.packetHandler = GameUtil.getGamePacketHandler(this.wsPool);
	}
	
	@Override
	public void handleReceivedMessage(String message) {
		//TODO
		//check whether the chip could be accepted
		List<String> responseList = this.packetHandler.handleGameRequest(message);
		for (String str : responseList) {
			String queueName = GameUtil.getQueueName(this.wsPool.getPoolName(), NetConstants.BOT_CTRL_QUEUE);
			KestrelConnector.enqueue(queueName, str);
		}
	}
}
