package com.lxtech.game.plaza.db.model;

import java.util.Date;

public class DiceSettlementGroup {
	private Long id;
	private Long banker_id;
	private Long banker_carry_amount;
	private Date start_time;
	private Date end_time;
	private String result;
	private Integer state;
	private Long banker_settlement_result;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBanker_id() {
		return banker_id;
	}

	public void setBanker_id(long banker_id) {
		this.banker_id = banker_id;
	}

	public Long getBanker_carry_amount() {
		return banker_carry_amount;
	}

	public void setBanker_carry_amount(Long banker_carry_amount) {
		this.banker_carry_amount = banker_carry_amount;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getBanker_settlement_result() {
		return banker_settlement_result;
	}

	public void setBanker_settlement_result(Long banker_settlement_result) {
		this.banker_settlement_result = banker_settlement_result;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setBanker_id(Long banker_id) {
		this.banker_id = banker_id;
	}

}
