package com.lxtech.game.plaza.timeline;

import java.util.List;

public class GameRound {
	// 当前的局数
	private int roundNo = 0;
	// 当前所处状态
	private int step;
	// 启动时间
	private long startTime;
	// 庄家id
	private long masterId;
	// 上庄队列
	private List<Long> masterBackupList;
	// 连庄次数
	private int remainCount;
	// 分数
	private long score;
	//所携带的金币数
	private long coins;
	//已下注的统计,index从0到34，对应下注index为1到35
	private long[] chipstat;

	public int getRoundNo() {
		return roundNo;
	}

	public void setRoundNo(int roundNo) {
		this.roundNo = roundNo;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getMasterId() {
		return masterId;
	}

	public void setMasterId(long masterId) {
		this.masterId = masterId;
	}

	public List<Long> getMasterBackupList() {
		return masterBackupList;
	}

	public void setMasterBackupList(List<Long> masterBackupList) {
		this.masterBackupList = masterBackupList;
	}

	public int getRemainCount() {
		return remainCount;
	}

	public void setRemainCount(int remainCount) {
		this.remainCount = remainCount;
	}

	public long getCoins() {
		return coins;
	}

	public void setCoins(long coins) {
		this.coins = coins;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public long[] getChipstat() {
		return chipstat;
	}

	public void setChipstat(long[] chipstat) {
		this.chipstat = chipstat;
	}
	
	
}
