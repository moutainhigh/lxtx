package com.jxt.netpay.appclient.action;

import java.io.IOException;

import com.jxt.netpay.appclient.service.IOneJump;
import com.jxt.netpay.appclient.util.OneJumpFactory;

public class OneJumpAction extends BaseAction{

	public String execute(){
		
		String type = servletRequest.getParameter("type");
		
		IOneJump oneJump = oneJumpFactory.getPrePay(type);
		
		if(oneJump != null){
			String jumpUrl = oneJump.oneJump(servletRequest);
			
			try {
				servletResponse.sendRedirect(jumpUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return NONE;
	}
	
	//ioc
	private OneJumpFactory oneJumpFactory;

	public void setOneJumpFactory(OneJumpFactory oneJumpFactory) {
		this.oneJumpFactory = oneJumpFactory;
	}
	
	
}
