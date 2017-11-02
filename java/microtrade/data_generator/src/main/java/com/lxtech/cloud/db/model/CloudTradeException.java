package com.lxtech.cloud.db.model;

import java.util.List;

public class CloudTradeException {

	private long id;
	
	private long day;
	
	private String hours;
	
	private List<Integer> hourList;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDay() {
		return day;
	}

	public void setDay(long day) {
		this.day = day;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public List<Integer> getHourList() {
		return hourList;
	}

	public void setHourList(List<Integer> hourList) {
		this.hourList = hourList;
	}

	
	
}
