package com.lxtx.settlement.config;

public class SettlementQueueConfig {
	public static final String SERVER_ADDRESS = "139.129.227.199";
	public static final int SERVER_PORT = 22134;

	public static final String DICE_SETTLEMENT_OPER_QUEUE = "main_seq_out";
	public static final String DICE_SETTLEMENT_CTRL_QUEUE = "settlement_control";

	public static final String ANIMAL_SETTLEMENT_OPER_QUEUE = "animal_main_seq_out";
	public static final String ANIMAL_SETTLEMENT_CTRL_QUEUE = "animal_settlement_control";

	public static final String CAR_SETTLEMENT_OPER_QUEUE = "car_main_seq_out";
	public static final String CAR_SETTLEMENT_CTRL_QUEUE = "car_settlement_control";

}
