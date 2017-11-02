package com.lxtx.settlement.service;

import com.google.gson.Gson;
import com.lxtx.settlement.model.protocol.BaseProtocol;
import com.lxtx.settlement.model.protocol.ProtocolSettlement;

public class ProtocolUtil {

	public static final int PROTOCOL_SETTLEMENT = 2003;

	public static BaseProtocol parseProtocal(String jsonStr) {
		Gson gson = new Gson();
		BaseProtocol baseProtocol = gson.fromJson(jsonStr, BaseProtocol.class);
		switch (baseProtocol.getProtocol()) {
		case PROTOCOL_SETTLEMENT:
			baseProtocol = gson.fromJson(jsonStr, ProtocolSettlement.class);
			break;
		}
		return baseProtocol;
	}

	public static String makeLoginMessage(int userId) {// 机器人系统登录，好像没有必要单独做，每有一个机器人登录刷新一次服务器状态，好像也可以满足要求
		// protocol:C2S_PROTOCOL_LOGIN,
		// cookie:''
		Gson gson = new Gson();
		return "";// gson.toJson(new C2SProtocolLogin(userId));
	}
}
