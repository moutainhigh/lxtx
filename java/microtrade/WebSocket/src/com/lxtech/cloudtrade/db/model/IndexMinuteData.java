package com.lxtech.cloudtrade.db.model;

import java.sql.Timestamp;

public class IndexMinuteData {
	private String subject;

	private Timestamp dtime;

	private int idx;

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
