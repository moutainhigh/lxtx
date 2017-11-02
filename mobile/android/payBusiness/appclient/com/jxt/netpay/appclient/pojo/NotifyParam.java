package com.jxt.netpay.appclient.pojo;

import java.util.Date;

/**
 * 回调时处理参数
 * @author leoliu
 *
 */
public class NotifyParam {
	/**
	 * 是否验证通过
	 */
	private boolean succ = false;
	
	/**
	 * 支付网关日志号
	 */
	private Long paymentLogId = 0l;
	/**
	 * 第三方支付流水号
	 */
	private String transactionNumber = "";
	/**
	 * 支付时间
	 */
	private Date paymentTime = new Date();
	/**
	 * 支付状态
	 */
	private Integer status = 0;
	/**
	 * 支付明细数据
	 */
	private String notifyData = "";
	
	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNotifyData() {
		return notifyData;
	}

	public void setNotifyData(String notifyData) {
		this.notifyData = notifyData;
	}

	public Long getPaymentLogId() {
		return paymentLogId;
	}

	public void setPaymentLogId(Long paymentLogId) {
		this.paymentLogId = paymentLogId;
	}

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}
}
