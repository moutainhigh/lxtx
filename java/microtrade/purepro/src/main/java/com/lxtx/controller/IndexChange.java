package com.lxtx.controller;

public class IndexChange {
	private String time;
	private int index;
	
	public IndexChange(String time, int index) {
		this.time = time;
		this.index = index;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
