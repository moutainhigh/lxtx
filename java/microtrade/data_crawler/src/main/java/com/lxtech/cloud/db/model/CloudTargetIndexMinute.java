package com.lxtech.cloud.db.model;

import java.sql.Timestamp;

public class CloudTargetIndexMinute {

	private String subject;
	
	private Timestamp dtime;
	
	private int idx;
	//一天的数据从上午8点开始，数据从9点开始，index为60，往后每加一分钟，index加1，比如9点50的数据，timeindex就是110.
	private int timeindex; 
	
	private String day;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Timestamp getDtime() {
		return dtime;
	}

	public void setDtime(Timestamp dtime) {
		this.dtime = dtime;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getTimeindex() {
		return timeindex;
	}

	public void setTimeindex(int timeindex) {
		this.timeindex = timeindex;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
}
