package com.lxtech.cloud.db.model;

/**
 * 保存标的数据的一些统计数据
 * @author wangwei
 *
 */
public class CloudTargetIndexStat {
	
	private long id;
	
	private String subject;
	
	private double open;
	
	private double high;
	
	private double low;
	
	private double last_close;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getLast_close() {
		return last_close;
	}

	public void setLast_close(double last_close) {
		this.last_close = last_close;
	}
	
}
