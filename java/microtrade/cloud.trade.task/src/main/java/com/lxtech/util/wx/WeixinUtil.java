package com.lxtech.util.wx;

import java.util.Random;

public class WeixinUtil {
	public static String orderNum() {
		String chars = "0123456789";
		String order = System.currentTimeMillis() + "";
		String res = "";
		for (int i = 0; i < 19; i++) {
			Random rd = new Random();
			res += chars.charAt(rd.nextInt(chars.length() - 1));
		}
		order += res;
		return order;
	}
}
