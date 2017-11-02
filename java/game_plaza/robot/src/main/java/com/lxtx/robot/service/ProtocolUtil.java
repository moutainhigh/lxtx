package com.lxtx.robot.service;

import com.google.gson.Gson;
import com.lxtx.robot.model.protocol.BaseProtocol;
import com.lxtx.robot.model.protocol.C2SProtocolLogin;
import com.lxtx.robot.model.protocol.C2SProtocolRequestMaster;
import com.lxtx.robot.model.protocol.C2SProtocolSetChips;
import com.lxtx.robot.model.protocol.S2CProtocolGameState;
import com.lxtx.robot.model.protocol.S2CProtocolHint;
import com.lxtx.robot.model.protocol.S2CProtocolLogin;
import com.lxtx.robot.model.protocol.S2CProtocolMasterList;
import com.lxtx.robot.model.protocol.S2CProtocolRequestMaster;
import com.lxtx.robot.model.protocol.S2CProtocolSetChips;
import com.lxtx.robot.model.protocol.S2CProtocolSettedChips;

public class ProtocolUtil {

	//?public static final int S2C_PROTOCOL_LOGIN = 2001;
	public static final int S2C_PROTOCOL_SETTED_CHIPS = 2002;
	public static final int S2C_PROTOCOL_GAME_STATE = 2003;
	public static final int S2C_PROTOCOL_REQUEST_MASTER = 2004;
	// public static final int S2C_PROTOCOL_LAST_RESULT = 2005;
	// public static final int S2C_PROTOCOL_TE_MA = 2006; // 改为遗漏值记录
	public static final int S2C_PROTOCOL_SET_CHIPS = 2007;
	public static final int S2C_PROTOCOL_MASTER_LIST = 2008;
	// public static final int S2C_PROTOCOL_GET_OR_SAVE_MONEY = 2009;
	// public static final int S2C_PROTOCOL_RESULT = 2010;
	// public static final int S2C_PROTOCOL_QUERY_OPEN_HISTORY = 2011;
	// public static final int S2C_PROTOCOL_QUERY_SET_HISTORY = 2012;
	// public static final int S2C_PROTOCOL_CHAT = 2013;
	// public static final int S2C_PROTOCOL_QUERY_YILOU_TIPS = 2014;
	// public static final int S2C_PROTOCOL_QUERY_GAME_SETTING = 2015;
	//?public static final int S2C_PROTOCOL_HINT = 2016;
	// public static final int S2C_PROTOCOL_NOTIFYCATION = 2017;
	// public static final int S2C_PROTOCOL_FORBID = 2018;
	// public static final int S2C_PROTOCOL_RELIEF = 2019;
	// public static final int S2C_PROTOCOL_CONTINE_SUCC = 2020;
	// public static final int S2C_PROTOCOL_TICKOUT = 2021;

	public static final int C2S_REQUEST_GAME_STATE = 1000;
	//?public static final int C2S_PROTOCOL_LOGIN = 1002;
	public static final int C2S_PROTOCOL_REQUEST_MASTER = 1102;
	public static final int C2S_PROTOCOL_SET_CHIPS = 1103;
	// var C2S_PROTOCOL_CHAT = 1104;
	public static final int C2S_PROTOCOL_GET_MASTER_LIST = 1105;
	// var C2S_PROTOCOL_GET_OR_SAVE_MONEY = 1106;
	// var C2S_PROTOCOL_CONTINE = 1107;
	// var C2S_PROTOCOL_SET_GAME_SETTING = 1108;
	// var C2S_PROTOCOL_RELIEF = 1109;
	// var C2S_PROTOCOL_TICK = 1110;

	// var C2S_PROTOCOL_QUERY_OPEN_HISTORY = 1602;
	// var C2S_PROTOCOL_QUERY_SET_HISTORY = 1603;
	// var C2S_PROTOCOL_QUERY_YILOU_TIPS = 1604;
	// var C2S_PROTOCOL_QUERY_GAME_SETTING = 1605;

	public static BaseProtocol parseProtocal(String jsonStr) {
		Gson gson = new Gson();
		BaseProtocol baseProtocol = gson.fromJson(jsonStr, BaseProtocol.class);
		switch (baseProtocol.getProtocol()) {
//		case S2C_PROTOCOL_LOGIN:
//			baseProtocol = gson.fromJson(jsonStr, S2CProtocolLogin.class);
//			break;
		case S2C_PROTOCOL_SETTED_CHIPS:
			baseProtocol = gson.fromJson(jsonStr, S2CProtocolSettedChips.class);
			break;
		case S2C_PROTOCOL_GAME_STATE:
			baseProtocol = gson.fromJson(jsonStr, S2CProtocolGameState.class);
			break;
		case S2C_PROTOCOL_REQUEST_MASTER:
			baseProtocol = gson.fromJson(jsonStr, S2CProtocolRequestMaster.class);
			break;
		case S2C_PROTOCOL_SET_CHIPS:
			baseProtocol = gson.fromJson(jsonStr, S2CProtocolSetChips.class);
			break;
		case S2C_PROTOCOL_MASTER_LIST:
			baseProtocol = gson.fromJson(jsonStr, S2CProtocolMasterList.class);
			break;
//		case S2C_PROTOCOL_HINT:
//			baseProtocol = gson.fromJson(jsonStr, S2CProtocolHint.class);
//			break;
		}
		return baseProtocol;
	}
	
	public static String makeRequestGameStateMessage(){
		Gson gson = new Gson();
		return gson.toJson(new BaseProtocol(C2S_REQUEST_GAME_STATE));
	}

	public static String makeLoginMessage(int userId) {// 机器人系统登录，好像没有必要单独做，每有一个机器人登录刷新一次服务器状态，好像也可以满足要求
		// protocol:C2S_PROTOCOL_LOGIN,
		// cookie:''
		Gson gson = new Gson();
		return gson.toJson(new C2SProtocolLogin(userId));
	}

	public static String makeLogoutMessage(int userId) {
		return "";
	}

	public static String makeRequestMasterMessage(int userId, boolean up) {
		// protocol:C2S_PROTOCOL_REQUEST_MASTER,
		// up:1 //1上庄， 0下庄
		Gson gson = new Gson();
		return gson.toJson(new C2SProtocolRequestMaster(userId, up ? 1 : 0));
	}

	public static String makeSetChipsMessage(int userId, int lotteryIndex, int num) {
		// protocol:C2S_PROTOCOL_SET_CHIPS,
		// lotteryIndex:1,
		// num:10000
		Gson gson = new Gson();
		return gson.toJson(new C2SProtocolSetChips(userId, lotteryIndex, num));
	}

	public static String makeGetMasterList() {
		// protocol:C2S_PROTOCOL_GET_MASTER_LIST
		Gson gson = new Gson();
		return gson.toJson(new BaseProtocol(C2S_PROTOCOL_GET_MASTER_LIST));
	}
}
