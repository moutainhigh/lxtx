package com.baidu.alipay;

import java.util.Date;
import java.util.UUID;

/**
 * 计费任务
 * @author leoliu
 *
 */
public class AppTask {
	
	public static final String FEE = "fee";
	public static final String APPLYTIME = "applytime";
	public static final String STATUS = "status";
	public static final String KEY = "key";
	public static final String XML = "xml";
	public static final String ERRORTIMES = "errorTimes";
	public static final String SYNCHSTATUS = "synchStatus";
	public static final String PRIORITY = "abcd";
	
	public static final int STATUS_ORI = 0;
	public static final int STATUS_SUCC = 1;
	
	private String key = UUID.randomUUID().toString();
	
	private int fee;
	
	private Date applyTime;
	
	private String xml = "";
	
	private int errorTimes = 0;
	
	private int status = STATUS_ORI;
	//异步任务同步状态
	private int synchStatus = 0;
	
	private int priority = 0;
	
	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public int getErrorTimes() {
		return errorTimes;
	}

	public void setErrorTimes(int errorTimes) {
		this.errorTimes = errorTimes;
	}

	public int getSynchStatus() {
		return synchStatus;
	}

	public void setSynchStatus(int synchStatus) {
		this.synchStatus = synchStatus;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	
}
