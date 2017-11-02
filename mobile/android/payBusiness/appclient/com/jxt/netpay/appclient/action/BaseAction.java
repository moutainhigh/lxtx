package com.jxt.netpay.appclient.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

public abstract class BaseAction extends ActionSupport implements ServletRequestAware,ServletResponseAware{

	protected HttpServletRequest servletRequest;
	
	protected HttpServletResponse servletResponse;
	
	@Override
	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

	@Override
	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}
	
	protected String getServerUrl(){
		String serverUrl = servletRequest.getScheme()+"://"+servletRequest.getServerName()+":"+servletRequest.getServerPort();
		
		return serverUrl;
	}
}
