package com.lxtech.game.plaza.db.model;

//{"protocol":1103,"lotteryIndex":1,"num":500}
public class ChipsetRequest {
	private int protocol;
	
	private int lotteryIndex;
	
	private long num;
	
	private long userId;

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public int getLotteryIndex() {
		return lotteryIndex;
	}

	public void setLotteryIndex(int lotteryIndex) {
		this.lotteryIndex = lotteryIndex;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
}
