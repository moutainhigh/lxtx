package com.jxt.netpay.appclient.pojo;

public class PayClassObj {

	private String payClassName;
	
	private String callBackUrl;
	
	private String notifyUrl;

	public String getPayClassName() {
		return payClassName;
	}

	public void setPayClassName(String payClassName) {
		this.payClassName = payClassName;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	
	public String toString(){
		return "payClassName="+payClassName+"&callBackUrl="+callBackUrl+"&notifyUrl="+notifyUrl;
	}
}
