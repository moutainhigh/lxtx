package com.lxtx.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	public static int getDayByDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return Integer.valueOf(sdf.format(date));
	}
	
	public static void main(String[] args) {
		System.out.println(getTimeStr(new Date()));
	}

	public static String getTimeStr24H(Date openTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		return sdf.format(openTime);
	}
	public static String getTimeStr(Date openTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
		return sdf.format(openTime);
	}
	
	
}
