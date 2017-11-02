package com.lxtx.settlement.model;

public class AnimalGameResult extends BaseGameResult {

	public int resultTiandi;
	public int resultWuxing;
	public int resultAnimal;

	public String toString() {
		return resultTiandi + "," + resultWuxing + "," + resultAnimal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + resultAnimal;
		result = prime * result + resultTiandi;
		result = prime * result + resultWuxing;
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
		AnimalGameResult other = (AnimalGameResult) obj;
		if (resultAnimal != other.resultAnimal)
			return false;
		if (resultTiandi != other.resultTiandi)
			return false;
		if (resultWuxing != other.resultWuxing)
			return false;
		return true;
	}

}
