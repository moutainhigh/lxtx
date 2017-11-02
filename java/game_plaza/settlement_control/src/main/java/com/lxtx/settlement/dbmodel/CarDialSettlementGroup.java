package com.lxtx.settlement.dbmodel;

import com.lxtx.settlement.model.CarDialGameResult;

public class CarDialSettlementGroup extends BaseSettlementGroup<CarDialGameResult> {
	private Integer result;
	private Integer result_flag;

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getResult_flag() {
		return result_flag;
	}

	public void setResult_flag(Integer result_flag) {
		this.result_flag = result_flag;
	}

	@Override
	public void setGameResult(CarDialGameResult gameResult) {
		this.setResult(gameResult.result);
		this.setResult_flag(gameResult.result_flag);
	}
}
