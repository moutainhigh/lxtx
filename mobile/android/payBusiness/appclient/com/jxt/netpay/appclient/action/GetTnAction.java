package com.jxt.netpay.appclient.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jxt.netpay.appclient.pojo.BaseOrder;
import com.jxt.netpay.appclient.pojo.CallBackAndNotify;
import com.jxt.netpay.appclient.pojo.IPayObj;
import com.jxt.netpay.appclient.pojo.PayParam;
import com.jxt.netpay.appclient.service.IPay;
import com.jxt.netpay.appclient.util.CommonUtil;
import com.jxt.netpay.appclient.util.IPayObjFactory;
import com.jxt.netpay.handler.PaymentLogHandler;
import com.jxt.netpay.handler.ReceiverAccountHandler;
import com.jxt.netpay.helper.ReceiverAccountHelper;
import com.jxt.netpay.pojo.PaymentLog;
import com.jxt.netpay.pojo.ReceiverAccount;
import com.qlzf.commons.struts.JsonBaseAppClientAction;

public class GetTnAction extends JsonBaseAppClientAction{

	@Override
	protected Object appRealExecute() throws Exception {
		
		System.out.println(baseOrder.toString());
		System.out.println("{callbackUrl:"+callbackUrl+",notifyUrl:"+notifyUrl+"}");
		
		//获取收款账号信息
		ReceiverAccount receiverAccount = receiverAccountHelper.getReceiverAccount(baseOrder.getPaymentMethodId());
		
		//记录到数据库
		String clientIp = CommonUtil.getRemortIP(servletRequest);
		PaymentLog paymentLog = paymentLogHandler.trace(baseOrder,callbackUrl,notifyUrl,receiverAccount.getId(),clientIp);
		
		Long paymentLogId = paymentLog.getId();
		
		//支付流水号
		PayParam payParam = new PayParam();
		payParam.setFee(baseOrder.getPrice());
		payParam.setSubject(baseOrder.getOrderId()+"");
		payParam.setTradeNo(""+paymentLogId);
		
	    IPayObj iPayObj = payFactory.getIPayObj(baseOrder.getPaymentMethodId());
	    
	    IPay ipay = iPayObj.getPay();
	    
	    ipay.initProp(receiverAccount.getAccountTxt());
	    
	  //获取跳转地址
  		CallBackAndNotify cban = new CallBackAndNotify();
  	    cban.setCallBackUrl(getServerUrl()+iPayObj.getCallBackUrl());
  	    cban.setNotifyUrl(getServerUrl()+iPayObj.getNotifyUrl());
  	    cban.setRequest(servletRequest);
  	    cban.setResponse(servletResponse);
  	 
	    String tn = ipay.getTn(payParam, cban);
	    
	    Map<String, Object> map = new HashMap<String, Object>();
	    
	    map.put("tn", tn);
	    
		return map;
	}
	
	//IOC
	private HttpServletRequest servletRequest;
	
	private HttpServletResponse servletResponse;
	
	private IPayObjFactory payFactory;
	
	private PaymentLogHandler paymentLogHandler;
	
	private ReceiverAccountHelper receiverAccountHelper;
	
	@Override
	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

	@Override
	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}
	
	public void setPayFactory(IPayObjFactory payFactory) {
		this.payFactory = payFactory;
	}

	public void setReceiverAccountHelper(
			ReceiverAccountHelper receiverAccountHelper) {
		this.receiverAccountHelper = receiverAccountHelper;
	}
	
	public void setPaymentLogHandler(PaymentLogHandler paymentLogHandler){
		this.paymentLogHandler = paymentLogHandler;
	}

	//Param
	private BaseOrder baseOrder;
	
	private String callbackUrl;
	
	private String notifyUrl;
	
	public BaseOrder getBaseOrder() {
		return baseOrder;
	}

	public void setBaseOrder(BaseOrder baseOrder) {
		this.baseOrder = baseOrder;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	
}
