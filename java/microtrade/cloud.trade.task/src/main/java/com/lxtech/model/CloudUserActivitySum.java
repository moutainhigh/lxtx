package com.lxtech.model;

import java.math.BigDecimal;

public class CloudUserActivitySum {
	private Integer id;
	private Integer uid;
	private String wxnm;
	private Integer tran_day_count;
	private Integer tran_order_count;
	private BigDecimal tran_amount;
	private BigDecimal tran_commission;
	private BigDecimal tran_profit;
	private String date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getWxnm() {
		return wxnm;
	}

	public void setWxnm(String wxnm) {
		this.wxnm = wxnm;
	}

	public Integer getTran_day_count() {
		return tran_day_count;
	}

	public void setTran_day_count(Integer tran_day_count) {
		this.tran_day_count = tran_day_count;
	}

	public Integer getTran_order_count() {
		return tran_order_count;
	}

	public void setTran_order_count(Integer tran_order_count) {
		this.tran_order_count = tran_order_count;
	}

	public BigDecimal getTran_amount() {
		return tran_amount;
	}

	public void setTran_amount(BigDecimal tran_amount) {
		this.tran_amount = tran_amount;
	}

	public BigDecimal getTran_commission() {
		return tran_commission;
	}

	public void setTran_commission(BigDecimal tran_commission) {
		this.tran_commission = tran_commission;
	}

	public BigDecimal getTran_profit() {
		return tran_profit;
	}

	public void setTran_profit(BigDecimal tran_profit) {
		this.tran_profit = tran_profit;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
