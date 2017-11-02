package com.lxtech.model;

import java.math.BigDecimal;
import java.util.Date;

public class CloudUserDayTranSum {
	private Integer id;
	private Integer tran_u_count;
	private BigDecimal tran_amount;
	private BigDecimal tran_commission;
	private Integer tran_order_count;
	private BigDecimal tran_u_mean_count;
	private BigDecimal tran_mean_amount;
	private BigDecimal single_tran_amount;
	private Integer tran_u_old_count;
	private Integer tran_u_old_order_count;
	private BigDecimal tran_u_old_amount;
	private BigDecimal tran_u_old_commission;
	private BigDecimal tran_u_old_count_prop;
	private BigDecimal tran_u_old_amount_prop;
	private BigDecimal tran_u_old_commission_prop;
	private String date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTran_u_count() {
		return tran_u_count;
	}

	public void setTran_u_count(Integer tran_u_count) {
		this.tran_u_count = tran_u_count;
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

	public Integer getTran_order_count() {
		return tran_order_count;
	}

	public void setTran_order_count(Integer tran_order_count) {
		this.tran_order_count = tran_order_count;
	}

	public Integer getTran_u_old_order_count() {
		return tran_u_old_order_count;
	}

	public void setTran_u_old_order_count(Integer tran_u_old_order_count) {
		this.tran_u_old_order_count = tran_u_old_order_count;
	}

	public BigDecimal getTran_u_mean_count() {
		return tran_u_mean_count;
	}

	public void setTran_u_mean_count(BigDecimal tran_u_mean_count) {
		this.tran_u_mean_count = tran_u_mean_count;
	}

	public BigDecimal getTran_mean_amount() {
		return tran_mean_amount;
	}

	public void setTran_mean_amount(BigDecimal tran_mean_amount) {
		this.tran_mean_amount = tran_mean_amount;
	}

	public BigDecimal getSingle_tran_amount() {
		return single_tran_amount;
	}

	public void setSingle_tran_amount(BigDecimal single_tran_amount) {
		this.single_tran_amount = single_tran_amount;
	}

	public Integer getTran_u_old_count() {
		return tran_u_old_count;
	}

	public void setTran_u_old_count(Integer tran_u_old_count) {
		this.tran_u_old_count = tran_u_old_count;
	}

	public BigDecimal getTran_u_old_amount() {
		return tran_u_old_amount;
	}

	public void setTran_u_old_amount(BigDecimal tran_u_old_amount) {
		this.tran_u_old_amount = tran_u_old_amount;
	}

	public BigDecimal getTran_u_old_commission() {
		return tran_u_old_commission;
	}

	public void setTran_u_old_commission(BigDecimal tran_u_old_commission) {
		this.tran_u_old_commission = tran_u_old_commission;
	}

	public BigDecimal getTran_u_old_count_prop() {
		return tran_u_old_count_prop;
	}

	public void setTran_u_old_count_prop(BigDecimal tran_u_old_count_prop) {
		this.tran_u_old_count_prop = tran_u_old_count_prop;
	}

	public BigDecimal getTran_u_old_amount_prop() {
		return tran_u_old_amount_prop;
	}

	public void setTran_u_old_amount_prop(BigDecimal tran_u_old_amount_prop) {
		this.tran_u_old_amount_prop = tran_u_old_amount_prop;
	}

	public BigDecimal getTran_u_old_commission_prop() {
		return tran_u_old_commission_prop;
	}

	public void setTran_u_old_commission_prop(BigDecimal tran_u_old_commission_prop) {
		this.tran_u_old_commission_prop = tran_u_old_commission_prop;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
