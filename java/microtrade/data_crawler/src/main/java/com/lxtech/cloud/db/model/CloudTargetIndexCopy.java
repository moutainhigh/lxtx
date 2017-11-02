package com.lxtech.cloud.db.model;

public class CloudTargetIndexCopy {

	private long id;

	private String targetday;

	private String sourceday;

	private int done;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTargetday() {
		return targetday;
	}

	public void setTargetday(String targetday) {
		this.targetday = targetday;
	}

	public String getSourceday() {
		return sourceday;
	}

	public void setSourceday(String sourceday) {
		this.sourceday = sourceday;
	}

	public int getDone() {
		return done;
	}

	public void setDone(int done) {
		this.done = done;
	}

}
