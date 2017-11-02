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

	private int contract_money;

	private double order_index;

	private int status;

	private double clear_upper_limit;

	private double clear_down_limit;
	
	private BigDecimal commission;
	
	private int limit;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

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

	public double getOrder_index() {
		return order_index;
	}

	public void setOrder_index(double order_index) {
		this.order_index = order_index;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getClear_upper_limit() {
		return clear_upper_limit;
	}

	public void setClear_upper_limit(double clear_upper_limit) {
		this.clear_upper_limit = clear_upper_limit;
	}

	public double getClear_down_limit() {
		return clear_down_limit;
	}

	public void setClear_down_limit(double clear_down_limit) {
		this.clear_down_limit = clear_down_limit;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public int getContract_money() {
		return contract_money;
	}

	public void setContract_money(int contract_money) {
		this.contract_money = contract_money;
	}
	
	
	
}
