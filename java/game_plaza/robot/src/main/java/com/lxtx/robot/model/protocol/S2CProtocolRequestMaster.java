package com.lxtx.robot.model.protocol;

import java.util.List;

public class S2CProtocolRequestMaster extends BaseProtocol {
	private S2CUserInfo user;
	private int state;
	private int remainCount;
	private int score;

	public S2CUserInfo getUser() {
		return user;
	}

	public void setUser(S2CUserInfo user) {
		this.user = user;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getRemainCount() {
		return remainCount;
	}

	public void setRemainCount(int remainCount) {
		this.remainCount = remainCount;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
