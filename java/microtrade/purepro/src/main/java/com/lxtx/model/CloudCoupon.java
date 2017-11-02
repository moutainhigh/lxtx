package com.lxtx.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudCoupon implements Serializable {
	/**
	 * cloud_coupon.id
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Integer id;

	/**
	 * cloud_coupon.uid
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Integer uid;

	/**
	 * cloud_coupon.coupon_type (1:现金券 2:折扣券)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Integer couponType;

	/**
	 * cloud_coupon.status (0:未使用 1:已使用 2:已过期)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Integer status;

	/**
	 * cloud_coupon.add_time (生效期)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Date addTime;

	/**
	 * cloud_coupon.overdue_time (失效日期)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Date overdueTime;

	/**
	 * cloud_coupon.order_id
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Integer orderId;

	/**
	 * cloud_coupon.coupon_name (券名)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private String couponName;

	/**
	 * cloud_coupon.coupon_amount (金额)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Integer couponAmount;

	/**
	 * cloud_coupon.use_amount (使用金额：满多少可用)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Integer useAmount;

	/**
	 * cloud_coupon.rate (折扣率)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Double rate;

	/**
	 * cloud_coupon.notified (0)
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:33
	 */
	private Integer notified;
	// 生效标志：true 未使用可用 false：未使用，待生效
	private boolean usedFlag = true;

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

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAddTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (addTime != null) {
			return sdf.format(addTime);
		}
		return "";
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getOverdueTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (overdueTime != null) {
			return sdf.format(overdueTime);
		}
		return "";
	}

	public void setOverdueTime(Date overdueTime) {
		this.overdueTime = overdueTime;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public Integer getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(Integer couponAmount) {
		this.couponAmount = couponAmount;
	}

	public Integer getUseAmount() {
		return useAmount;
	}

	public void setUseAmount(Integer useAmount) {
		this.useAmount = useAmount;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Integer getNotified() {
		return notified;
	}

	public void setNotified(Integer notified) {
		this.notified = notified;
	}

	public boolean isUsedFlag() {
		return usedFlag;
	}

	public void setUsedFlag(boolean usedFlag) {
		this.usedFlag = usedFlag;
	}

}