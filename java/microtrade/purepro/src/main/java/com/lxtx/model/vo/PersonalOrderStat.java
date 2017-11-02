package com.lxtx.model.vo;

public class PersonalOrderStat {

	private int total_orders;

	private int total_amount;

	private int hide_times = 0;
	
	private String day;

	public int getTotal_orders() {
		return total_orders;
	}

	public void setTotalOrders(int total_orders) {
		this.total_orders = total_orders;
	}

	public int getTotal_amount() {
		return total_amount;
	}

	public void setTotalAmount(int total_amount) {
		this.total_amount = total_amount;
	}

	public int getHide_times() {
		return hide_times;
	}

	public void setHideTimes(int hide_times) {
		this.hide_times = hide_times;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "PersonalOrderStat [total_orders=" + total_orders + ", total_amount=" + total_amount + ", hide_times="
				+ hide_times + ", day=" + day + "]";
	}
	
	
	
}
