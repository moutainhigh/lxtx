package com.lxtech.cloud.db.model;

import java.util.Date;

public class CloudCoupon {
	private long uid;
	
	private int coupon_type;
	
	private int status;
	
	private Date add_time;
	
	private Date overdue_time;
	
	private long order_id;
	
	private String coupon_name;
	
	private int coupon_amount;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getCoupon_type() {
		return coupon_type;
	}

	public void setCoupon_type(int coupon_type) {
		this.coupon_type = coupon_type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAdd_time() {
		return add_time;
	}

	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}

	public Date getOverdue_time() {
		return overdue_time;
	}

	public void setOverdue_time(Date overdue_time) {
		this.overdue_time = overdue_time;
	}

	public long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}

	public String getCoupon_name() {
		return coupon_name;
	}

	public void setCoupon_name(String coupon_name) {
		this.coupon_name = coupon_name;
	}

	public int getCoupon_amount() {
		return coupon_amount;
	}

	public void setCoupon_amount(int coupon_amount) {
		this.coupon_amount = coupon_amount;
	}
	
	
}
