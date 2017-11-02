package com.lxtx.model.vo;

import java.io.Serializable;

public class CloudChnSumProfitVo implements Serializable {

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
	private String profit;

	/**
	 * cloud_chn_day_profit.loss (亏损)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String loss;

	/**
	 * cloud_chn_day_profit.commission (手续费)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String commission;

	/**
	 * cloud_chn_day_profit.coupon_commission (用券手续费)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String couponCommission;

	/**
	 * cloud_chn_day_profit.add_user (新增用户量)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String addUser;

	/**
	 * cloud_chn_day_profit.order_count (平仓订单数量)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String orderCount;

	/**
	 * cloud_chn_day_profit.chn_commission (渠道手续费)
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String chnCommission;

	/**
	 * cloud_chn_day_profit.date
	 * 
	 * @ibatorgenerated 2016-11-27 21:39:09
	 */
	private String date;

	// 交易金额
	private String contractMoney;
	// 总盈亏
	private String fProfit;

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

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public String getLoss() {
		return loss;
	}

	public void setLoss(String loss) {
		this.loss = loss;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getCouponCommission() {
		return couponCommission;
	}

	public void setCouponCommission(String couponCommission) {
		this.couponCommission = couponCommission;
	}

	public String getAddUser() {
		return addUser;
	}

	public void setAddUser(String addUser) {
		this.addUser = addUser;
	}

	public String getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(String orderCount) {
		this.orderCount = orderCount;
	}

	public String getChnCommission() {
		return chnCommission;
	}

	public void setChnCommission(String chnCommission) {
		this.chnCommission = chnCommission;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContractMoney() {
		return contractMoney;
	}

	public void setContractMoney(String contractMoney) {
		this.contractMoney = contractMoney;
	}

	public String getfProfit() {
		return fProfit;
	}

	public void setfProfit(String fProfit) {
		this.fProfit = fProfit;
	}

}
