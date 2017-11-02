package com.jxt.netpay.appclient.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.jxt.netpay.appclient.pojo.CallBackResult;
import com.jxt.netpay.appclient.service.IPay;
import com.jxt.netpay.appclient.util.HttpUtil;
import com.jxt.netpay.appclient.util.IPayObjFactory;
import com.jxt.netpay.handler.PaymentLogHandler;
import com.jxt.netpay.handler.ReceiverAccountHandler;
import com.jxt.netpay.pojo.PaymentLog;
import com.jxt.netpay.pojo.ReceiverAccount;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 回跳
 * @author leoliu
 *
 */
public class CallBackAction extends ActionSupport implements ServletRequestAware,ServletResponseAware {

	public String execute(){
		
		Long tradeNo = pay.getTradeNoFromCallBack(servletRequest);
		
		PaymentLog paymentLog = paymentLogHandler.select(tradeNo);
		
		ReceiverAccount receiverAccount = receiverAccountHandler.select(paymentLog.getReceiverAccountId());
		
		pay.initProp(receiverAccount.getAccountTxt());
		
		CallBackResult callBackResult = pay.callBack(servletRequest, servletResponse);
		
		System.out.println(callBackResult);
		
		try {
			String url = HttpUtil.getUrl(paymentLog.getCallbackUrl(),new StringBuffer("succ=").append(callBackResult.isSucc()).append("&orderId=").append(paymentLog.getOrderId()).append("&msg=").append(callBackResult.getMsg()).toString());
			
			System.out.println("callback redirect url :"+url);
			
			servletResponse.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return NONE;
	}
	
	
	//IOC
	private HttpServletRequest servletRequest;
	
	private HttpServletResponse servletResponse;
	
	private IPay pay;
	
	private PaymentLogHandler paymentLogHandler;
	
	private ReceiverAccountHandler receiverAccountHandler;
	
	@Override
	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

	@Override
	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public void setPay(IPay pay) {
		this.pay = pay;
	}

	public void setPaymentLogHandler(PaymentLogHandler paymentLogHandler) {
		this.paymentLogHandler = paymentLogHandler;
	}

	public void setReceiverAccountHandler(
			ReceiverAccountHandler receiverAccountHandler) {
		this.receiverAccountHandler = receiverAccountHandler;
	}

}
