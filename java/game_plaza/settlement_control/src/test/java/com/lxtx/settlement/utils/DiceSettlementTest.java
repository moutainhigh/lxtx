package com.lxtx.settlement.utils;

import com.lxtx.settlement.utils.DiceSettlement;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DiceSettlementTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public DiceSettlementTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(DiceSettlementTest.class);
	}

	public static void assertEquals(double[] src, double[] dist) {
		assertEquals(src.length, dist.length);
		for (int i = 0; i < src.length; i++) {
			assertEquals(src[i], dist[i], 0.0001F);
		}
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testGetAllTypeMultiples() {
		{
			double[] multiples = DiceSettlement.getAllTypeMultiples(new int[] { 1, 1, 1 });
			double[] expectedMultiples = new double[35];
			expectedMultiples[DiceSettlement.DICE_TYPE_BAO_ZI_TOTAL_OPTION
					- 1] = DiceSettlement.DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE;// 豹子通选
			expectedMultiples[DiceSettlement.DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + (1 - 1)
					- 1] = DiceSettlement.DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE;// 豹子单选
			expectedMultiples[DiceSettlement.DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + (1 - 1)
					- 1] = DiceSettlement.DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE;// 对子单选
			expectedMultiples[DiceSettlement.DICE_TYPE_SINGLE_OPTION_BEGIN + (1 - 1)
					- 1] = DiceSettlement.DICE_TYPE_SINGLE_MULTIPLES[3];// 三骰
			assertEquals(multiples, expectedMultiples);
		}
		{
			double[] multiples = DiceSettlement.getAllTypeMultiples(new int[] { 2, 2, 2 });
			double[] expectedMultiples = new double[35];
			expectedMultiples[DiceSettlement.DICE_TYPE_BAO_ZI_TOTAL_OPTION
					- 1] = DiceSettlement.DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE;// 豹子通选
			expectedMultiples[DiceSettlement.DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + (2 - 1)
					- 1] = DiceSettlement.DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE;// 豹子单选
			expectedMultiples[DiceSettlement.DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + (2 - 1)
					- 1] = DiceSettlement.DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE;// 对子单选
			expectedMultiples[DiceSettlement.DICE_TYPE_SUM_VALUE_OPTION_BEGIN + (6 - DiceSettlement.SUM_VALUE_VALUE_MIN)
					- 1] = DiceSettlement.DICE_TYPE_SUM_VALUE_MULTIPLES[6 - DiceSettlement.SUM_VALUE_VALUE_MIN];// 和值
			expectedMultiples[DiceSettlement.DICE_TYPE_SINGLE_OPTION_BEGIN + (2 - 1)
					- 1] = DiceSettlement.DICE_TYPE_SINGLE_MULTIPLES[3];// 三骰
			assertEquals(multiples, expectedMultiples);
		}
		{
			double[] multiples = DiceSettlement.getAllTypeMultiples(new int[] { 6, 6, 6 });
			double[] expectedMultiples = new double[35];
			expectedMultiples[DiceSettlement.DICE_TYPE_BAO_ZI_TOTAL_OPTION
					- 1] = DiceSettlement.DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE;// 豹子通选
			expectedMultiples[DiceSettlement.DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + (6 - 1)
					- 1] = DiceSettlement.DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE;// 豹子单选
			expectedMultiples[DiceSettlement.DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + (6 - 1)
					- 1] = DiceSettlement.DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE;// 对子单选
			expectedMultiples[DiceSettlement.DICE_TYPE_SINGLE_OPTION_BEGIN + (6 - 1)
					- 1] = DiceSettlement.DICE_TYPE_SINGLE_MULTIPLES[3];// 三骰
			assertEquals(multiples, expectedMultiples);
		}
		{
			double[] multiples = DiceSettlement.getAllTypeMultiples(new int[] { 1, 1, 2 });
			double[] expectedMultiples = new double[35];
			expectedMultiples[DiceSettlement.DICE_TYPE_LITTLE_OPTION - 1] = DiceSettlement.DICE_TYPE_LITTLE_MULTIPLE;// 小
			expectedMultiples[DiceSettlement.DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + (1 - 1)
					- 1] = DiceSettlement.DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE;// 对子
			expectedMultiples[DiceSettlement.DICE_TYPE_SUM_VALUE_OPTION_BEGIN + (4 - DiceSettlement.SUM_VALUE_VALUE_MIN)
					- 1] = DiceSettlement.DICE_TYPE_SUM_VALUE_MULTIPLES[4 - DiceSettlement.SUM_VALUE_VALUE_MIN];// 和值
			expectedMultiples[DiceSettlement.DICE_TYPE_SINGLE_OPTION_BEGIN + (1 - 1)
					- 1] = DiceSettlement.DICE_TYPE_SINGLE_MULTIPLES[2];// 双骰
			expectedMultiples[DiceSettlement.DICE_TYPE_SINGLE_OPTION_BEGIN + (2 - 1)
					- 1] = DiceSettlement.DICE_TYPE_SINGLE_MULTIPLES[1];// 单骰
			assertEquals(multiples, expectedMultiples);
		}

		{
			double[] multiples = DiceSettlement.getAllTypeMultiples(new int[] { 4, 5, 6 });
			double[] expectedMultiples = new double[35];
			expectedMultiples[DiceSettlement.DICE_TYPE_LARGE_OPTION - 1] = DiceSettlement.DICE_TYPE_LARGE_MULTIPLE;
			expectedMultiples[DiceSettlement.DICE_TYPE_SUM_VALUE_OPTION_BEGIN + (15 - DiceSettlement.SUM_VALUE_VALUE_MIN)
					- 1] = DiceSettlement.DICE_TYPE_SUM_VALUE_MULTIPLES[15 - DiceSettlement.SUM_VALUE_VALUE_MIN];// 和值
			expectedMultiples[DiceSettlement.DICE_TYPE_SINGLE_OPTION_BEGIN + (4 - 1)
					- 1] = DiceSettlement.DICE_TYPE_SINGLE_MULTIPLES[1];// 单骰
			expectedMultiples[DiceSettlement.DICE_TYPE_SINGLE_OPTION_BEGIN + (5 - 1)
								- 1] = DiceSettlement.DICE_TYPE_SINGLE_MULTIPLES[1];// 单骰
			expectedMultiples[DiceSettlement.DICE_TYPE_SINGLE_OPTION_BEGIN + (6 - 1)
								- 1] = DiceSettlement.DICE_TYPE_SINGLE_MULTIPLES[1];// 单骰
			assertEquals(multiples, expectedMultiples);
		}
	}

}
