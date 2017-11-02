package com.lxtech.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CloudProfit implements Serializable {
	private Integer id;

	private String date;

	private String chnno;

	private BigDecimal fill;

	private BigDecimal real_fill;

	private BigDecimal repay;

	private BigDecimal commission;

	private BigDecimal sum_commission;

	private int add_user;

	private int order_count;

	private BigDecimal profit;

	private BigDecimal real_profit;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public BigDecimal getFill() {
		return fill;
	}

	public void setFill(BigDecimal fill) {
		this.fill = fill;
	}

	public BigDecimal getReal_fill() {
		return real_fill;
	}

	public void setReal_fill(BigDecimal real_fill) {
		this.real_fill = real_fill;
	}

	public BigDecimal getRepay() {
		return repay;
	}

	public void setRepay(BigDecimal repay) {
		this.repay = repay;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getSum_commission() {
		return sum_commission;
	}

	public void setSum_commission(BigDecimal sum_commission) {
		this.sum_commission = sum_commission;
	}

	public int getAdd_user() {
		return add_user;
	}

	public void setAdd_user(int add_user) {
		this.add_user = add_user;
	}

	public int getOrder_count() {
		return order_count;
	}

	public void setOrder_count(int order_count) {
		this.order_count = order_count;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getReal_profit() {
		return real_profit;
	}

	public void setReal_profit(BigDecimal real_profit) {
		this.real_profit = real_profit;
	}

	@Override
	public String toString() {
		return "CloudProfit [date=" + date + ", chnno=" + chnno + ", fill=" + fill + ", real_fill=" + real_fill
				+ ", repay=" + repay + ", commission=" + commission + ", sum_commission=" + sum_commission
				+ ", add_user=" + add_user + ", order_count=" + order_count + ", profit=" + profit + ", real_profit="
				+ real_profit + "]";
	}

}