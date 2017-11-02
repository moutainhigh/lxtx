package com.lxtech.game.plaza.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiceSettlement {
	private static BaseDiceType[] sumDiceTypes;

	public static final int DICE_TYPE_LITTLE_OPTION = 1;// 小
	public static final int DICE_TYPE_LARGE_OPTION = 2;
	public static final int DICE_TYPE_BAO_ZI_TOTAL_OPTION = 3;
	public static final int DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN = 4;
	public static final int DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN = 10;
	public static final int DICE_TYPE_SUM_VALUE_OPTION_BEGIN = 16;
	public static final int DICE_TYPE_SINGLE_OPTION_BEGIN = 30;
	public static final int SUM_VALUE_VALUE_MIN = 4;
	public static final int SUM_VALUE_VALUE_MAX = 17;

	public static final double DICE_TYPE_LITTLE_MULTIPLE = 1.96;
	public static final double DICE_TYPE_LARGE_MULTIPLE = 1.96;
	public static final double DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE = 36;
	public static final double DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE = 200;
	public static final double DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE = 10;
	public static final double[] DICE_TYPE_SUM_VALUE_MULTIPLES = { 50, 18, 14, 12, 8, 6, 6, 6, 6, 8, 12, 14, 18, 50 };// 4-17
	public static final double[] DICE_TYPE_SINGLE_MULTIPLES = { 0, 1, 2, 3 };// 押中1，2，3分别的赔率

	static {
		loadSumDiceTypes();
	}

	public static void main(String[] args) {
		long bankerAmountCount = 10000 * 10000;
		long[] existBetCounts = new long[35];
		int option = DICE_TYPE_LITTLE_OPTION;
		{
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (bankerAmountCount / (DICE_TYPE_LITTLE_MULTIPLE - 1)));
		}
		option = DICE_TYPE_BAO_ZI_TOTAL_OPTION;
		{
			long except = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(except, (int) (bankerAmountCount / (DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE - 1)));
		}
		option = DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN;
		{
			long except = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(except, (int) (bankerAmountCount / (DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE - 1)));
		}
		option = DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN;
		{
			long except = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(except, (int) (bankerAmountCount / (DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE - 1)));
		}
		option = DICE_TYPE_SUM_VALUE_OPTION_BEGIN;
		{
			long except = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(except, (int) (bankerAmountCount / (DICE_TYPE_SUM_VALUE_MULTIPLES[0] - 1)));
		}
		option = DICE_TYPE_SUM_VALUE_OPTION_BEGIN + 2;
		{
			long except = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(except, (int) (bankerAmountCount / (DICE_TYPE_SUM_VALUE_MULTIPLES[2] - 1)));
		}

		existBetCounts[DICE_TYPE_LITTLE_OPTION - 1] = 1300;
		existBetCounts[DICE_TYPE_LARGE_OPTION - 1] = 200;
		existBetCounts[DICE_TYPE_BAO_ZI_TOTAL_OPTION - 1] = 300;
		existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 0] = 400;
		existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 1] = 500;
		existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 2] = 600;
		existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 3] = 700;
		existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 4] = 800;
		existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 5] = 900;

		existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 0] = 1000;
		existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 1] = 1100;
		existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 2] = 1200;
		existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 3] = 1300;
		existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 4] = 1400;
		existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 5] = 1500;

		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 0] = 1600;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 1] = 1700;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 2] = 1800;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 3] = 1900;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 4] = 2000;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 5] = 2100;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 6] = 2200;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 7] = 2300;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 8] = 2400;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 9] = 2500;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 10] = 2600;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 11] = 2700;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 12] = 2800;
		existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 13] = 2900;

		existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 0] = 3000;
		existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 1] = 3100;
		existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 2] = 3200;
		existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 3] = 3300;
		existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 4] = 3400;
		existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 5] = 3500;
		int sumBetCounts = 0;
		for (int i = 0; i < existBetCounts.length; i++) {
			sumBetCounts += existBetCounts[i];
		}
		option = DICE_TYPE_LITTLE_OPTION;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 3]
					* DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE);// 排除对5，6之后的可能最大赔的值
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 0]
					* DICE_TYPE_SUM_VALUE_MULTIPLES[0]);// 赔钱最多的选项
			for (int i = 0; i < 6; i++) {
				virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + i]
						* DICE_TYPE_SINGLE_MULTIPLES[3]);//
			}
			/*
			 * virtualBankerAmountCount -= (int)
			 * (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 3]
			 * DICE_TYPE_SINGLE_MULTIPLES[3]);// virtualBankerAmountCount -=
			 * (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 4]
			 * DICE_TYPE_SINGLE_MULTIPLES[3]);// virtualBankerAmountCount -=
			 * (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 5]
			 * DICE_TYPE_SINGLE_MULTIPLES[3]);//
			 */
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count,
					(int) (virtualBankerAmountCount / (DICE_TYPE_LITTLE_MULTIPLE - 1) - existBetCounts[option - 1]));
		}
		option = DICE_TYPE_BAO_ZI_TOTAL_OPTION;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 5]
					* DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE);// 排除可能最大赔的值
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 5]
					* DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE);// 排除可能最大赔的值
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 13]
					* DICE_TYPE_SUM_VALUE_MULTIPLES[13]);// 赔钱最多的选项
			for (int i = 0; i < 6; i++) {
				virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + i]
						* DICE_TYPE_SINGLE_MULTIPLES[3]);//
			}
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE - 1)
					- existBetCounts[option - 1]));
		}
		option = DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 0;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_TOTAL_OPTION - 1]
					* DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);// 排除豹子通选
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 0]
					* DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE);// 排除对1
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 0]
					* DICE_TYPE_SINGLE_MULTIPLES[3]);
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE - 1)
					- existBetCounts[option - 1]));
		}
		option = DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 1;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_TOTAL_OPTION - 1]
					* DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);// 排除豹子通选
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 1]
					* DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE);// 排除对2
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 2]
					* DICE_TYPE_SUM_VALUE_MULTIPLES[2]);// 排除sum6
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 1]
					* DICE_TYPE_SINGLE_MULTIPLES[3]);
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE - 1)
					- existBetCounts[option - 1]));
		}
		option = DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 5;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_TOTAL_OPTION - 1]
					* DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);// 排除豹子通选
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN - 1 + 5]
					* DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE);// 排除对6
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN - 1 + 5]
					* DICE_TYPE_SINGLE_MULTIPLES[3]);
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE - 1)
					- existBetCounts[option - 1]));
		}
		option = DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 0;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_LITTLE_OPTION - 1] * DICE_TYPE_LITTLE_MULTIPLE);//
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_TOTAL_OPTION - 1]
					* DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);// 排除豹子通选
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 0]
					* DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE);// 排除1
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 0]
					* DICE_TYPE_SUM_VALUE_MULTIPLES[0]);// 45678中最大的
			for (int i = 0; i < 6; i++) {
				virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN -1 + i]
						* DICE_TYPE_SINGLE_MULTIPLES[3]);//
			}
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE - 1)
					- existBetCounts[option - 1]));
		}
		option = DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 1;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_LITTLE_OPTION - 1] * DICE_TYPE_LITTLE_MULTIPLE);//
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_TOTAL_OPTION - 1]
					* DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);// 排除豹子通选
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 1]
					* DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE);// 排除2
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 1]
					* DICE_TYPE_SUM_VALUE_MULTIPLES[1]);// 56789 10中最大的
			for (int i = 0; i < 6; i++) {
				virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN -1 + i]
						* DICE_TYPE_SINGLE_MULTIPLES[3]);//
			}
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE - 1)
					- existBetCounts[option - 1]));
		}
		option = DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 2;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_LARGE_OPTION - 1] * DICE_TYPE_LARGE_MULTIPLE);//
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_LITTLE_OPTION - 1] * DICE_TYPE_LITTLE_MULTIPLE);//
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_TOTAL_OPTION - 1]
					* DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);// 排除豹子通选
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 2]
					* DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE);// 排除3
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 3]
					* DICE_TYPE_SUM_VALUE_MULTIPLES[3]);// 789 10 11 12中最大的
			for (int i = 0; i < 6; i++) {
				virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN -1 + i]
						* DICE_TYPE_SINGLE_MULTIPLES[3]);//
			}
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE - 1)
					- existBetCounts[option - 1]));
		}
		option = DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 5;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_LARGE_OPTION - 1] * DICE_TYPE_LARGE_MULTIPLE);//
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_TOTAL_OPTION - 1]
					* DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);// 排除豹子通选
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN - 1 + 5]
					* DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE);// 排除3
			virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SUM_VALUE_OPTION_BEGIN - 1 + 13]
					* DICE_TYPE_SUM_VALUE_MULTIPLES[13]);// 13 14 15 16 17中最大的
			for (int i = 0; i < 6; i++) {
				virtualBankerAmountCount -= (int) (existBetCounts[DICE_TYPE_SINGLE_OPTION_BEGIN -1 + i]
						* DICE_TYPE_SINGLE_MULTIPLES[3]);//
			}
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE - 1)
					- existBetCounts[option - 1]));
		}
		option = DICE_TYPE_SUM_VALUE_OPTION_BEGIN + 0;
		{
			long virtualBankerAmountCount = bankerAmountCount + sumBetCounts - existBetCounts[option - 1];
			
			long count = getOptionMaxBetCount(bankerAmountCount, existBetCounts, option);
			//assetEquals(count, (int) (virtualBankerAmountCount / (DICE_TYPE_SUM_VALUE_MULTIPLES[0] - 1) - existBetCounts[option - 1]));
		}
	}

	private static void assetEquals(long l1, long l2) {
		if (l1 != l2) {
			throw new RuntimeException("not equals:" + l1 + " : " + l2);
		}
	}

	private static void setDiceType(BaseDiceType diceType) {
		sumDiceTypes[diceType.getIndex() - 1] = diceType;
	}

	private static void setDiceType(BaseDiceType[] diceTypes) {
		for (BaseDiceType type : diceTypes) {
			setDiceType(type);
		}
	}

	private static void loadSumDiceTypes() {
		sumDiceTypes = new BaseDiceType[35];
		setDiceType(new DiceTypeLittle());
		setDiceType(new DiceTypeLarge());
		setDiceType(new DiceTypeBaoziTotal());
		setDiceType(DiceTypeBaoziSingle.getSum());
		setDiceType(DiceTypeDuiZiSingle.getSum());
		setDiceType(DiceTypeSumValue.getSum());
		setDiceType(DiceTypeSingle.getSum());
	}

	public static BaseDiceType getDiceType(int option) {
		return sumDiceTypes[option - 1];
	}

	private static void addInteger(Set<Integer> list, int value) {
		boolean success = list.add(value);
		if (!success) {
			throw new RuntimeException("have same value:" + value);
		}
	}

	private static void addIntegers(Set<Integer> list, int begin, int end) {
		for (int i = begin; i <= end; i++) {
			addInteger(list, i);
		}
	}

	public static long getOptionMaxBetCount(long bankerAmountCount, long[] existBetAmountCounts, int option) {
		Set<Integer> rejectionOptions = getRejectionOptions(option);
		addRejectionOptionsWithoutMax(existBetAmountCounts, rejectionOptions, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN,
				DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
		addRejectionOptionsWithoutMax(existBetAmountCounts, rejectionOptions, DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN,
				DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
		addRejectionOptionsWithoutMax(existBetAmountCounts, rejectionOptions, DICE_TYPE_SUM_VALUE_OPTION_BEGIN,
				DICE_TYPE_SUM_VALUE_OPTION_BEGIN + SUM_VALUE_VALUE_MAX - SUM_VALUE_VALUE_MIN);
		addRejectionOptionsWithoutMax(existBetAmountCounts, rejectionOptions, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN,
				DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
		/*
		 * List<Object[]> sortSingle = new ArrayList<>(); for (int i = 0; i < 6;
		 * i++) { int tempOption = DICE_TYPE_SINGLE_OPTION_BEGIN + i; double
		 * settlement = existBetAmountCounts[tempOption - 1] *
		 * getDiceType(tempOption).getMultiple(); int j; for (j = 0; j <
		 * sortSingle.size(); j++) { if (settlement > (Double)
		 * sortSingle.get(j)[1]) { break; } } sortSingle.add(j, new Object[] {
		 * tempOption, settlement }); } int excludeCount = 0; for (int i = 0; i
		 * < 6; i++) { Integer tempOption = (Integer) sortSingle.get(i)[0]; if
		 * (excludeCount < 3 && !rejectionOptions.contains(tempOption)) {
		 * excludeCount++; } if (excludeCount > 3) {
		 * rejectionOptions.add(tempOption); } }
		 */

		// addRejectionOptionsWithoutMax(existBetAmountCounts, rejectionOptions,
		// DICE_TYPE_SINGLE_OPTION_BEGIN, DICE_TYPE_SINGLE_OPTION_BEGIN + 6 -
		// 1);
		long bankerMaybeMax = bankerAmountCount;
		long bankerMaybeLoseMax = 0;
		for (int i = 1; i <= 35; i++) {
			if (i == option) {
			} else if (rejectionOptions.contains(i)) {
				bankerMaybeMax += existBetAmountCounts[i - 1];
			} else {
				bankerMaybeLoseMax += (long) (existBetAmountCounts[i - 1] * (getDiceType(i).getMultiple() - 1));
			}
		}
		long bankerRemainForOption = bankerMaybeMax - bankerMaybeLoseMax;
		double multiple = (getDiceType(option).getMultiple() - 1);
		long betMaxForOption = (long) (bankerRemainForOption / multiple);
		long remain = betMaxForOption - existBetAmountCounts[option - 1];
		return remain;
	}

	private static void addRejectionOptionsWithoutMax(long[] existBetAmountCounts, Set<Integer> rejectionOptions,
			int optionBegin, int optionEnd) {
		double settlementMax = 0;
		int maxOption = -1;
		for (int i = optionBegin; i <= optionEnd; i++) {
			if (!rejectionOptions.contains(i)) {
				double settlement = existBetAmountCounts[i - 1] * getDiceType(i).getMultiple();
				if (settlement > settlementMax) {
					if (maxOption > 0) {
						rejectionOptions.add(maxOption);
					}
					settlementMax = settlement;
					maxOption = i;
				} else {
					rejectionOptions.add(i);
				}
			}
		}
	}

	protected static Set<Integer> getRejectionOptions(int option) {// 获得和当前下注选项一定不会同时出现的下注选项
		// 豹子单选 对子单选 和值单选 的1-6不可能同时出现，选出赔率最大的值作为可能同时出现的值参与计算。。。单双三筛子
		Set<Integer> rejections = new HashSet<>();
		if (option == DICE_TYPE_LITTLE_OPTION) {
			addInteger(rejections, DICE_TYPE_LARGE_OPTION);
			addInteger(rejections, DICE_TYPE_BAO_ZI_TOTAL_OPTION);// 豹子通选
			addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);// 豹子单选
			addIntegers(rejections, DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 5 - 1,
					DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 6 - 1);// 对5 对6
			addIntegers(rejections, DICE_TYPE_SUM_VALUE_OPTION_BEGIN + (11 - SUM_VALUE_VALUE_MIN),
					DICE_TYPE_SUM_VALUE_OPTION_BEGIN + (SUM_VALUE_VALUE_MAX - SUM_VALUE_VALUE_MIN));// 和值单选，11-17
		} else if (option == DICE_TYPE_LARGE_OPTION) {
			addInteger(rejections, DICE_TYPE_LITTLE_OPTION);
			addInteger(rejections, DICE_TYPE_BAO_ZI_TOTAL_OPTION);// 豹子通选
			addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);// 豹子单选
			addIntegers(rejections, DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 1 - 1,
					DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 2 - 1);// 对1 对2
			addIntegers(rejections, DICE_TYPE_SUM_VALUE_OPTION_BEGIN,
					DICE_TYPE_SUM_VALUE_OPTION_BEGIN + (10 - SUM_VALUE_VALUE_MIN));// 和值单选，4-10
		} else if (option == DICE_TYPE_BAO_ZI_TOTAL_OPTION) {
			addInteger(rejections, DICE_TYPE_LITTLE_OPTION);
			addInteger(rejections, DICE_TYPE_LARGE_OPTION);
		} else if (option >= DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN
				&& option < DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6) {
			int diceValue = option - DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 1;
			addInteger(rejections, DICE_TYPE_LITTLE_OPTION);
			addInteger(rejections, DICE_TYPE_LARGE_OPTION);
			addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN,
					DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 - 1);// 值不同的豹子
			addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 + 1,
					DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
			addIntegers(rejections, DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN,
					DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 - 1);// 值不同的对子
			addIntegers(rejections, DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 + 1,
					DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
			addIntegers(rejections, DICE_TYPE_SUM_VALUE_OPTION_BEGIN,
					DICE_TYPE_SUM_VALUE_OPTION_BEGIN + diceValue * 3 - 4 - 1);// 值不同的
			addIntegers(rejections, DICE_TYPE_SUM_VALUE_OPTION_BEGIN + diceValue * 3 - 4 + 1,
					DICE_TYPE_SUM_VALUE_OPTION_BEGIN + 14 - 1);
			addIntegers(rejections, DICE_TYPE_SINGLE_OPTION_BEGIN, DICE_TYPE_SINGLE_OPTION_BEGIN + diceValue - 1 - 1);// 值不同的单张
			addIntegers(rejections, DICE_TYPE_SINGLE_OPTION_BEGIN + diceValue - 1 + 1,
					DICE_TYPE_SINGLE_OPTION_BEGIN + 6 - 1);
		} else if (option >= DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN
				&& option < DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 6) {
			int diceValue = option - DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 1;
			if (diceValue * 2 + 1 >= 11) {
				addInteger(rejections, DICE_TYPE_LITTLE_OPTION);
			}
			if (diceValue * 2 + 6 <= 10) {
				addInteger(rejections, DICE_TYPE_LARGE_OPTION);
			}
			addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN,
					DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 - 1);// 值不同的豹子
			addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 + 1,
					DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
			addIntegers(rejections, DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN,
					DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 - 1);// 值不同的对子
			addIntegers(rejections, DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 + 1,
					DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
			for (int i = SUM_VALUE_VALUE_MIN; i <= SUM_VALUE_VALUE_MAX; i++) {
				if (i >= diceValue * 2 + 1 && i <= diceValue * 2 + 6) {
				}else{
					addInteger(rejections, DICE_TYPE_SUM_VALUE_OPTION_BEGIN + i - SUM_VALUE_VALUE_MIN);
				}
			}
		} else if (option >= DICE_TYPE_SUM_VALUE_OPTION_BEGIN
				&& option <= DICE_TYPE_SUM_VALUE_OPTION_BEGIN + SUM_VALUE_VALUE_MAX - SUM_VALUE_VALUE_MIN) {
			int sumValue = option - DICE_TYPE_SUM_VALUE_OPTION_BEGIN + SUM_VALUE_VALUE_MIN;
			if (sumValue <= 10) {
				addInteger(rejections, DICE_TYPE_LARGE_OPTION);
			} else {
				addInteger(rejections, DICE_TYPE_LITTLE_OPTION);
			}
			if (sumValue % 3 == 0) {
				int v = sumValue / 3;
				addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN, v - 1);// 值不同的豹子
				addIntegers(rejections, v + 1, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
			} else {
				addInteger(rejections, DICE_TYPE_BAO_ZI_TOTAL_OPTION);// 豹子通选
				addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN,
						DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
			}
		} else {
			int diceValue = option - DICE_TYPE_SINGLE_OPTION_BEGIN + 1;
			addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN,
					DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 - 1);// 值不同的豹子
			addIntegers(rejections, DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + diceValue - 1 + 1,
					DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + 6 - 1);
		}
		return rejections;
	}

	public static double[] getAllTypeMultiples(int[] dices) {
		double[] multiples = new double[sumDiceTypes.length];
		for (int i = 0; i < sumDiceTypes.length; i++) {
			multiples[i] = sumDiceTypes[i].getDicesMultiple(dices);
		}
		return multiples;
	}

	public static abstract class BaseDiceType {

		private int index;
		private double multiple;

		public BaseDiceType(int index, double multiple) {
			super();
			this.index = index;
			this.multiple = multiple;
		}

		protected abstract boolean isConform(int[] dices);

		public int getIndex() {
			return index;
		}

		public double getMultiple() {
			return multiple;
		}

		public double getDicesMultiple(int[] dices) {
			if (this.isConform(dices)) {
				return multiple;
			} else {
				return 0;
			}
		}
	}

	public static class DiceTypeLittle extends BaseDiceType {

		public DiceTypeLittle() {
			super(DICE_TYPE_LITTLE_OPTION, DICE_TYPE_LITTLE_MULTIPLE);
		}

		@Override
		protected boolean isConform(int[] dices) {
			boolean baozi = dices[0] == dices[1] && dices[1] == dices[2];
			if (baozi) {
				return false;
			} else {
				return (dices[0] + dices[1] + dices[2]) <= 10;
			}
		}
	}

	public static class DiceTypeLarge extends BaseDiceType {

		public DiceTypeLarge() {
			super(DICE_TYPE_LARGE_OPTION, DICE_TYPE_LARGE_MULTIPLE);
		}

		@Override
		protected boolean isConform(int[] dices) {
			boolean baozi = dices[0] == dices[1] && dices[1] == dices[2];
			if (baozi) {
				return false;
			} else {
				return (dices[0] + dices[1] + dices[2]) > 10;
			}
		}
	}

	public static class DiceTypeBaoziTotal extends BaseDiceType {

		public DiceTypeBaoziTotal() {
			super(DICE_TYPE_BAO_ZI_TOTAL_OPTION, DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);
		}

		@Override
		protected boolean isConform(int[] dices) {
			return dices[0] == dices[1] && dices[1] == dices[2];
		}
	}

	public static class DiceTypeBaoziSingle extends BaseDiceType {
		public static DiceTypeBaoziSingle[] getSum() {
			DiceTypeBaoziSingle[] sum = new DiceTypeBaoziSingle[6];
			for (int i = 1; i <= 6; i++) {
				sum[i - 1] = new DiceTypeBaoziSingle(i);
			}
			return sum;
		}

		private int diceValue;

		private DiceTypeBaoziSingle(int value) {
			super(DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + value - 1, DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE);// 4-9
			this.diceValue = value;
		}

		@Override
		protected boolean isConform(int[] dices) {
			return dices[0] == diceValue && dices[1] == diceValue && dices[2] == diceValue;
		}
	}

	public static class DiceTypeDuiZiSingle extends BaseDiceType {

		public static DiceTypeDuiZiSingle[] getSum() {
			DiceTypeDuiZiSingle[] sum = new DiceTypeDuiZiSingle[6];
			for (int i = 1; i <= 6; i++) {
				sum[i - 1] = new DiceTypeDuiZiSingle(i);
			}
			return sum;
		}

		private int diceValue;

		private DiceTypeDuiZiSingle(int value) {
			super(DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + value - 1, DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE);// 10-15
			this.diceValue = value;
		}

		@Override
		protected boolean isConform(int[] dices) {
			int count = 0;
			for (int d : dices) {
				count += d == this.diceValue ? 1 : 0;
			}
			return count >= 2;
		}
	}

	public static class DiceTypeSumValue extends BaseDiceType {
		public static DiceTypeSumValue[] getSum() {
			DiceTypeSumValue[] sum = new DiceTypeSumValue[SUM_VALUE_VALUE_MAX - SUM_VALUE_VALUE_MIN + 1];
			for (int i = SUM_VALUE_VALUE_MIN; i <= SUM_VALUE_VALUE_MAX; i++) {
				sum[i - SUM_VALUE_VALUE_MIN] = new DiceTypeSumValue(i,
						DICE_TYPE_SUM_VALUE_MULTIPLES[i - SUM_VALUE_VALUE_MIN]);
			}
			return sum;
		}

		private int sumValue;

		private DiceTypeSumValue(int value, double multiple) {
			super(DICE_TYPE_SUM_VALUE_OPTION_BEGIN + value - SUM_VALUE_VALUE_MIN, multiple);// 16-29
			this.sumValue = value;
		}

		@Override
		protected boolean isConform(int[] dices) {
			return (dices[0] + dices[1] + dices[2]) == this.sumValue;
		}
	}

	public static class DiceTypeSingle extends BaseDiceType {
		public static DiceTypeSingle[] getSum() {
			DiceTypeSingle[] sum = new DiceTypeSingle[6];
			for (int i = 1; i <= 6; i++) {
				sum[i - 1] = new DiceTypeSingle(i);
			}
			return sum;
		}

		private int diceValue;

		private DiceTypeSingle(int value) {
			super(DICE_TYPE_SINGLE_OPTION_BEGIN + value - 1, DICE_TYPE_SINGLE_MULTIPLES[3]);// 30-35
			this.diceValue = value;
		}

		@Override
		protected boolean isConform(int[] dices) {
			return dices[0] == this.diceValue || dices[1] == this.diceValue || dices[2] == this.diceValue;
		}

		@Override
		public double getDicesMultiple(int[] dices) {
			int count = 0;
			for (int d : dices) {
				count += d == this.diceValue ? 1 : 0;
			}
			return DICE_TYPE_SINGLE_MULTIPLES[count];
		}

	}
}
