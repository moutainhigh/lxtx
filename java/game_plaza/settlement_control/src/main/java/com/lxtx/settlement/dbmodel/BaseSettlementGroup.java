package com.lxtx.settlement.dbmodel;

import java.util.Date;

import com.lxtx.settlement.model.BaseGameResult;

public abstract class BaseSettlementGroup<T extends BaseGameResult> {
	private Integer id;
	private Integer banker_id;
	private Long banker_carry_amount;
	private Date start_time;
	private Date end_time;
	private Integer state;
	private Long banker_settlerment_result;
	private Long system_settlement_result;

	public abstract void setGameResult(T gameResult);

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

	public Long getBanker_settlerment_result() {
		return banker_settlerment_result;
	}

	public void setBanker_settlerment_result(Long banker_settlerment_result) {
		this.banker_settlerment_result = banker_settlerment_result;
	}

	public Long getSystem_settlement_result() {
		return system_settlement_result;
	}

	public void setSystem_settlement_result(Long system_settlement_result) {
		this.system_settlement_result = system_settlement_result;
	}

}
