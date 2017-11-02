package com.lxtx.settlement.utils;

public class AnimalDialSettlement {

	public static final int OPTION_TYPE_ANIMAL_BEGIN = 1;// 十二生肖开始索引
	public static final int OPTION_COUNT_FOR_ANIMAL = 12;
	public static final int OPTION_TYPE_WU_XING_BEGIN = OPTION_TYPE_ANIMAL_BEGIN + OPTION_COUNT_FOR_ANIMAL;// 五行开始索引,13
	public static final int OPTION_COUNT_FOR_WU_XING = 5;
	public static final int OPTION_TYPE_TIAN_DI_BEGIN = OPTION_TYPE_WU_XING_BEGIN + OPTION_COUNT_FOR_WU_XING;// 天地开始索引,18
	public static final int OPTION_COUNT_FOR_TIAN_DI = 2;
	public static final int OPTION_TYPE_COMBINED_TWO = OPTION_TYPE_TIAN_DI_BEGIN + OPTION_COUNT_FOR_TIAN_DI;// 20,二串一
	public static final int OPTION_TYPE_COMBINED_THREE = OPTION_TYPE_COMBINED_TWO + 1;// 21,三串一
	public static final int OPTION_TYPE_COUNT = OPTION_TYPE_COMBINED_THREE;

	public static final double MULTIPLE_FOR_ANIMAL = 12;
	public static final double MULTIPLE_FOR_TIAN_DI = 1.96;
	public static final double MULTIPLE_FOR_WUXING = 5;
	public static final double MULTIPLE_FOR_COMBINED_TWO = 24;
	public static final double MULTIPLE_FOR_COMBINED_THREE = 120;

	public static double[] getAllTypeMultiples(int[] combinedTwoDefine, int[] combinedThreeDefine, int tiandiIndex,
			int wuxingIndex, int animalIndex) {//
		assert (combinedTwoDefine.length == 2);
		assert (combinedThreeDefine.length == 3);
		assert (tiandiIndex >= 0 && tiandiIndex < 2);
		assert (wuxingIndex >= 0 && wuxingIndex < 5);
		assert (animalIndex >= 0 && animalIndex < 12);

		double[] multiples = new double[OPTION_TYPE_COMBINED_THREE];
		for (int i = 0; i < OPTION_COUNT_FOR_ANIMAL; i++) {// 生肖
			double multiple = (i == animalIndex ? MULTIPLE_FOR_ANIMAL : 0);
			multiples[OPTION_TYPE_ANIMAL_BEGIN - 1 + i] = multiple;
		}

		for (int i = 0; i < OPTION_COUNT_FOR_WU_XING; i++) {// 五行
			double multiple = (i == wuxingIndex ? MULTIPLE_FOR_WUXING : 0);
			multiples[OPTION_TYPE_WU_XING_BEGIN - 1 + i] = multiple;
		}

		for (int i = 0; i < OPTION_COUNT_FOR_TIAN_DI; i++) {// 天地
			double multiple = (i == tiandiIndex ? MULTIPLE_FOR_TIAN_DI : 0);
			multiples[OPTION_TYPE_TIAN_DI_BEGIN - 1 + i] = multiple;
		}

		boolean isCombinedTwoSuccess = (tiandiIndex == combinedTwoDefine[0] && animalIndex == combinedTwoDefine[1]);// 二串一
		multiples[OPTION_TYPE_COMBINED_TWO - 1] = isCombinedTwoSuccess ? MULTIPLE_FOR_COMBINED_TWO : 0;

		boolean isCombinedThreeSuccess = (tiandiIndex == combinedThreeDefine[0] && wuxingIndex == combinedThreeDefine[1]
				&& animalIndex == combinedThreeDefine[2]);// 三串一
		multiples[OPTION_TYPE_COMBINED_THREE - 1] = isCombinedThreeSuccess ? MULTIPLE_FOR_COMBINED_THREE : 0;
		return multiples;
	}
}
