package com.lxtx.settlement.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDialSettlement {
	/*
	 * 劳斯莱斯 1 宝马 2 法拉利 3 奔驰 4 兰博基尼 5 大众 6 宾利 7 奥迪 8
	 */
	public static final double MULTIPLE_FOR_NORMAL = 5;
	public static final double MULTIPLE_FOR_LUXURY = 20;
	public static final int OPTION_TYPE_COUNT = 8;

	private static final int OPTION_LAO_SI_LAI_SI = 1;
	private static final int OPTION_BAO_MA = 2;
	private static final int OPTION_FA_LA_LI = 3;
	private static final int OPTION_BENG_CHI = 4;
	private static final int OPTION_LAN_BO_JI_NI = 5;
	private static final int OPTION_DA_ZHONG = 6;
	private static final int OPTION_BING_LI = 7;
	private static final int OPTION_AO_DI = 8;

	private static final int[] sumOptionFlags = { OPTION_LAO_SI_LAI_SI, OPTION_AO_DI, OPTION_BAO_MA, OPTION_BENG_CHI, //
			OPTION_FA_LA_LI, OPTION_BENG_CHI, OPTION_BAO_MA, OPTION_DA_ZHONG, //
			OPTION_LAN_BO_JI_NI, OPTION_BENG_CHI, OPTION_DA_ZHONG, OPTION_AO_DI, //
			OPTION_BING_LI, OPTION_DA_ZHONG, OPTION_AO_DI, OPTION_BAO_MA//
	};

	private static final Map<Integer, List<Integer>> sumOptionFlagsMap = new HashMap<>();

	public static Integer[] sumOptions;

	static {
		sumOptions = new Integer[20];
		int index = 0;
		for (int i = 0; i < OPTION_TYPE_COUNT; i++) {
			int count = (i % 2 == 0) ? 1 : 4;
			for (int j = 0; j < count; j++) {
				sumOptions[index++] = i + 1;
			}
		}
		Tools.shuffle(sumOptions);
		for (int i = 0; i < sumOptionFlags.length; i++) {
			int option = sumOptionFlags[i];
			List<Integer> list = sumOptionFlagsMap.get(option);
			if (null == list) {
				list = new ArrayList<Integer>();
				sumOptionFlagsMap.put(option, list);
			}
			list.add(i + 1);
		}
		for (int i = 1; i <= OPTION_TYPE_COUNT; i++) {
			List<Integer> flags = sumOptionFlagsMap.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append("option:").append(i).append(":    ");
			for (Integer f : flags) {
				sb.append(f).append(",");
			}
			System.out.println(sb.toString());
		}
	}
	
	public static List<Integer> getOptionFlags(int option){
		return sumOptionFlagsMap.get(option);
	}

	public static double[] getAllTypeMultiples(int index) {
		double[] multiples = new double[OPTION_TYPE_COUNT];
		multiples[index] = index % 2 == 0 ? MULTIPLE_FOR_LUXURY : MULTIPLE_FOR_NORMAL;
		return multiples;
	}

}
