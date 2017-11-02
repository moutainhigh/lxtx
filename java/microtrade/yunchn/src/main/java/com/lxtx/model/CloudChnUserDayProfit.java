package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudChnUserDayProfit implements Serializable {
	/**
	 * cloud_chn_user_day_profit.id
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private Integer id;

	/**
	 * cloud_chn_user_day_profit.uid
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private Integer uid;

	/**
	 * cloud_chn_user_day_profit.wxnm
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private String wxnm;

	/**
	 * cloud_chn_user_day_profit.chnno
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private String chnno;

	/**
	 * cloud_chn_user_day_profit.subject
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private String subject;

	/**
	 * cloud_chn_user_day_profit.limit
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private Integer limit;

	/**
	 * cloud_chn_user_day_profit.order_count
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private Integer orderCount;

	/**
	 * cloud_chn_user_day_profit.profit_count
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private Integer profitCount;

	/**
	 * cloud_chn_user_day_profit.loss_count
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private Integer lossCount;

	/**
	 * cloud_chn_user_day_profit.cash
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private BigDecimal cash;

	/**
	 * cloud_chn_user_day_profit.profit_amount
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private BigDecimal profitAmount;

	/**
	 * cloud_chn_user_day_profit.loss_amount
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private BigDecimal lossAmount;

	/**
	 * cloud_chn_user_day_profit.profit_loss
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private BigDecimal profitLoss;

	/**
	 * cloud_chn_user_day_profit.commission
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private BigDecimal commission;

	/**
	 * cloud_chn_user_day_profit.coupon_count
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private Integer couponCount;

	/**
	 * cloud_chn_user_day_profit.coupon_amount
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private BigDecimal couponAmount;

	/**
	 * cloud_chn_user_day_profit.date
	 * 
	 * @ibatorgenerated 2016-12-03 20:49:01
	 */
	private Date date;
	
	private BigDecimal fillAmount;
	private BigDecimal repayAmount;
	private BigDecimal balance;

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

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public Integer getProfitCount() {
		return profitCount;
	}

	public void setProfitCount(Integer profitCount) {
		this.profitCount = profitCount;
	}

	public Integer getLossCount() {
		return lossCount;
	}

	public void setLossCount(Integer lossCount) {
		this.lossCount = lossCount;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public BigDecimal getProfitAmount() {
		return profitAmount;
	}

	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}

	public BigDecimal getLossAmount() {
		return lossAmount;
	}

	public void setLossAmount(BigDecimal lossAmount) {
		this.lossAmount = lossAmount;
	}

	public BigDecimal getProfitLoss() {
		return profitLoss;
	}

	public void setProfitLoss(BigDecimal profitLoss) {
		this.profitLoss = profitLoss;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public Integer getCouponCount() {
		return couponCount;
	}

	public void setCouponCount(Integer couponCount) {
		this.couponCount = couponCount;
	}

	public BigDecimal getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}

	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null) {
			return sdf.format(date);
		}
		return "";
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getFillAmount() {
		return fillAmount;
	}

	public void setFillAmount(BigDecimal fillAmount) {
		this.fillAmount = fillAmount;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}