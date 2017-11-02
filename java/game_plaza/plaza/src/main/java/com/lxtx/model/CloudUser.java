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
	
	private Integer bankBalance;

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

	private Integer brokerId;

	private Date crtTm;
	private String crtTm1;

	// 验证码
	private String validCode;
	// 验证码手机号
	private String vCodeMobile;

	private String loginStatus;

	private String headimgurl;
	private Integer firstVisit;

	// 渠道号
	private String chnno;
	// 0 取消关注 1 已关注
	private Integer isSubscribe;

	private Integer wxProviderId;
	// 标的物个数
	private String subjectCount;

	/**
	 * g_user.identity (身份：0，普通用户；1，机器人)
	 * 
	 * @ibatorgenerated 2017-01-08 13:21:34
	 */
	private Integer identity;

	/**
	 * g_user.carry_amount (身上携带的金币)
	 * 
	 * @ibatorgenerated 2017-01-08 13:21:34
	 */
	private Long carryAmount;
	
	private BigDecimal allBalance;

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

	public Integer getWxProviderId() {
		return wxProviderId;
	}

	public void setWxProviderId(Integer wxProviderId) {
		this.wxProviderId = wxProviderId;
	}

	public String getSubjectCount() {
		return subjectCount;
	}

	public void setSubjectCount(String subjectCount) {
		this.subjectCount = subjectCount;
	}

	public Integer getIdentity() {
		return identity;
	}

	public void setIdentity(Integer identity) {
		this.identity = identity;
	}

	public Long getCarryAmount() {
		return carryAmount;
	}

	public void setCarryAmount(Long carryAmount) {
		this.carryAmount = carryAmount;
	}

	public Integer getBankBalance() {
		return this.balance.multiply(new BigDecimal(10000)).intValue();
	}

	public void setBankBalance(Integer bankBalance) {
		this.bankBalance = bankBalance;
	}

	public BigDecimal getAllBalance() {
		return allBalance;
	}

	public void setAllBalance(BigDecimal allBalance) {
		this.allBalance = allBalance;
	}

}