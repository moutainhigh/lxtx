package com.lxtx.model.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BrokerUnderPerVo {
	private String wxnm;
	private String mobile;
	private String balance;
	private Date crtTm;

	public String getWxnm() {
		return wxnm;
	}

	public void setWxnm(String wxnm) {
		this.wxnm = wxnm;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getCrtTm() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		if (crtTm != null) {
			return sdf.format(crtTm);
		}
		return "";
	}

	public void setCrtTm(Date crtTm) {
		this.crtTm = crtTm;
	}

}
