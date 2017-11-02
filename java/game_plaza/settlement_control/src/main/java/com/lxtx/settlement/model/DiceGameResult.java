package com.lxtx.settlement.model;

import java.util.Arrays;

public class DiceGameResult extends BaseGameResult {
	public int[] dices;

	public String toString() {
		return dices[0] + "," + dices[1] + "," + dices[2];
	}

	private int[] getSortedDices() {
		int[] temp = Arrays.copyOf(dices, dices.length);
		Arrays.sort(temp);
		return temp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(getSortedDices());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiceGameResult other = (DiceGameResult) obj;
		if (!Arrays.equals(this.getSortedDices(), other.getSortedDices()))
			return false;
		return true;
	}

}
