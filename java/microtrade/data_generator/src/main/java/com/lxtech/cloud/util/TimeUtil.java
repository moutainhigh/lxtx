package com.lxtech.cloud.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

public class TimeUtil {

	//in format 'yyyy-MM-dd' 
	public static String getDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d h:m:s");
		return sdf.format(new Date());
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

	public static int getCurrentDayOfWeek() {
		Calendar calendar = Calendar.getInstance();
		DateTime dt = new DateTime(calendar);
		return dt.getDayOfWeek();
	}
	
	/**
	 * 获取当前的秒数，介于
	 * @return
	 */
	public static int getCurrentSecondOfMinute() {
		Calendar calendar = Calendar.getInstance();
		DateTime dt = new DateTime(calendar);
		return dt.getSecondOfMinute();
	}
	
	public static int getCurrentHourOfDay() {
		Calendar calendar = Calendar.getInstance();
		DateTime dt = new DateTime(calendar);
		return dt.getHourOfDay();
	}

	public static Object getCurTimeGMT() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:m:s");
		return sdf.format(new Date(System.currentTimeMillis() - 480 * 60 * 1000));
	}
	
	public static void main(String[] args) {
		System.out.println(getCurrentHourOfDay());
	}
	
	
}
