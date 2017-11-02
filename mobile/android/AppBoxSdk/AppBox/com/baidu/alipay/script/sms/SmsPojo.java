package com.baidu.alipay.script.sms;

public class SmsPojo {
	
	public static final String KEY = "key";
	public static final String SENDSMSKEY = "sendsmskey";
	public static final String STATUS = "status";
	
	public static final int STATUS_ORI = 0;
	public static final int STATUS_SUCC = 1;
	public static final int STATUS_FAIL = -1;
	
	private String key;
	
	private String sendSmsKey;
	
	private int status = STATUS_ORI;
	
	public SmsPojo(){
		
	}
	
	public SmsPojo(String key,String sendSmsKey){
		this();
		this.key = key;
		this.sendSmsKey = sendSmsKey;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSendSmsKey() {
		return sendSmsKey;
	}

	public void setSendSmsKey(String sendSmsKey) {
		this.sendSmsKey = sendSmsKey;
	}
	
	
}
