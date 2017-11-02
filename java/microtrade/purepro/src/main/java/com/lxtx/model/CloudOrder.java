package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudOrder implements Serializable {
	/**
	 * cloud_order.id
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer id;

	/**
	 * cloud_order.uid
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer uid;

	/**
	 * cloud_order.chnno
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private String chnno;

	/**
	 * cloud_order.subject
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private String subject;

	/**
	 * cloud_order.subject_id (标的物id)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer subjectId;

	/**
	 * cloud_order.direction (1:做多2:做空)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer direction;

	/**
	 * cloud_order.money
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal money;

	/**
	 * cloud_order.amount
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer amount;

	/**
	 * cloud_order.contract_money
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal contractMoney;

	/**
	 * cloud_order.limit (止损点)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer limit;

	/**
	 * cloud_order.cleared (是否完成结算)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer cleared;

	/**
	 * cloud_order.order_time
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Date orderTime;

	/**
	 * cloud_order.clear_time
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Date clearTime;

	/**
	 * cloud_order.order_index
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer orderIndex;

	/**
	 * cloud_order.clear_index
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer clearIndex;

	/**
	 * cloud_order.commission (手续费)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal commission;

	/**
	 * cloud_order.status (结算状态,0.未结算,1:处理中,2:赚钱,3:输钱,4:日终自动处理)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer status;

	/**
	 * cloud_order.clear_upper_limit (结算的上限值)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer clearUpperLimit;

	/**
	 * cloud_order.clear_down_limit (结算的下限值)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private Integer clearDownLimit;

	/**
	 * cloud_order.coupon_commission (用券手续费)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal couponCommission;

	/**
	 * cloud_order.cash (头寸：押金)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal cash;

	/**
	 * cloud_order.sys_profit (系统赢时的金额)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal sysProfit;

	/**
	 * cloud_order.sys_loss (系统输时的金额)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal sysLoss;

	/**
	 * cloud_order.f_profit (最终系统盈利金额)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal fProfit;

	/**
	 * cloud_order.coupou_money (用券抵扣金额)
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:45
	 */
	private BigDecimal coupouMoney;
	
	private Integer hidden;

	// 0 未交付 else 1 已平仓
	private String dealkind;
	// 2赚、3亏、 退1
	private String perStatus;
	private String clearTypeNm;
	// 赚亏样式
	private String perStyle;
	// 看涨 看跌
	private String orderType;
	// 用户显示利润
	private BigDecimal profit;
	// 标的物名
	private String cname;

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

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public BigDecimal getContractMoney() {
		return contractMoney;
	}

	public void setContractMoney(BigDecimal contractMoney) {
		this.contractMoney = contractMoney;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getCleared() {
		return cleared;
	}

	public void setCleared(Integer cleared) {
		this.cleared = cleared;
	}

	public String getOrderTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (orderTime != null) {
			return sdf.format(orderTime);
		}
		return "";
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public String getClearTime1() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (clearTime != null) {
			return sdf.format(clearTime);
		}
		return "";
	}

	public Date getClearTime() {
		return clearTime;
	}

	public void setClearTime(Date clearTime) {
		this.clearTime = clearTime;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public Integer getClearIndex() {
		return clearIndex;
	}

	public void setClearIndex(Integer clearIndex) {
		this.clearIndex = clearIndex;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getClearUpperLimit() {
		return clearUpperLimit;
	}

	public void setClearUpperLimit(Integer clearUpperLimit) {
		this.clearUpperLimit = clearUpperLimit;
	}

	public Integer getClearDownLimit() {
		return clearDownLimit;
	}

	public void setClearDownLimit(Integer clearDownLimit) {
		this.clearDownLimit = clearDownLimit;
	}

	public BigDecimal getCouponCommission() {
		return couponCommission;
	}

	public void setCouponCommission(BigDecimal couponCommission) {
		this.couponCommission = couponCommission;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public BigDecimal getSysProfit() {
		return sysProfit;
	}

	public void setSysProfit(BigDecimal sysProfit) {
		this.sysProfit = sysProfit;
	}

	public BigDecimal getSysLoss() {
		return sysLoss;
	}

	public void setSysLoss(BigDecimal sysLoss) {
		this.sysLoss = sysLoss;
	}

	public BigDecimal getfProfit() {
		return fProfit;
	}

	public void setfProfit(BigDecimal fProfit) {
		this.fProfit = fProfit;
	}

	public BigDecimal getCoupouMoney() {
		return coupouMoney;
	}

	public void setCoupouMoney(BigDecimal coupouMoney) {
		this.coupouMoney = coupouMoney;
	}

	public String getDealkind() {
		return dealkind;
	}

	public void setDealkind(String dealkind) {
		this.dealkind = dealkind;
	}

	public String getPerStatus() {
		return perStatus;
	}

	public void setPerStatus(String perStatus) {
		this.perStatus = perStatus;
	}

	public String getClearTypeNm() {
		return clearTypeNm;
	}

	public void setClearTypeNm(String clearTypeNm) {
		this.clearTypeNm = clearTypeNm;
	}

	public String getPerStyle() {
		return perStyle;
	}

	public void setPerStyle(String perStyle) {
		this.perStyle = perStyle;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public Integer getHidden() {
		return hidden;
	}

	public void setHidden(Integer hidden) {
		this.hidden = hidden;
	}
}