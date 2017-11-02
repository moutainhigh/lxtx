package com.lxtx.robot.model.protocol;

public class S2CProtocolGameState extends BaseProtocol {

	private int state;
	private int interval;//剩余时间

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
