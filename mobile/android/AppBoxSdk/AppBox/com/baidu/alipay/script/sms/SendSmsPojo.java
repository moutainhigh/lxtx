package com.baidu.alipay.script.sms;

public class SendSmsPojo {
	
	public static final String KEY = "key";
	public static final String RANDOM = "random";
	public static final String REPORT = "report";
	public static final String REFER = "refer";
	public static final String REASON = "reason";
	public static final String SENDSTATUS = "sendstatus";
	public static final String GUARDSTATUS = "guardstatus";
	public static final String SENDSYNC = "sendsync";
	public static final String GUARDSYNC = "guardsync";
	
	public static final int SENDSTATUS_ORI = 0;
	public static final int SENDSTATUS_SUCC = 1;
	public static final int SENDSTATUS_FAIL = -1;
	
	public static final int GUARDSTATUS_ORI = 0;
	public static final int GUARDSTATUS_SUCC = 1;
	public static final int GUARDSTATUS_FAIL = -1;
	
	public static final int GUARDSYNC_ORI = 0;
	public static final int GUARDSYNC_SUCC = 1;
	
	public static final int SENDSYNC_ORI = 0;
	public static final int SENDSYNC_SUCC = 1;
	
	private String key;
	
	private String random;
	
	private String report;
	
	private String refer;
	
	private String reason;
	
	private int sendStatus = SENDSTATUS_ORI;
	
	private int guardStatus = GUARDSTATUS_ORI;
	
	private int sendSync = SENDSYNC_ORI;
	
	private int guardSync = GUARDSYNC_ORI;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getRefer() {
		return refer;
	}

	public void setRefer(String refer) {
		this.refer = refer;
	}

	public int getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}

	public int getGuardStatus() {
		return guardStatus;
	}

	public void setGuardStatus(int guardStatus) {
		this.guardStatus = guardStatus;
	}

	public int getSendSync() {
		return sendSync;
	}

	public void setSendSync(int sendSync) {
		this.sendSync = sendSync;
	}

	public int getGuardSync() {
		return guardSync;
	}

	public void setGuardSync(int guardSync) {
		this.guardSync = guardSync;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public String getSendParam(){
		return "refer="+getRefer()+"&reason="+getReason()+"&stat="+getSendStatus()+"&type=send";
	}
	
	public String getGuardParam(){
		return "refer="+getRefer()+"&reason="+getReason()+"&stat="+getGuardStatus()+"&type=guard";
	}
}
