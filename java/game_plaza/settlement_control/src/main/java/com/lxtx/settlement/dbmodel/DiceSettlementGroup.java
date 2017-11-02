package com.lxtx.settlement.dbmodel;

import com.lxtx.settlement.model.DiceGameResult;

public class DiceSettlementGroup extends BaseSettlementGroup<DiceGameResult> {
	private String result;
	private Integer result_1_num;
	private Integer result_2_num;
	private Integer result_3_num;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getResult_1_num() {
		return result_1_num;
	}

	public void setResult_1_num(Integer result_1_num) {
		this.result_1_num = result_1_num;
	}

	public Integer getResult_2_num() {
		return result_2_num;
	}

	public void setResult_2_num(Integer result_2_num) {
		this.result_2_num = result_2_num;
	}

	public Integer getResult_3_num() {
		return result_3_num;
	}

	public void setResult_3_num(Integer result_3_num) {
		this.result_3_num = result_3_num;
	}

	@Override
	public void setGameResult(DiceGameResult gameResult) {
		this.setResult(gameResult.dices[0] + "," + gameResult.dices[1] + "," + gameResult.dices[2]);
		this.setResult_1_num(gameResult.dices[0]);
		this.setResult_2_num(gameResult.dices[1]);
		this.setResult_3_num(gameResult.dices[2]);
	}

}
