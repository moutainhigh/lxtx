package com.lxtech.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CloudDailyStat implements Serializable {
	/**
	 * cloud_daily_stat.id
	 * 
	 * @ibatorgenerated 2016-10-28 18:18:29
	 */
	private Integer id;

	/**
	 * cloud_daily_stat.day
	 * 
	 * @ibatorgenerated 2016-10-28 18:18:29
	 */
	private Integer day;

	/**
	 * cloud_daily_stat.income
	 * 
	 * @ibatorgenerated 2016-10-28 18:18:29
	 */
	private BigDecimal income;

	/**
	 * cloud_daily_stat.pay
	 * 
	 * @ibatorgenerated 2016-10-28 18:18:29
	 */
	private BigDecimal pay;

	/**
	 * cloud_daily_stat.net_income
	 * 
	 * @ibatorgenerated 2016-10-28 18:18:29
	 */
	private BigDecimal netIncome;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public BigDecimal getPay() {
		return pay;
	}

	public void setPay(BigDecimal pay) {
		this.pay = pay;
	}

	public BigDecimal getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(BigDecimal netIncome) {
		this.netIncome = netIncome;
	}

}