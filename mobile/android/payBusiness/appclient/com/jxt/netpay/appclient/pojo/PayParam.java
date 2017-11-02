package com.jxt.netpay.appclient.pojo;

/**
 * 支付参数
 * @author leoliu
 *
 */
public class PayParam {

	/**
	 * 网关支付流水号
	 */
	private String tradeNo;
	
	/**
	 * 主题
	 */
	private String subject;
	
	/**
	 * 支付金额，以元为单位
	 */
	private Double fee;

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}
}
