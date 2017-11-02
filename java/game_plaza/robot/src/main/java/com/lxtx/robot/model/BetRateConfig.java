package com.lxtx.robot.model;

import java.util.List;

public class BetRateConfig {
	private int bet_delay_time_min;
	private int bet_delay_time_max;
	private int bet_count_min;
	private int bet_count_max;
	private List<BetTimeRateConfig> special;

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

	public List<BetTimeRateConfig> getSpecial() {
		return special;
	}

	public void setSpecial(List<BetTimeRateConfig> special) {
		this.special = special;
	}
}