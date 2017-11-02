package com.jxt.netpay.handler;

import java.util.List;

import com.jxt.netpay.appclient.pojo.BaseOrder;
import com.jxt.netpay.appclient.util.CommonUtil;
import com.jxt.netpay.pojo.PaymentLog;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class PaymentLogHandler extends SimpleIbatisEntityHandler<PaymentLog> {

	private static  final String SQL_UPDATENOTIFY = "updateNotify";
	private static final String SQL_UPDATEDEALSTATUS = "updateDealStatus";
	
	private static final String SQL_QUERYPAYLIST = "queryPayList";
	
	public int updateDealStatus(PaymentLog paymentLog){
		return update(SQL_UPDATEDEALSTATUS,paymentLog);
	}
	
	public List<PaymentLog> queryPayByDealStatus(int dealStatus){
		return this.queryForList(SQL_QUERYPAYLIST, dealStatus);
	}
	
	public PaymentLog trace(BaseOrder baseOrder,String callbackUrl,String notifyUrl,Long receiverAccountId,String clientIp) {
		
		PaymentLog paymentLog = new PaymentLog();
		
		paymentLog.setFee(baseOrder.getPrice());
		paymentLog.setOrderDesc(baseOrder.getOrderId()+"");
		paymentLog.setOrderId(baseOrder.getOrderId());
		paymentLog.setPaymentMethodId(baseOrder.getPaymentMethodId());
		
		paymentLog.setReceiverAccountId(receiverAccountId);
		paymentLog.setCallbackUrl(callbackUrl);
		paymentLog.setNotifyUrl(notifyUrl);
		
		paymentLog.setIp(clientIp);
		paymentLog.setCreateDay(CommonUtil.getDay());
		
		this.insert(paymentLog);
		
		return paymentLog;
		
	}

	public void updateNotify(PaymentLog paymentLog) {
		this.update(SQL_UPDATENOTIFY,paymentLog);
	}


}
