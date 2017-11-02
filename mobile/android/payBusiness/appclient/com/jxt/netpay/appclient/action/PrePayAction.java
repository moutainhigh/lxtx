package com.jxt.netpay.appclient.action;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.jxt.netpay.appclient.service.IPrePay;
import com.jxt.netpay.appclient.util.PrePayFactory;

/**
 * 预下单
 * @author leoliu
 *
 */
public class PrePayAction extends BaseAction{

	private static Logger logger = Logger.getLogger(PrePayAction.class);
	
	public String execute() throws IOException{
		
		String appId = servletRequest.getParameter("appId");
		
		IPrePay prePay = prePayFactory.getPrePay(appId);
		
		if(prePay != null){
			String resp = prePay.prepay(servletRequest);
			
			logger.info("resp of "+appId+" :"+resp);
			
			servletResponse.setContentType("text/html;charset=utf-8");
			servletResponse.getWriter().print(resp);
		}else{
			logger.info("prePay is null : "+appId);
		}
		
		return NONE;
	}
		
	//ioc
	private PrePayFactory prePayFactory;

	public void setPrePayFactory(PrePayFactory prePayFactory) {
		this.prePayFactory = prePayFactory;
	}
	
}
