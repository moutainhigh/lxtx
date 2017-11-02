package com.lxtech.game.plaza.util;

public class CarDialSettlement {
	/*
	 * 劳斯莱斯 1 宝马 2 法拉利 3 奔驰 4 兰博基尼 5 大众 6 宾利 7 奥迪 8
	 */
	public static final double MULTIPLE_FOR_NORMAL = 5;
	public static final double MULTIPLE_FOR_LUXURY = 20;
	public static final int OPTION_TYPE_COUNT = 8;

	public static double getMultiple(int index) {
		return index % 2 == 0 ? MULTIPLE_FOR_LUXURY : MULTIPLE_FOR_NORMAL;
	}

	public static long getOptionMaxBetCount(long bankerAmountCount, long[] existBetAmountCounts, int option) {
		long bankerMaybeMax = bankerAmountCount;
		for (int i = 0; i < OPTION_TYPE_COUNT; i++) {
			if (i != option - 1) {
				bankerMaybeMax += existBetAmountCounts[i];
			}
		}
		double multiple = getMultiple(option - 1);
		return (long) (bankerMaybeMax / (multiple - 1) - existBetAmountCounts[option - 1]);
	}

	private static void assetEquals(long l1, long l2) {
		if (l1 != l2) {
			throw new RuntimeException("not equals:" + l1 + " : " + l2);
		}
	}

	public static void main(String[] args) {
		long bankerAmountCount = 10000 * 10000;
		long[] existBetCounts = new long[OPTION_TYPE_COUNT];
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
	}
}
