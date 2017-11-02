package com.lxtx.robot.model.protocol;

import com.lxtx.robot.service.ProtocolUtil;

public class C2SProtocolSetChips extends BaseProtocol {
	private int userId;
	private int lotteryIndex;
	private int num;

	public C2SProtocolSetChips(int userId, int lotteryIndex, int num) {
		super(ProtocolUtil.C2S_PROTOCOL_SET_CHIPS);
		this.userId = userId;
		this.lotteryIndex = lotteryIndex;
		this.num = num;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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
