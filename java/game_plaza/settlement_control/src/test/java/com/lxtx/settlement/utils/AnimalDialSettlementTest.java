package com.lxtx.settlement.utils;

import com.lxtx.settlement.utils.DiceSettlement;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AnimalDialSettlementTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AnimalDialSettlementTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AnimalDialSettlementTest.class);
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
			int[] combinedTwoDefine = { 0, 0 };
			int[] combinedThreeDefine = { 0, 0, 0 };
			int tiandiIndex = 0;
			int wuxingIndex = 0;
			int animalIndex = 0;
			double[] multiples = AnimalDialSettlement.getAllTypeMultiples(combinedTwoDefine, combinedThreeDefine,
					tiandiIndex, wuxingIndex, animalIndex);
			double[] expectedMultiples = new double[AnimalDialSettlement.OPTION_TYPE_COUNT];
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_TIAN_DI_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_TIAN_DI;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_WU_XING_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_WUXING;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_ANIMAL_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_ANIMAL;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_COMBINED_TWO
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_COMBINED_TWO;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_COMBINED_THREE
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_COMBINED_THREE;
			assertEquals(multiples, expectedMultiples);
		}
		{
			int[] combinedTwoDefine = { 1, 0 };
			int[] combinedThreeDefine = { 0, 0, 0 };
			int tiandiIndex = 0;
			int wuxingIndex = 0;
			int animalIndex = 0;
			double[] multiples = AnimalDialSettlement.getAllTypeMultiples(combinedTwoDefine, combinedThreeDefine,
					tiandiIndex, wuxingIndex, animalIndex);
			double[] expectedMultiples = new double[AnimalDialSettlement.OPTION_TYPE_COUNT];
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_TIAN_DI_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_TIAN_DI;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_WU_XING_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_WUXING;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_ANIMAL_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_ANIMAL;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_COMBINED_THREE
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_COMBINED_THREE;
			assertEquals(multiples, expectedMultiples);
		}
		{
			int[] combinedTwoDefine = { 1, 0 };
			int[] combinedThreeDefine = { 1, 0, 0 };
			int tiandiIndex = 0;
			int wuxingIndex = 0;
			int animalIndex = 0;
			double[] multiples = AnimalDialSettlement.getAllTypeMultiples(combinedTwoDefine, combinedThreeDefine,
					tiandiIndex, wuxingIndex, animalIndex);
			double[] expectedMultiples = new double[AnimalDialSettlement.OPTION_TYPE_COUNT];
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_TIAN_DI_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_TIAN_DI;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_WU_XING_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_WUXING;
			expectedMultiples[AnimalDialSettlement.OPTION_TYPE_ANIMAL_BEGIN
					- 1] = AnimalDialSettlement.MULTIPLE_FOR_ANIMAL;
			assertEquals(multiples, expectedMultiples);
		}
	}

}
