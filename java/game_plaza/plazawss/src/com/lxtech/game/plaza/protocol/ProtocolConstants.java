package com.lxtech.game.plaza.protocol;

/**
 * This interface provides definitions for the message types
 * 
 * @author wangwei
 */
public interface ProtocolConstants {

	public enum GAME_ROUND_STATE {
		ROOM_STATE_WAITING_FOR_MASTER, ROOM_STATE_WAITING_FOR_START, ROOM_STATE_WAITING_FOR_SET_CHIPS, ROOM_STATE_WAITING_FOR_CACLULATE
	}

	/**
	 * the protocol of server to client
	 */
	int S2C_PROTOCOL_SETTED_CHIPS = 2002;

	int S2C_PROTOCOL_GAME_STATE = 2003;

	int S2C_PROTOCOL_REQUEST_MASTER = 2004;

	int S2C_PROTOCOL_LAST_RESULT = 2005;

	int S2C_PROTOCOL_TE_MA = 2006; // 改为遗漏值记录

	int S2C_PROTOCOL_SET_CHIPS = 2007;

	int S2C_PROTOCOL_MASTER_LIST = 2008;

	int S2C_PROTOCOL_GET_OR_SAVE_MONEY = 2009;

	int S2C_PROTOCOL_RESULT = 2010;

	int S2C_PROTOCOL_QUERY_OPEN_HISTORY = 2011;

	int S2C_PROTOCOL_QUERY_SET_HISTORY = 2012;

	int S2C_PROTOCOL_CHAT = 2013;

	int S2C_PROTOCOL_QUERY_YILOU_TIPS = 2014;//{"yilou_pos_list":[6],"protocol":2014}

	int S2C_PROTOCOL_QUERY_GAME_SETTING = 2015;

	int S2C_PROTOCOL_HINT = 2016;

	int S2C_PROTOCOL_NOTIFYCATION = 2017;

	int S2C_PROTOCOL_FORBID = 2018;

	int S2C_PROTOCOL_RELIEF = 2019;

	int S2C_PROTOCOL_CONTINE_SUCC = 2020;

	int S2C_PROTOCOL_TICKOUT = 2021;

	/**
	 * the protocol of client to server
	 */
	int C2S_PROTOCOL_GAME_STATE = 1000;
	
	int C2S_PROTOCOL_LOGIN = 1002;

	int C2S_PROTOCOL_REQUEST_MASTER = 1102;
//doable
	int C2S_PROTOCOL_SET_CHIPS = 1103;
	// doable	done
	int C2S_PROTOCOL_CHAT = 1104;

	int C2S_PROTOCOL_GET_MASTER_LIST = 1105;
	// doable	done
	int C2S_PROTOCOL_GET_OR_SAVE_MONEY = 1106;

	int C2S_PROTOCOL_CONTINE = 1107;

	int C2S_PROTOCOL_SET_GAME_SETTING = 1108;
	// doable
	int C2S_PROTOCOL_RELIEF = 1109;

	int C2S_PROTOCOL_TICK = 1110;
	//for car
	int C2S_PROTOCOL_TICK_2 = 1111;
	// doable	done?
	int C2S_PROTOCOL_QUERY_OPEN_HISTORY = 1602;

	int C2S_PROTOCOL_QUERY_SET_HISTORY = 1603;
	// doable
	int C2S_PROTOCOL_QUERY_YILOU_TIPS = 1604;//{"protocol":1604}

	int C2S_PROTOCOL_QUERY_GAME_SETTING = 1605;
	
	static final String NO_MASTER = "当前无人上庄";

	public static void main(String[] args) {
		System.out.println(GAME_ROUND_STATE.ROOM_STATE_WAITING_FOR_CACLULATE.ordinal());
	}
}
