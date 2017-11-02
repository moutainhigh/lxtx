package com.baidu.serv1;

public class PayResult {
	public static final String CODE_CANCEL = "299";
	public static final String CODE_SUCC = "200";
	public static final String CODE_FAIL = "208";
	public static final String CODE_UPERROR = "201";
	public static final String CODE_NOORDER = "202";
	public static final String CODE_NETERROR = "204";
	public static final String CODE_RUNERROR = "205";
		
	private String code;
	
	private int money;
	
	private int payMoney;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(int payMoney) {
		this.payMoney = payMoney;
	}
	
	
}
