package com.jxt.netpay.appclient.action;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.netpay.handler.PaymentLogHandler;
import com.jxt.netpay.pojo.PaymentLog;

public class QueryAction extends BaseAction{

	private long id;
	
	public String execute(){
		JSONObject jo = new JSONObject();
		
		PaymentLog paymentLog = paymentLogHandler.select(id);
		
		try {
			if(paymentLog != null){
				int status = paymentLog.getStatus();
				
				jo.put("status", status);
				
				if(status != 0){
					jo.put("callBackUrl",paymentLog.getCallbackUrl());
					jo.put("orderId", paymentLog.getOrderId());
				}
				
			}else{
				jo.put("status", "-2");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			servletResponse.setContentType("text/html;charset=utf-8");
			
			String ret = "";
			String callback = servletRequest.getParameter("callback");
			
			if(callback != null && callback.length() > 0){
				ret = callback+"("+jo.toString()+")";
			}else{
				ret = jo.toString();
			}
			servletResponse.getWriter().print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return NONE;
	}
	
	
	//ioc
	private PaymentLogHandler paymentLogHandler;

	public void setPaymentLogHandler(PaymentLogHandler paymentLogHandler) {
		this.paymentLogHandler = paymentLogHandler;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
}
