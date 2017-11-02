package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudUser implements Serializable {
	/**
	 * cloud_user.id
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:42
	 */
	private Integer id;

	/**
	 * cloud_user.wxid
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:42
	 */
	private String wxid;
	
	private String wxnm;

	/**
	 * cloud_user.balance
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:42
	 */
	private BigDecimal balance;

	/**
	 * cloud_user.mobile
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:42
	 */
	private String mobile;

	/**
	 * cloud_user.password
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:42
	 */
	private String password;

	/**
	 * cloud_user.contract_amount (合约定金)
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:42
	 */
	private BigDecimal contractAmount;

	// 0:未设置手机号 1：未设置密码 2.用户状态不正常 9.允许交易
	private String userStatus;

	// 经济人id
	private Integer brokerId;
	
	private Date crtTm;
	private String crtTm1;

	// 验证码
	private String validCode;
	//验证码手机号
	private String vCodeMobile;

	private String loginStatus;

	private String headimgurl;
	private Integer firstVisit;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWxid() {
		return wxid;
	}

	public void setWxid(String wxid) {
		this.wxid = wxid;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigDecimal getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(BigDecimal contractAmount) {
		this.contractAmount = contractAmount;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getValidCode() {
		return validCode;
	}

	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public Integer getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(Integer brokerId) {
		this.brokerId = brokerId;
	}

	public Date getCrtTm() {
		return crtTm;
	}

	public void setCrtTm(Date crtTm) {
		this.crtTm = crtTm;
	}

	public String getCrtTm1() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		if (crtTm != null) {
			return sdf.format(crtTm);
		}
		return "";
	}

	public void setCrtTm1(String crtTm1) {
		this.crtTm1 = crtTm1;
	}

	public String getWxnm() {
		return wxnm;
	}

	public void setWxnm(String wxnm) {
		this.wxnm = wxnm;
	}

	public String getvCodeMobile() {
		return vCodeMobile;
	}

	public void setvCodeMobile(String vCodeMobile) {
		this.vCodeMobile = vCodeMobile;
	}

	public Integer getFirstVisit() {
		return firstVisit;
	}

	public void setFirstVisit(Integer firstVisit) {
		this.firstVisit = firstVisit;
	}
	
}