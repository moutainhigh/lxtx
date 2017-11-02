package com.lxtx.robot.dbmodel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.lxtx.robot.model.protocol.S2CUserInfo;

public class UserData implements Serializable {

	public static UserData conver(S2CUserInfo s2cUserInfo) {
		UserData userData = new UserData();
		userData.setId(s2cUserInfo.getId());
		userData.setWxnm(s2cUserInfo.getName());
		userData.setCarry_amount((long) s2cUserInfo.getChips());
		userData.setBalance(new BigDecimal(s2cUserInfo.getMoney()));
		return userData;
	}

	public static List<UserData> conver(List<S2CUserInfo> infos) {
		List<UserData> userDatas = new ArrayList<>();
		for (S2CUserInfo info : infos) {
			userDatas.add(conver(info));
		}
		return userDatas;
	}

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
