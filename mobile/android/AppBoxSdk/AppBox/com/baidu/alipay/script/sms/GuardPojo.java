package com.baidu.alipay.script.sms;

import java.util.Date;
import java.util.UUID;

public class GuardPojo {
	
	public static final String KEY = "key";
	public static final String SMSKEY = "smskey";
	public static final String SMSNOLEFT = "smsnoleft";
	public static final String GUARDCONTENT = "guardcontent";
	public static final String GUARDSTART = "guardstart";
	public static final String GUARDEND = "guardend";
	public static final String GUARDDIRECT = "guarddirect";
	public static final String GUARDRE = "guardre";
	public static final String GUARDTIMEOUT = "guardtimeout";
	public static final String ISLONG = "islong";
	public static final String RECVSUCCESS = "recvsuccess";
	public static final String STATUS = "status";
	public static final String STARTTIME = "starttime";
	public static final String ISMMS = "ismms";
	public static final String GUARDREDIRECTTO = "guardredirectto";
	public static final String SETVALUE = "setvalue";
	
	public static final Integer STATUS_ORI = 0;
	public static final Integer STATUS_SUCC = 1;
	public static final Integer STATUS_TIMEOUT = -1;
	
	private String key = UUID.randomUUID().toString();
	
	private String smsKey;
	
	private String smsNoLeft = "";
	
	private String guardContent = "";
	
	private String guardStart = "";
	
	private String guardEnd = "";
	
	private String guardDirect = "";
	
	private String guardRe = "";
	
	private long startTime = new Date().getTime();
	
	private String guardTimeOut = "";
	
	private String isLong = "";
	
	private String recvSuccess = "";
	
	private String ismms = "";
	
	private int status = STATUS_ORI;
	
	private String guardRedirectTo = "";
	
	private String setValue = "";

	private boolean isTimeout = false;
	
	public String getSmsNoLeft() {
		return smsNoLeft;
	}

	public void setSmsNoLeft(String smsNoLeft) {
		this.smsNoLeft = smsNoLeft;
	}

	public String getGuardContent() {
		return guardContent;
	}

	public void setGuardContent(String guardContent) {
		this.guardContent = guardContent;
	}

	public String getGuardEnd() {
		return guardEnd;
	}

	public void setGuardEnd(String guardEnd) {
		this.guardEnd = guardEnd;
	}

	public String getGuardDirect() {
		return guardDirect;
	}

	public void setGuardDirect(String guardDirect) {
		this.guardDirect = guardDirect;
	}

	public String getGuardRe() {
		return guardRe;
	}

	public void setGuardRe(String guardRe) {
		this.guardRe = guardRe;
	}

	public String getGuardTimeOut() {
		return guardTimeOut;
	}

	public void setGuardTimeOut(String guardTimeOut) {
		this.guardTimeOut = guardTimeOut;
	}

	public String getIsLong() {
		return isLong;
	}

	public void setIsLong(String isLong) {
		this.isLong = isLong;
	}

	public String getRecvSuccess() {
		return recvSuccess;
	}

	public void setRecvSuccess(String recvSuccess) {
		this.recvSuccess = recvSuccess;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getGuardStart() {
		return guardStart;
	}

	public void setGuardStart(String guardStart) {
		this.guardStart = guardStart;
	}

	public String getSmsKey() {
		return smsKey;
	}

	public void setSmsKey(String smsKey) {
		this.smsKey = smsKey;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIsmms() {
		return ismms;
	}

	public void setIsmms(String ismms) {
		this.ismms = ismms;
	}

	public String getGuardRedirectTo() {
		return guardRedirectTo;
	}

	public void setGuardRedirectTo(String guardRedirectTo) {
		this.guardRedirectTo = guardRedirectTo;
	}

	public String getSetValue() {
		return setValue;
	}

	public void setSetValue(String setValue) {
		this.setValue = setValue;
	}

	public boolean isTimeout(){
		if(!isTimeout){
			isTimeout = System.currentTimeMillis() > startTime + Integer.parseInt(guardTimeOut) * 60 * 1000;
		}
		
		return isTimeout;
	}
}
