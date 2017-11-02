package com.jxt.netpay.appclient.service;

import javax.servlet.http.HttpServletRequest;

public interface IOneJump {

	public String getType();
	
	public String oneJump(HttpServletRequest servletRequest);
	
}
