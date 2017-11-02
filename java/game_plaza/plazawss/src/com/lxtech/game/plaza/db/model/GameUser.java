package com.lxtech.game.plaza.db.model;

import java.math.BigDecimal;
import java.util.Date;

public class GameUser {

	private long id;

	private String wxid;

	private String wxnm;

	private BigDecimal balance;

	private String mobile;

	private String password;

	private BigDecimal contract_amount;

	private String headimgurl;

	private int first_visit;

	private String chnno;

	private int is_subscribe;

	private Date crt_tm;

	private int wx_provider_id;

	private int broker_id;

	private int identity;

	private long carry_amount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public BigDecimal getContract_amount() {
		return contract_amount;
	}

	public void setContract_amount(BigDecimal contract_amount) {
		this.contract_amount = contract_amount;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public int getFirst_visit() {
		return first_visit;
	}

	public void setFirst_visit(int first_visit) {
		this.first_visit = first_visit;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public int getIs_subscribe() {
		return is_subscribe;
	}

	public void setIs_subscribe(int is_subscribe) {
		this.is_subscribe = is_subscribe;
	}

	public Date getCrt_tm() {
		return crt_tm;
	}

	public void setCrt_tm(Date crt_tm) {
		this.crt_tm = crt_tm;
	}

	public int getWx_provider_id() {
		return wx_provider_id;
	}

	public void setWx_provider_id(int wx_provider_id) {
		this.wx_provider_id = wx_provider_id;
	}

	public int getBroker_id() {
		return broker_id;
	}

	public void setBroker_id(int broker_id) {
		this.broker_id = broker_id;
	}

	public int getIdentity() {
		return identity;
	}

	public void setIdentity(int identity) {
		this.identity = identity;
	}

	public long getCarry_amount() {
		return carry_amount;
	}

	public void setCarry_amount(long carry_amount) {
		this.carry_amount = carry_amount;
	}

	

}
