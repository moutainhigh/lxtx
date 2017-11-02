package com.lxtech.game.plaza.db.model;

/**
 * provides the ds for settlement result of each round
 * This pojo fits all the 3 games 
 * @author wangwei
 *
 */
public class DiceSettlementResult {

	private long user_id;

	private long win_num;

	private long setted_num;

	private int identity;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getWin_num() {
		return win_num;
	}

	public void setWin_num(long win_num) {
		this.win_num = win_num;
	}

	public long getSetted_num() {
		return setted_num;
	}

	public void setSetted_num(long setted_num) {
		this.setted_num = setted_num;
	}

	public int getIdentity() {
		return identity;
	}

	public void setIdentity(int identity) {
		this.identity = identity;
	}

}
