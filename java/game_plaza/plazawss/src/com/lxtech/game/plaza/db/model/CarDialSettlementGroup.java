package com.lxtech.game.plaza.db.model;

import java.util.Date;

public class CarDialSettlementGroup {
	private Integer id;
	private Integer banker_id;
	private Long banker_carry_amount;
	private Date start_time;
	private Date end_time;
	private Integer result;
	private Integer state;
	private Long banker_settlement_result;
	private Integer result_flag;

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

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBanker_id() {
		return banker_id;
	}

	public void setBanker_id(Integer banker_id) {
		this.banker_id = banker_id;
	}

	public Long getBanker_settlement_result() {
		return banker_settlement_result;
	}

	public void setBanker_settlement_result(Long banker_settlerment_result) {
		this.banker_settlement_result = banker_settlerment_result;
	}

	public Integer getResult_flag() {
		return result_flag;
	}

	public void setResult_flag(Integer result_flag) {
		this.result_flag = result_flag;
	}

}
