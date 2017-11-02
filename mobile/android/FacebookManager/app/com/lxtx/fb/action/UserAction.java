package com.lxtx.fb.action;

import com.lxtx.fb.pojo.CsUser;
import com.lxtx.fb.service.CsUserService;
import com.lxtx.fb.util.SessionUtils;

public class UserAction extends BaseAction{

	private CsUser csUser;
	
	public String login(){
		
		CsUser loginUser = csUserService.login(csUser);
		
		if(loginUser != null){
			if(loginUser.valid()){
				SessionUtils.setLoginUserToSession(servletRequest, loginUser);
				
				return SUCCESS;
			}
		}
		
		return INPUT;
	}
	
	
	//ioc
	private CsUserService csUserService;

	public void setCsUserService(CsUserService csUserService) {
		this.csUserService = csUserService;
	}

	public CsUser getCsUser() {
		return csUser;
	}

	public void setCsUser(CsUser csUser) {
		this.csUser = csUser;
	}
	
	
}
