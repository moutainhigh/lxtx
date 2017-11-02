package com.lxtx.settlement.dbmodel;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserData implements Serializable {

	private Integer id;
	private String wxnm;
	private BigDecimal balance;
	private Integer identity;
	private Long carry_amount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getIdentity() {
		return identity;
	}

	public void setIdentity(Integer identity) {
		this.identity = identity;
	}

	public Long getCarry_amount() {
		return carry_amount;
	}

	public void setCarry_amount(Long carry_amount) {
		this.carry_amount = carry_amount;
	}
}
