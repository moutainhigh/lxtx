package com.lxtx.settlement.model;

public class CarDialGameResult extends BaseGameResult {
	public int result;
	public int result_flag;

	public String toString() {
		return result + "," + result_flag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.result;
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
		CarDialGameResult other = (CarDialGameResult) obj;
		if (result != other.result)
			return false;
		return true;
	}

}
