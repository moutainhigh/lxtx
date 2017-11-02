package com.lxtech.game.plaza.db.model;

//{"result_1_num":5,"result_2_num":4,"result_3_num":3,"win_num":0,"setted_num":0,"master_score":-473060,"open_index":59547,"protocol":2010}
public class RoundDisclosure {
	private int result_1_num;

	private int result_2_num;

	private int result_3_num;

	private long win_num;

	private long setted_num;

	private long master_score;

	private long open_index;

	private int protocol;

	public int getResult_1_num() {
		return result_1_num;
	}

	public void setResult_1_num(int result_1_num) {
		this.result_1_num = result_1_num;
	}

	public int getResult_2_num() {
		return result_2_num;
	}

	public void setResult_2_num(int result_2_num) {
		this.result_2_num = result_2_num;
	}

	public int getResult_3_num() {
		return result_3_num;
	}

	public void setResult_3_num(int result_3_num) {
		this.result_3_num = result_3_num;
	}

	public long getMaster_score() {
		return master_score;
	}

	public void setMaster_score(long master_score) {
		this.master_score = master_score;
	}

	public long getOpen_index() {
		return open_index;
	}

	public void setOpen_index(long open_index) {
		this.open_index = open_index;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
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

}
