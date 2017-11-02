package com.lxtech.game.plaza.protocol;

import java.util.List;

import com.lxtech.game.plaza.websocket.WebSocketMessageInbound;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

/**
 * abstract packet handler
 * @author wangwei
 *
 */
public abstract class AbstractGamePacketHandler {
	
	protected WebSocketMessageInboundPool pool;
	
	protected WebSocketMessageInbound inbound;
	
	public AbstractGamePacketHandler(WebSocketMessageInboundPool pool, WebSocketMessageInbound inbound) {
		this.pool = pool;
		this.inbound = inbound;
	}
	
	public AbstractGamePacketHandler(WebSocketMessageInboundPool pool) {
		this.pool = pool;
	}
	
	public abstract List<String> handleGameRequest(String request);
}