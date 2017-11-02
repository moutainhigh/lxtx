package com.lxtech.game.plaza.db.model;

import java.util.Date;

public class AnimalDialSettlementGroup {
	private Integer id;
	private Integer banker_id;
	private Long banker_carry_amount;
	private Date start_time;
	private Integer combined_two_tiandi;
	private Integer combined_two_animal;
	private Integer combined_three_tiandi;
	private Integer combined_three_wuxing;
	private Integer combined_three_animal;
	private Date end_time;
	private Integer result_tiandi;
	private Integer result_wuxing;
	private Integer result_animal;
	private Integer state;
	private Long banker_settlement_result;

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

	public Long getBanker_settlement_result() {
		return banker_settlement_result;
	}

	public void setBanker_settlement_result(Long banker_settlerment_result) {
		this.banker_settlement_result = banker_settlerment_result;
	}

	public Integer getCombined_two_tiandi() {
		return combined_two_tiandi;
	}

	public void setCombined_two_tiandi(Integer combined_two_tiandi) {
		this.combined_two_tiandi = combined_two_tiandi;
	}

	public Integer getCombined_two_animal() {
		return combined_two_animal;
	}

	public void setCombined_two_animal(Integer combined_two_animal) {
		this.combined_two_animal = combined_two_animal;
	}

	public Integer getCombined_three_tiandi() {
		return combined_three_tiandi;
	}

	public void setCombined_three_tiandi(Integer combined_three_tiandi) {
		this.combined_three_tiandi = combined_three_tiandi;
	}

	public Integer getCombined_three_wuxing() {
		return combined_three_wuxing;
	}

	public void setCombined_three_wuxing(Integer combined_three_wuxing) {
		this.combined_three_wuxing = combined_three_wuxing;
	}

	public Integer getCombined_three_animal() {
		return combined_three_animal;
	}

	public void setCombined_three_animal(Integer combined_three_animal) {
		this.combined_three_animal = combined_three_animal;
	}

	public Integer getResult_tiandi() {
		return result_tiandi;
	}

	public void setResult_tiandi(Integer result_tiandi) {
		this.result_tiandi = result_tiandi;
	}

	public Integer getResult_wuxing() {
		return result_wuxing;
	}

	public void setResult_wuxing(Integer result_wuxing) {
		this.result_wuxing = result_wuxing;
	}

	public Integer getResult_animal() {
		return result_animal;
	}

	public void setResult_animal(Integer result_animal) {
		this.result_animal = result_animal;
	}

}
