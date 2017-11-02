package com.lxtech.model;

import java.io.Serializable;
import java.util.Date;

public class CloudKPeriod implements Serializable {
	/**
	 * cloud_k_period.id
	 * 
	 * @ibatorgenerated 2016-11-10 18:28:53
	 */
	private Integer id;

	/**
	 * cloud_k_period.subject
	 * 
	 * @ibatorgenerated 2016-11-10 18:28:53
	 */
	private String subject;

	/**
	 * cloud_k_period.time
	 * 
	 * @ibatorgenerated 2016-11-10 18:28:53
	 */
	private String time;

	/**
	 * cloud_k_period.open
	 * 
	 * @ibatorgenerated 2016-11-10 18:28:53
	 */
	private Integer open;

	/**
	 * cloud_k_period.high
	 * 
	 * @ibatorgenerated 2016-11-10 18:28:53
	 */
	private Integer high;

	/**
	 * cloud_k_period.low
	 * 
	 * @ibatorgenerated 2016-11-10 18:28:53
	 */
	private Integer low;

	/**
	 * cloud_k_period.close
	 * 
	 * @ibatorgenerated 2016-11-10 18:28:53
	 */
	private Integer close;

	/**
	 * cloud_k_period.type (k线类型 )
	 * 
	 * @ibatorgenerated 2016-11-10 18:28:53
	 */
	private Integer type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getOpen() {
		return open;
	}

	public void setOpen(Integer open) {
		this.open = open;
	}

	public Integer getHigh() {
		return high;
	}

	public void setHigh(Integer high) {
		this.high = high;
	}

	public Integer getLow() {
		return low;
	}

	public void setLow(Integer low) {
		this.low = low;
	}

	public Integer getClose() {
		return close;
	}

	public void setClose(Integer close) {
		this.close = close;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "CloudKPeriod [id=" + id + ", subject=" + subject + ", time=" + time + ", open=" + open + ", high="
				+ high + ", low=" + low + ", close=" + close + ", type=" + type + "]";
	}

}