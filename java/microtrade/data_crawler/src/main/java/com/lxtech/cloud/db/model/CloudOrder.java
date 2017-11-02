package com.lxtech.cloud.db.model;

import java.math.BigDecimal;

public class CloudOrder {
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private long id;

	private long uid;

	private String subject;

	private int direction;

	private BigDecimal money;

	private int order_index;

	private int status;

	private int clear_upper_limit;

	private int clear_down_limit;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public int getOrder_index() {
		return order_index;
	}

	public void setOrder_index(int order_index) {
		this.order_index = order_index;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getClear_upper_limit() {
		return clear_upper_limit;
	}

	public void setClear_upper_limit(int clear_upper_limit) {
		this.clear_upper_limit = clear_upper_limit;
	}

	public int getClear_down_limit() {
		return clear_down_limit;
	}

	public void setClear_down_limit(int clear_down_limit) {
		this.clear_down_limit = clear_down_limit;
	}

}
