package com.jxt.netpay.pojo;

/**
 * 账号信息
 * @author leoliu
 *
 */
public class ReceiverAccount {

	private Long id;
	
	private Integer paymentMethodId;
	
	/**
	 * 用|分隔
	 */
	private String accountTxt;
	
	private Double feeRate;
	
	private Boolean Valid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(Integer paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public String getAccountTxt() {
		return accountTxt;
	}

	public void setAccountTxt(String accountTxt) {
		this.accountTxt = accountTxt;
	}

	public Double getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(Double feeRate) {
		this.feeRate = feeRate;
	}

	public Boolean getValid() {
		return Valid;
	}

	public void setValid(Boolean valid) {
		Valid = valid;
	}
	
	
	
}
