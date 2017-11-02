package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CloudDayCensus implements Serializable {
	/**
	 * cloud_day_census.id
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private Integer id;

	/**
	 * cloud_day_census.order_date (截止日终当天日期)
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private Date orderDate;

	/**
	 * cloud_day_census.sub1_sum
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private Integer sub1Sum;

	/**
	 * cloud_day_census.sub2_sum
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private Integer sub2Sum;

	/**
	 * cloud_day_census.sub3_sum
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private Integer sub3Sum;

	/**
	 * cloud_day_census.sub4_sum
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private Integer sub4Sum;

	/**
	 * cloud_day_census.all_sum (订单总量)
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private Integer allSum;

	/**
	 * cloud_day_census.profit (日盈利 头天停牌至当日停牌)
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private BigDecimal profit;

	/**
	 * cloud_day_census.commission_profit (手续费收益)
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private BigDecimal commissionProfit;

	private BigDecimal chnCommission;

	/**
	 * cloud_day_census.loss
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private BigDecimal loss;

	/**
	 * cloud_day_census.profit_loss (日统计盈亏)
	 * 
	 * @ibatorgenerated 2016-11-10 15:49:20
	 */
	private BigDecimal profitLoss;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Integer getSub1Sum() {
		return sub1Sum;
	}

	public void setSub1Sum(Integer sub1Sum) {
		this.sub1Sum = sub1Sum;
	}

	public Integer getSub2Sum() {
		return sub2Sum;
	}

	public void setSub2Sum(Integer sub2Sum) {
		this.sub2Sum = sub2Sum;
	}

	public Integer getSub3Sum() {
		return sub3Sum;
	}

	public void setSub3Sum(Integer sub3Sum) {
		this.sub3Sum = sub3Sum;
	}

	public Integer getSub4Sum() {
		return sub4Sum;
	}

	public void setSub4Sum(Integer sub4Sum) {
		this.sub4Sum = sub4Sum;
	}

	public Integer getAllSum() {
		return allSum;
	}

	public void setAllSum(Integer allSum) {
		this.allSum = allSum;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getCommissionProfit() {
		return commissionProfit;
	}

	public void setCommissionProfit(BigDecimal commissionProfit) {
		this.commissionProfit = commissionProfit;
	}

	public BigDecimal getLoss() {
		return loss;
	}

	public void setLoss(BigDecimal loss) {
		this.loss = loss;
	}

	public BigDecimal getProfitLoss() {
		return profitLoss;
	}

	public void setProfitLoss(BigDecimal profitLoss) {
		this.profitLoss = profitLoss;
	}

	public BigDecimal getChnCommission() {
		return chnCommission;
	}

	public void setChnCommission(BigDecimal chnCommission) {
		this.chnCommission = chnCommission;
	}

}