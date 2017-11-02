package com.lxtx.settlement.model.protocol;

public class ProtocolSettlement extends BaseProtocol {
	private int state;
	private int interval;
	private int groupId;

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

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}