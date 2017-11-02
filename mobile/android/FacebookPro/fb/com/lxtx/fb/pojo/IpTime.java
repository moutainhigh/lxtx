package com.lxtx.fb.pojo;

public class IpTime {

	private long id;
	
	private String section;
	
	private long lastTime;
	
	private long lastTime1;

	public IpTime(){
		super();
	}
	
	public IpTime(String section){
		this();
		this.section = section;
		this.lastTime = System.currentTimeMillis();
	}
	
	public IpTime(String section, long lastTime){
		this();
		this.section = section;
		this.lastTime = lastTime;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public long getLastTime1() {
		return lastTime1;
	}

	public void setLastTime1(long lastTime1) {
		this.lastTime1 = lastTime1;
	}
	
	
}
