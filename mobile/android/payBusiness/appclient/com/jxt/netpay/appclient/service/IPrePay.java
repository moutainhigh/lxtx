package com.jxt.netpay.appclient.service;

import javax.servlet.http.HttpServletRequest;

public interface IPrePay {
	
	public String prepay(HttpServletRequest servletRequest);
	
	public String getType();
}
