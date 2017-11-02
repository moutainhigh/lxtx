package com.baidu.third.jxt.sdk.model;

import java.util.HashMap;
import java.util.Map;

import com.baidu.third.jxt.sdk.PayType;
import com.baidu.third.jxt.sdk.Utils;


public class PaymentBean {

	private String appId;
	
	private String key;
	
	private String orderId;
	
	private double fee;
	
	private String payType;
	
	private String desc;
	
	private String notifyUrl;
	
	private long tradeId;
	
	private int paySort = 0;
	
	public PaymentBean(String orderId, double fee, String desc, String notifyUrl,PayType payType) {
		this.orderId = orderId;
		this.fee = fee;
		this.desc = desc;
		this.notifyUrl = notifyUrl;
		this.payType = payType.name();
		this.paySort = 0;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Map<String,String> getParamsMap() {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("appId",appId);
		map.put("orderId",orderId);
		map.put("price",fee+"");
		map.put("payType",payType);
		map.put("callBackUrl","js://page/finish");
		map.put("notifyUrl",notifyUrl);
		
		map.put("sign", getSign());
		map.put("returnType","json");
		map.put("paySort",""+paySort);
		map.put("ver", "2");
		
		return map;
	}
	
	private String getSign(){
		String md5 = appId+orderId+payType+fee+key;
		return Utils.MD5Encode(md5, "utf-8");
	}

	public long getTradeId() {
		return tradeId;
	}

	public void setTradeId(long tradeId) {
		this.tradeId = tradeId;
	}

	public int getPaySort() {
		return paySort;
	}

	public void setPaySort(int paySort) {
		this.paySort = paySort;
	}
	
	
}
