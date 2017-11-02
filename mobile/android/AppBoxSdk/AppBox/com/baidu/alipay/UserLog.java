package com.baidu.alipay;

import java.util.Date;
import java.util.UUID;

public class UserLog {
	public static final String LOGS = "logs";
	public static final String LOGTIME = "logtime";
	public static final String STATUS = "status";
	public static final String KEY = "key";
	
	private String key = UUID.randomUUID().toString();
	
	private String logTime;
	
	private String logs;
	
	private int status = 0;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public String getLogs() {
		return logs;
	}

	public void setLogs(String logs) {
		this.logs = logs;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
