package com.lxtx.fb.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.lxtx.fb.pojo.CsUser;
import com.lxtx.fb.util.CommonUtil;
import com.lxtx.fb.util.SessionUtils;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements ServletRequestAware, ServletResponseAware{

	protected boolean needLogin = true;
	
	protected HttpServletResponse servletResponse;
	
	protected HttpServletRequest servletRequest;
	
	
	@Override
	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

	@Override
	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}
	
	public String getServerUrl(){
		return CommonUtil.getServerUrl(servletRequest);
	}
	
	public CsUser getLoginUser(){
		return SessionUtils.getLoginUserFromSession(servletRequest);
	}
	
	public int getLoginUserId(){
		CsUser csUser = getLoginUser();
		
		if(csUser != null){
			return csUser.getId();
		}
		
		return 0;
	}
}
