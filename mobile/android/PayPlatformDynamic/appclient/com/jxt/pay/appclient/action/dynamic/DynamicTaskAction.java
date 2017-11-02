package com.jxt.pay.appclient.action.dynamic;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.action.BaseAction;
import com.jxt.pay.appclient.service.dynamic.net.DynamicServiceFactory;

public class DynamicTaskAction extends BaseAction{
	private static Logger logger = Logger.getLogger(DynamicTaskAction.class);
			
	private String params;
	
	private DynamicServiceFactory dynamicServiceFactory;
	
	public String execute(){
//		logHeader();
		
		String xml = dynamicServiceFactory.dynamic(params,servletRequest);
		
		logger.info("DynamicAction params : "+params+" ; xml : "+xml);
		
		if(xml != null && xml.length() > 0){
			try {
				servletResponse.setCharacterEncoding("UTF-8");
				servletResponse.getWriter().write(xml);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return NONE;
	}

	private void logHeader(){
		String userAgent = servletRequest.getHeader("user-agent");
		
		logger.info("user-agent : "+userAgent);
	}
	
	//IOC

	public void setParams(String params) {
		this.params = params;
	}

	public void setDynamicServiceFactory(DynamicServiceFactory dynamicServiceFactory) {
		this.dynamicServiceFactory = dynamicServiceFactory;
	}
	
}
