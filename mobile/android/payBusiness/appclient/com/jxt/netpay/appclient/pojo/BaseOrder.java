package com.jxt.netpay.appclient.pojo;

/**
 * 基础订单元素
 * @author leoliu
 *
 */
public class BaseOrder {
	/**
	 * 订单号
	 */
	private String orderId = "";
	
	private Double price = 0.0;
	
	private Integer paymentMethodId = 0;
	
	/**
	 * 处理业务逻辑URL，必填
	 */
	private String url = "";

	public BaseOrder(String orderId) {
		this.orderId = orderId;
	}

	public BaseOrder() {
		// TODO Auto-generated constructor stub
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPrice() {
		return price;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(Integer paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{orderId:").append(orderId);
		sb.append(",price:").append(price);
		sb.append(",paymentMethodId:").append(paymentMethodId);
		sb.append("}");
		
		return sb.toString();
	}
}
