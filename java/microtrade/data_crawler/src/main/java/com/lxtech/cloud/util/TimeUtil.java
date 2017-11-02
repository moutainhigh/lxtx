package com.lxtech.cloud.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	//in format 'yyyy-MM-dd' 
	public static String getDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	public static Date parseDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d h:m:s");
		try {
			Date d = sdf.parse(time);
			return d;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
