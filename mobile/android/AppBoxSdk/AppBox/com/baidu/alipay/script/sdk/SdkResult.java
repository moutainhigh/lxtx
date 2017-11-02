package com.baidu.alipay.script.sdk;

public class SdkResult {

	private boolean succ = true;
	
	private String reason = "";

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public SdkResult(){
		
	}
	
	public SdkResult(boolean succ,String reason){
		this.succ = succ;
		this.reason = reason;
	}
}
