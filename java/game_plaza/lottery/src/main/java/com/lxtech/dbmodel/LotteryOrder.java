package com.lxtech.dbmodel;

import java.util.Date;

public class LotteryOrder {
	public static enum LotteryOrderState {
		LOS_DEFAULT, LOS_FINISH
	}

	private Integer id;
	private Integer user_id;
	private Date order_time;
	private Integer money;
	private Integer state;
	private Date settlement_time;
	private Integer settlement_result;
	private Integer direct_date;
	private Integer direct_sn;
	private Integer code;
	private String announcement;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Date getOrder_time() {
		return order_time;
	}

	public void setOrder_time(Date order_time) {
		this.order_time = order_time;
	}

	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getSettlement_time() {
		return settlement_time;
	}

	public void setSettlement_time(Date settlement_time) {
		this.settlement_time = settlement_time;
	}

	public Integer getSettlement_result() {
		return settlement_result;
	}

	public void setSettlement_result(Integer settlement_result) {
		this.settlement_result = settlement_result;
	}

	public Integer getDirect_date() {
		return direct_date;
	}

	public void setDirect_date(Integer direct_date) {
		this.direct_date = direct_date;
	}

	public Integer getDirect_sn() {
		return direct_sn;
	}

	public void setDirect_sn(Integer direct_sn) {
		this.direct_sn = direct_sn;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}

}
