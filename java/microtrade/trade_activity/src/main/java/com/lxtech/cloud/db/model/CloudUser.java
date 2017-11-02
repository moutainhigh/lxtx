package com.lxtech.cloud.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudUser implements Serializable {
	private Integer id;

	private String wxid;
	
	private String wxnm;

	private BigDecimal balance;

	private String mobile;

	private String password;

	private BigDecimal contractAmount;

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
	public String getWxnm() {
		return wxnm;
	}
	public void setWxnm(String wxnm) {
		this.wxnm = wxnm;
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
		return crtTm1;
	}
	public void setCrtTm1(String crtTm1) {
		this.crtTm1 = crtTm1;
	}
	public String getValidCode() {
		return validCode;
	}
	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}
	public String getvCodeMobile() {
		return vCodeMobile;
	}
	public void setvCodeMobile(String vCodeMobile) {
		this.vCodeMobile = vCodeMobile;
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
	public Integer getFirstVisit() {
		return firstVisit;
	}
	public void setFirstVisit(Integer firstVisit) {
		this.firstVisit = firstVisit;
	}
	public String getChnno() {
		return chnno;
	}
	public void setChnno(String chnno) {
		this.chnno = chnno;
	}
	public Integer getIsSubscribe() {
		return isSubscribe;
	}
	public void setIsSubscribe(Integer isSubscribe) {
		this.isSubscribe = isSubscribe;
	}
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
	// 渠道号
	private String chnno;
	// 0 取消关注  1  已关注
	private Integer isSubscribe;
	
	
	
}