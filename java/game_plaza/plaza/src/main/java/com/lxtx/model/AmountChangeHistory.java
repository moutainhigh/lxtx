package com.lxtx.model;

import java.io.Serializable;
import java.util.Date;

public class AmountChangeHistory implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer user_id;
	private Integer amount_change;
	private String platform;
	private Date operator_time;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getAmount_change() {
		return amount_change;
	}

	public void setAmount_change(Integer amount_change) {
		this.amount_change = amount_change;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Date getOperator_time() {
		return operator_time;
	}

	public void setOperator_time(Date operator_time) {
		this.operator_time = operator_time;
	}

}