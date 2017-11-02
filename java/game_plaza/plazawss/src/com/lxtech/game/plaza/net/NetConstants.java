package com.lxtech.game.plaza.net;

public interface NetConstants {
	static final String CHIP_QUEUE = "chip_oper";
	
	static final String BOT_CTRL_QUEUE = "bot_control";
	
	static final String SETTLEMENT_CTRL_QUEUE = "settlement_control";
	
	static final String MAIN_SEQ_QUEUE_IN = "main_seq_in";
	
	static final String MAIN_SEQ_QUEUE_OUT = "main_seq_out";
	
	static final String CAR_CHIP_QUEUE = "car_chip_oper";
	
	static final String CAR_BOT_CTRL_QUEUE = "car_bot_control";
	
	static final String CAR_SETTLEMENT_CTRL_QUEUE = "car_settlement_control";
	
	static final String CAR_MAIN_SEQ_QUEUE_IN = "car_main_seq_in";
	
	static final String CAR_MAIN_SEQ_QUEUE_OUT = "car_main_seq_out";
	
	static final String ANIMAL_CHIP_QUEUE = "animal_chip_oper";
	
	static final String ANIMAL_BOT_CTRL_QUEUE = "animal_bot_control";
	
	static final String ANIMAL_SETTLEMENT_CTRL_QUEUE = "animal_settlement_control";
	
	static final String ANIMAL_MAIN_SEQ_QUEUE_IN = "animal_main_seq_in";
	
	static final String ANIMAL_MAIN_SEQ_QUEUE_OUT = "animal_main_seq_out";	
	
	static final int REQEST_MASTER_STATE_NOT_IN_START_STATE = 0;

	static final int REQEST_MASTER_STATE_BE_SETTED_BY_OTHER = 1;

	static final int REQEST_MASTER_STATE_NOT_ENOUGH_CHIPS = 2;

	static final int REQEST_MASTER_STATE_SUCCESS = 3;

	static final int REQEST_MASTER_STATE_INFOMATION = 4;

	static final int REQEST_MASTER_STATE_DOWN_SUCCESS = 5;
	
	static final int REQEST_MASTER_NO_SUCH_USER = 6;
	
	static final int ROOM_STATE_WAITING_FOR_MASTER = 0;

	static final int ROOM_STATE_WAITING_FOR_START = 1;

	static final int ROOM_STATE_WAITING_FOR_SET_CHIPS = 2;

	static final int ROOM_STATE_WAITING_FOR_CACLULATE = 3;

	static final int ROOM_STATE_WAITING_FOR_SERVER = 4;	
	
	static final int REQUEST_MASTER_MIN_COINS = 10000000;
	
	static final int GAME_STATUS_PLAYER = 1;
	
	static final int GAME_STATUS_IN_QUEUE = 2;
	
	static final int GAME_STATUS_MASTER = 3;
	
	static final int GAME_ROOM_CAR = 1;
	
	static final int GAME_ROOM_ANIMAL = 2;
	
	static final int GAME_ROOM_DICE = 3;
}
