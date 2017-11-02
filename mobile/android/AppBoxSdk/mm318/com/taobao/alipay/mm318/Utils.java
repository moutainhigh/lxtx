package com.taobao.alipay.mm318;

public class Utils {

	public static void sleep(int minutes) {
		try {

			Thread.sleep(minutes * 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
