package com.lxtech.cloud.db.model;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

public class CloudTargetIndexChange {
	private String subject;
	
	private Timestamp time;
	
	private double index;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public double getIndex() {
		return index;
	}

	public void setIndex(double index) {
		this.index = index;
	}
	
	public CloudTargetIndexChange(String subject, Timestamp time, double index) {
		this.subject = subject;
		this.time = time;
		this.index = index;
	}
	
}
