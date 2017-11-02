package com.lxtech.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CloudClearDetail implements Serializable {

	/**
	 * cloud_clear_detail.id
	 * 
	 * @ibatorgenerated 2016-11-07 12:14:21
	 */
	private Integer id;

	/**
	 * cloud_clear_detail.clear_count (本次平仓数量)
	 * 
	 * @ibatorgenerated 2016-11-07 12:14:21
	 */
	private Integer clearCount;

	/**
	 * cloud_clear_detail.profit_count (系统赢的笔数)
	 * 
	 * @ibatorgenerated 2016-11-07 12:14:21
	 */
	private Integer profitCount;

	/**
	 * cloud_clear_detail.profit_amount (系统赢的金额)
	 * 
	 * @ibatorgenerated 2016-11-07 12:14:21
	 */
	private BigDecimal profitAmount;

	/**
	 * cloud_clear_detail.loss_count (系统输的笔数)
	 * 
	 * @ibatorgenerated 2016-11-07 12:14:21
	 */
	private Integer lossCount;

	/**
	 * cloud_clear_detail.loss_amount (系统输的金额)
	 * 
	 * @ibatorgenerated 2016-11-07 12:14:21
	 */
	private BigDecimal lossAmount;

	/**
	 * cloud_clear_detail.sys_amount (本次平仓输赢)
	 * 
	 * @ibatorgenerated 2016-11-07 12:14:21
	 */
	private BigDecimal sysAmount;

	/**
	 * cloud_clear_detail.cleart_tm (平仓开始时间)
	 * 
	 * @ibatorgenerated 2016-11-07 12:14:21
	 */
	private Date cleartTm;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getClearCount() {
		return clearCount;
	}

	public void setClearCount(Integer clearCount) {
		this.clearCount = clearCount;
	}

	public Integer getProfitCount() {
		return profitCount;
	}

	public void setProfitCount(Integer profitCount) {
		this.profitCount = profitCount;
	}

	public BigDecimal getProfitAmount() {
		return profitAmount;
	}

	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}

	public Integer getLossCount() {
		return lossCount;
	}

	public void setLossCount(Integer lossCount) {
		this.lossCount = lossCount;
	}

	public BigDecimal getLossAmount() {
		return lossAmount;
	}

	public void setLossAmount(BigDecimal lossAmount) {
		this.lossAmount = lossAmount;
	}

	public BigDecimal getSysAmount() {
		return sysAmount;
	}

	public void setSysAmount(BigDecimal sysAmount) {
		this.sysAmount = sysAmount;
	}

	public Date getCleartTm() {
		return cleartTm;
	}

	public void setCleartTm(Date cleartTm) {
		this.cleartTm = cleartTm;
	}
}