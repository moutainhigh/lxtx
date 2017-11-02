package com.lxtx.robot.model;

public class BetTimeRateConfig {
	private String time_begin;
	private String time_end;
	private int bet_delay_time_min;
	private int bet_delay_time_max;
	private int bet_count_min;
	private int bet_count_max;

	public String getTime_begin() {
		return time_begin;
	}

	public void setTime_begin(String time_begin) {
		this.time_begin = time_begin;
	}

	public String getTime_end() {
		return time_end;
	}

	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}

	public int getBet_delay_time_min() {
		return bet_delay_time_min;
	}

	public void setBet_delay_time_min(int bet_delay_time_min) {
		this.bet_delay_time_min = bet_delay_time_min;
	}

	public int getBet_delay_time_max() {
		return bet_delay_time_max;
	}

	public void setBet_delay_time_max(int bet_delay_time_max) {
		this.bet_delay_time_max = bet_delay_time_max;
	}

	public int getBet_count_min() {
		return bet_count_min;
	}

	public void setBet_count_min(int bet_count_min) {
		this.bet_count_min = bet_count_min;
	}

	public int getBet_count_max() {
		return bet_count_max;
	}

	public void setBet_count_max(int bet_count_max) {
		this.bet_count_max = bet_count_max;
	}
}
