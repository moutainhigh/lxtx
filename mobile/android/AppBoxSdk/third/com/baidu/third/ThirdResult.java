package com.baidu.third;

public class ThirdResult {

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
	
	public ThirdResult(){
		
	}
	
	public ThirdResult(boolean succ,String reason){
		this.succ = succ;
		this.reason = reason;
	}
}
