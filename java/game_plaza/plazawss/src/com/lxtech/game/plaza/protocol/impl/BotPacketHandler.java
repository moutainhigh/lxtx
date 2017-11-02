package com.lxtech.game.plaza.protocol.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.lxtech.game.plaza.protocol.ProtocolConstants;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

public class BotPacketHandler extends DicePacketHandler{

	public BotPacketHandler(WebSocketMessageInboundPool pool) {
		super(pool);
	}

	@Override
	public List<String> handleGameRequest(String request) {
		long time1 = System.currentTimeMillis();
		System.out.println("in handle game request:" + request);
		Map map = (Map)JsonUtil.convertStringToObject(request);
		int protocol = GameUtil.getMapInfo(map, "protocol");
		
		List<String> responseList = Lists.newArrayList();
		switch (protocol) {
		case ProtocolConstants.C2S_PROTOCOL_SET_CHIPS:
			responseList = handleChipsetReq(map);
			break;
			
		case ProtocolConstants.C2S_PROTOCOL_GAME_STATE:
			responseList = handleGameSettingRequest(map); 
			break;
			
		case ProtocolConstants.C2S_PROTOCOL_GET_MASTER_LIST:
			responseList = handleGetMasterListReq(map);
			break;
		
		case ProtocolConstants.C2S_PROTOCOL_REQUEST_MASTER:
			responseList = handleMasterReq(map);
			break;
			
		default:
			break;
		}
		System.out.println("cost time:" + (System.currentTimeMillis() - time1) + " milli seconds.");
		return responseList;
	}
}
