package com.lxtech.cloud.db.model;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

public class CloudTargetIndexChange {
	private String subject;

	private Timestamp time;

	private long index;

	private int subject_id;

	private long id;

/*	public CloudTargetIndexChange(String code, Timestamp adjustedTime, int calculatedIndex) {
		this.subject = code;
		this.time = adjustedTime;
		this.index = calculatedIndex;
	}*/
	
	/*public CloudTargetIndexChange(long id, String code, int subject_id, Timestamp adjustedTime, int calculatedIndex) {
		this.id = id;
		this.subject = code;
		this.subject_id = subject_id;
		this.time = adjustedTime;
		this.index = calculatedIndex;
	}*/

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

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public int getSubject_id() {
		return subject_id;
	}

	public void setSubject_id(int subject_id) {
		this.subject_id = subject_id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	
}
