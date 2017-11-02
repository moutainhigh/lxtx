package com.lxtech.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeUtil {
	/**
	 * Convert date obj to string
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public static String formatDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static String formatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static int getStartOfDaySeconds(Date date) {
		return getTimeOfDaySecond(date, "00:00:00");
	}

	public static String getTimeOfSpecifiedDate(Date date) {
		return (new SimpleDateFormat("HH:mm:ss")).format(date);
	}

	public static int getCurrentTimeInSec(Date date) {
		return (int) (date.getTime() / 1000);
	}

	public static Date parseStringToDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date != null) {
			try {
				return sdf.parse(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Date parseString10ToDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null) {
			try {
				return sdf.parse(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param date
	 *            - such as "2016-09-30"
	 * @param time
	 *            - such as "20:30:00"
	 * @return
	 */
	public static int getTimeOfDaySecond(Date date, String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dayStr = sdf.format(date);
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTime dt = DateTime.parse(dayStr + " " + time, format);
		int dayStartSecond = (int) (dt.getMillis() / 1000);
		return dayStartSecond;
	}

	public static int getTimeOfDaySecond(String dayStr, String time) {
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
		DateTime dt = DateTime.parse(dayStr + " " + time, format);
		int dayStartSecond = (int) (dt.getMillis() / 1000);
		return dayStartSecond;
	}

	public static boolean inWorkingHours() {
		DateTime dt = new DateTime();
		int hourofday = dt.getHourOfDay();

		if (hourofday >= 4 && hourofday < 9) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @param date
	 * @param formatter
	 * @param offset
	 *            例如：-1表示前一天 1表示后一天 0表示当前
	 * @return
	 */
	public static String formatDateCommon(Date date, String formatter, int offset) {
		Date resultDate = new Date(date.getTime() + (offset * 1000 * 60 * 60 * 24));
		SimpleDateFormat sdf = new SimpleDateFormat(formatter);

		return sdf.format(resultDate);

	}

	/**
	 * 
	 * @param date
	 * @param formatter
	 * @param offset
	 *            例如：-1表示前一天 1表示后一天 0表示当前
	 * @return
	 */
	public static Date formatDateCommon(Date date, int offset) {
		Date resultDate = new Date(date.getTime() + (offset * 1000 * 60 * 60 * 24));
		return resultDate;

	}

	public static boolean compareDate(String now, String config) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 创建日期转换对象HH:mm:ss为时分秒，年月日为yyyy-MM-dd
		try {
			Date dt1 = df.parse(now);// 将字符串转换为date类型
			Date dt2 = df.parse(config);
			if (dt1.getTime() >= dt2.getTime())// 比较时间大小,dt1小于dt2
			{
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean compareDate1(String now, String config) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 创建日期转换对象HH:mm:ss为时分秒，年月日为yyyy-MM-dd
		try {
			Date dt1 = df.parse(now);// 将字符串转换为date类型
			Date dt2 = df.parse(config);
			if (dt1.getTime() >= dt2.getTime())// 比较时间大小,dt1小于dt2
			{
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean compareDate(Date now, Date config) {
		try {
			if (now.getTime() >= config.getTime())// 比较时间大小
			{
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) {
		/*
		 * long time = 1475057661324l; Date date = new Date(time);
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 * System.out.println(sdf.format(date));
		 */
		// System.out.println(getStartOfDaySeconds(new Date()));
		// System.out.println(System.currentTimeMillis());
		// Date date = new Date();
		// System.out.println(date.getTime()/1000);
		// int time = TimeUtil.getTimeOfDaySecond("2016113", "5:43:28");
		System.out.println(compareDate("2017-01-07 16:50:00", "2016-01-07 16:50:00"));
	}

}
