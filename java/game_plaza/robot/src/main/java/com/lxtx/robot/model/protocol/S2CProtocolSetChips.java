package com.lxtx.robot.model.protocol;

import java.util.List;

public class S2CProtocolSetChips extends BaseProtocol {
	private int code;
	private String errMsg;
	private S2CUserInfo user;
	private int lotteryIndex;
	private int num;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public S2CUserInfo getUser() {
		return user;
	}

	public void setUser(S2CUserInfo user) {
		this.user = user;
	}

	public int getLotteryIndex() {
		return lotteryIndex;
	}

	public void setLotteryIndex(int lotteryIndex) {
		this.lotteryIndex = lotteryIndex;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
