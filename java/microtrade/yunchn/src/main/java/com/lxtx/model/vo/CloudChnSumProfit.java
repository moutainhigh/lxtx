package com.lxtx.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudChnSumProfit implements Serializable {

	/**
	 * cloud_chn_day_profit.id
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private Integer id;

	/**
	 * cloud_chn_day_profit.chnno
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String chnno;

	/**
	 * cloud_chn_day_profit.profit (盈利)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private BigDecimal profit;

	/**
	 * cloud_chn_day_profit.loss (亏损)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private BigDecimal loss;

	/**
	 * cloud_chn_day_profit.commission (手续费)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private BigDecimal commission;

	/**
	 * cloud_chn_day_profit.coupon_commission (用券手续费)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private BigDecimal couponCommission;

	/**
	 * cloud_chn_day_profit.add_user (新增用户量)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private Integer addUser;

	/**
	 * cloud_chn_day_profit.order_count (平仓订单数量)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private Integer orderCount;

	/**
	 * cloud_chn_day_profit.chn_commission (渠道手续费)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private BigDecimal chnCommission;

	/**
	 * cloud_chn_day_profit.date
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String date;

	// 交易金额
	private BigDecimal contractMoney;
	// 总盈亏
	private BigDecimal fProfit;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getLoss() {
		return loss;
	}

	public void setLoss(BigDecimal loss) {
		this.loss = loss;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getCouponCommission() {
		return couponCommission;
	}

	public void setCouponCommission(BigDecimal couponCommission) {
		this.couponCommission = couponCommission;
	}

	public Integer getAddUser() {
		return addUser;
	}

	public void setAddUser(Integer addUser) {
		this.addUser = addUser;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public BigDecimal getChnCommission() {
		return chnCommission;
	}

	public void setChnCommission(BigDecimal chnCommission) {
		this.chnCommission = chnCommission;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getContractMoney() {
		return contractMoney;
	}

	public void setContractMoney(BigDecimal contractMoney) {
		this.contractMoney = contractMoney;
	}

	public BigDecimal getfProfit() {
		return fProfit;
	}

	public void setfProfit(BigDecimal fProfit) {
		this.fProfit = fProfit;
	}

}
