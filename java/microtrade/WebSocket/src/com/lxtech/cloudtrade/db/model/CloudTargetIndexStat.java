package com.lxtech.cloudtrade.db.model;

/**
 * 保存标的数据的一些统计数据
 * @author wangwei
 *
 */
public class CloudTargetIndexStat {
	
	private long id;
	
	private String subject;
	
	private long open;
	
	private long high;
	
	private long low;
	
	private long last_close;

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

	public long getOpen() {
		return open;
	}

	public void setOpen(long open) {
		this.open = open;
	}

	public long getHigh() {
		return high;
	}

	public void setHigh(long high) {
		this.high = high;
	}

	public long getLow() {
		return low;
	}

	public void setLow(long low) {
		this.low = low;
	}

	public long getLast_close() {
		return last_close;
	}

	public void setLast_close(long last_close) {
		this.last_close = last_close;
	}
	
	
	
}
