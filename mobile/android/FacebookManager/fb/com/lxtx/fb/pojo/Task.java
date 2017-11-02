package com.lxtx.fb.pojo;

public class Task {
	
	private long id;
	
	private long userId;
	
	private int type;

	private int day;
	
	private String data;

	private int status;
	
	private long startTime;
	
	private int machineId;
	
	private int csUserId;
	
	private int opDay;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getMachineId() {
		return machineId;
	}

	public void setMachineId(int machineId) {
		this.machineId = machineId;
	}

	public int getCsUserId() {
		return csUserId;
	}

	public void setCsUserId(int csUserId) {
		this.csUserId = csUserId;
	}

	public int getOpDay() {
		return opDay;
	}

	public void setOpDay(int opDay) {
		this.opDay = opDay;
	}
	
	
}
