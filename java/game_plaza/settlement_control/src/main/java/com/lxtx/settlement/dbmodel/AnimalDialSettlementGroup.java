package com.lxtx.settlement.dbmodel;

import com.lxtx.settlement.model.AnimalGameResult;

public class AnimalDialSettlementGroup extends BaseSettlementGroup<AnimalGameResult> {
	private Integer combined_two_tiandi;
	private Integer combined_two_animal;
	private Integer combined_three_tiandi;
	private Integer combined_three_wuxing;
	private Integer combined_three_animal;
	private Integer result_tiandi;
	private Integer result_wuxing;
	private Integer result_animal;

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

	@Override
	public void setGameResult(AnimalGameResult gameResult) {
		this.setResult_tiandi(gameResult.resultTiandi);
		this.setResult_wuxing(gameResult.resultWuxing);
		this.setResult_animal(gameResult.resultAnimal);
	}
}
