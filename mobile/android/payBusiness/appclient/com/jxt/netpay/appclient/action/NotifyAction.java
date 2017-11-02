package com.jxt.netpay.appclient.action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.jxt.netpay.appclient.pojo.NotifyParam;
import com.jxt.netpay.appclient.service.IPay;
import com.jxt.netpay.handler.PaymentLogHandler;
import com.jxt.netpay.handler.ReceiverAccountHandler;
import com.jxt.netpay.pojo.PaymentLog;
import com.jxt.netpay.pojo.ReceiverAccount;
import com.opensymphony.xwork2.ActionSupport;


/**
 * 后台通知类
 * @author leoliu
 *
 */
public class NotifyAction extends ActionSupport implements ServletRequestAware,ServletResponseAware {

	@Override
	public final String execute() throws Exception {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Boolean succ = false;

		Long traderNo = pay.getTradeNoFromNotify(request);
		
		PaymentLog paymentLog = paymentLogHandler.select(traderNo);
		
		ReceiverAccount receiverAccount = receiverAccountHandler.select(paymentLog.getReceiverAccountId());
		
		pay.initProp(receiverAccount.getAccountTxt());
		
		NotifyParam notifyParam = pay.notify(request, response);
		
		if(notifyParam.isSucc()){
			paymentLog.setNotifyData(notifyParam.getNotifyData());
			paymentLog.setPaymentTime(notifyParam.getPaymentTime());
			paymentLog.setStatus(notifyParam.getStatus());
			paymentLog.setTransactionNumber(notifyParam.getTransactionNumber());
			
			paymentLogHandler.updateNotify(paymentLog);
			
			succ = true;
		}
		
		out.print(pay.getNotifyMsg(succ));
		
		return NONE;
	}


	//IOC
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private PaymentLogHandler paymentLogHandler;
	
	private ReceiverAccountHandler receiverAccountHandler;
	
	private IPay pay;
	
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setPaymentLogHandler(PaymentLogHandler paymentLogHandler) {
		this.paymentLogHandler = paymentLogHandler;
	}

	public void setReceiverAccountHandler(
			ReceiverAccountHandler receiverAccountHandler) {
		this.receiverAccountHandler = receiverAccountHandler;
	}

	public void setPay(IPay pay) {
		this.pay = pay;
	}
}
