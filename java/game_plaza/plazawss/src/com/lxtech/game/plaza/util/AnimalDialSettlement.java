package com.lxtech.game.plaza.util;

import java.util.ArrayList;
import java.util.List;

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

	public static long getOptionMaxBetCount(int[] combinedTwoOptions, int[] combinedThreeOptions,
			long bankerAmountCount, long[] existBetAmountCounts, int option) {
		List<Integer> sameTimeOptions = getSameTimeOptions(combinedTwoOptions, combinedThreeOptions, option);
		long bankerMaybeMax = bankerAmountCount;
		for (int i = 0; i < OPTION_TYPE_COUNT; i++) {
			int currentOption = i + 1;
			if (currentOption == option) {
			} else if (sameTimeOptions.contains(currentOption)) {
				bankerMaybeMax -= existBetAmountCounts[i] * getMultiples(currentOption);
			} else {
				bankerMaybeMax += existBetAmountCounts[i];
			}
		}
		double multiple = getMultiples(option);
		return (long) (bankerMaybeMax / multiple - existBetAmountCounts[option - 1]);
	}

	private static double getMultiples(int option) {//
		assert (option >= OPTION_TYPE_ANIMAL_BEGIN && option <= OPTION_TYPE_COUNT);
		if (option < OPTION_TYPE_WU_XING_BEGIN) {
			return MULTIPLE_FOR_ANIMAL;
		} else if (option < OPTION_TYPE_TIAN_DI_BEGIN) {
			return MULTIPLE_FOR_WUXING;
		} else if (option < OPTION_TYPE_COMBINED_TWO) {
			return MULTIPLE_FOR_TIAN_DI;
		} else if (option == OPTION_TYPE_COMBINED_TWO) {
			return MULTIPLE_FOR_COMBINED_TWO;
		} else if (option == OPTION_TYPE_COMBINED_THREE) {
			return MULTIPLE_FOR_COMBINED_THREE;
		}
		return 0;
	}

	protected static List<Integer> getSameTimeOptions(int[] combinedTwoOptions, int[] combinedThreeOptions,
			int option) {
		List<Integer> sameTimeOptions = new ArrayList<>();
		if (option == OPTION_TYPE_COMBINED_TWO) {
			sameTimeOptions.add(combinedTwoOptions[0]);
			sameTimeOptions.add(combinedTwoOptions[1]);
		} else if (option == OPTION_TYPE_COMBINED_THREE) {
			sameTimeOptions.add(combinedThreeOptions[0]);
			sameTimeOptions.add(combinedThreeOptions[1]);
			sameTimeOptions.add(combinedThreeOptions[2]);
		} else {
			for (int i = 0; i < 2; i++) {
				if (combinedTwoOptions[i] == option) {
					sameTimeOptions.add(OPTION_TYPE_COMBINED_TWO);
					break;
				}
			}
			for (int i = 0; i < 3; i++) {
				if (combinedThreeOptions[i] == option) {
					sameTimeOptions.add(OPTION_TYPE_COMBINED_THREE);
					break;
				}
			}
		}
		return sameTimeOptions;
	}

	private static void assetEquals(long l1, long l2) {
		if (l1 != l2) {
			throw new RuntimeException("not equals:" + l1 + " : " + l2);
		}
	}

	public static void main(String[] args) {
		long bankerAmountCount = 10000 * 10000;
		long[] existBetCounts = new long[OPTION_TYPE_COUNT];
		/*
		{
			int option = 1;
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (long) (bankerAmountCount / (MULTIPLE_FOR_LUXURY - 1)));
		}
		{
			int option = 2;
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (long) (bankerAmountCount / (MULTIPLE_FOR_NORMAL - 1)));
		}

		existBetCounts[0] = 100;
		existBetCounts[1] = 200;
		{
			int option = 1;
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (long) ((bankerAmountCount + 200) / (MULTIPLE_FOR_LUXURY - 1)) - 100);
		}
		{
			int option = 2;
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (long) ((bankerAmountCount + 100) / (MULTIPLE_FOR_NORMAL - 1)) - 200);
		}
		{
			int option = 3;
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (long) ((bankerAmountCount + 100 + 200) / (MULTIPLE_FOR_LUXURY - 1)));
		}
		*/
	}
}
