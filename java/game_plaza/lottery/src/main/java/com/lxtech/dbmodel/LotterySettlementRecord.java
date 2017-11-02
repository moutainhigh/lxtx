package com.lxtech.dbmodel;

import java.util.Date;

public class LotterySettlementRecord {
	private Integer id;
	private Integer data_date;
	private Integer data_sn;
	private String data_open_code;
	private Date operator_time;
	private Integer order_count;
	private Integer win_count;
	private Integer lose_count;
	private Long order_money;
	private Long settlement_money;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getOperator_time() {
		return operator_time;
	}

	public void setOperator_time(Date operator_time) {
		this.operator_time = operator_time;
	}

	public Integer getOrder_count() {
		return order_count;
	}

	public void setOrder_count(Integer order_count) {
		this.order_count = order_count;
	}

	public Integer getWin_count() {
		return win_count;
	}

	public void setWin_count(Integer win_count) {
		this.win_count = win_count;
	}

	public Integer getLose_count() {
		return lose_count;
	}

	public void setLose_count(Integer lose_count) {
		this.lose_count = lose_count;
	}

	public Long getOrder_money() {
		return order_money;
	}

	public void setOrder_money(Long order_money) {
		this.order_money = order_money;
	}

	public Long getSettlement_money() {
		return settlement_money;
	}

	public void setSettlement_money(Long settlement_money) {
		this.settlement_money = settlement_money;
	}

	public Integer getData_date() {
		return data_date;
	}

	public void setData_date(Integer data_date) {
		this.data_date = data_date;
	}

	public Integer getData_sn() {
		return data_sn;
	}

	public void setData_sn(Integer data_sn) {
		this.data_sn = data_sn;
	}

	public String getData_open_code() {
		return data_open_code;
	}

	public void setData_open_code(String data_open_code) {
		this.data_open_code = data_open_code;
	}
	
}
