package com.lxtech.game.plaza.db.model;

import java.math.BigDecimal;
import java.util.Date;

public class AnimalDialSettlementHistory {
	private Integer id;
	private Integer user_id;
	private Integer group_id;
	private Integer amount;
	private Integer target;
	private Date create_time;
	private Long settlement_result;

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

	public Integer getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Integer group_id) {
		this.group_id = group_id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Long getSettlement_result() {
		return settlement_result;
	}

	public void setSettlement_result(Long settlement_result) {
		this.settlement_result = settlement_result;
	}
}
