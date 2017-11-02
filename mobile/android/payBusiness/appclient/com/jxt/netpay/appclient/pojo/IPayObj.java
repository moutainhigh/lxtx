package com.jxt.netpay.appclient.pojo;

import com.jxt.netpay.appclient.service.IPay;

public class IPayObj {

	private IPay pay;
	
	private String callBackUrl;
	
	private String notifyUrl;

	public IPay getPay() {
		return pay;
	}

	public void setPay(IPay pay) {
		this.pay = pay;
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
}
