package com.lxtech.cloud.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;

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
	
	public static List<Date> listDateForDiscount() {
		DateTime dt = new DateTime();
		List<Date> dayList = new ArrayList<Date>();
		List<Date> effectiveDayList = new ArrayList<Date>();
		for (int i = 0; i < 24; i++) {
			dayList.add(new Date(dt.plusDays(i).getMillis()));
		}
		
		int iter = 1;
		for (Date d : dayList) {
			dt = new DateTime(d.getTime());
			if (dt.getDayOfWeek() <= 5) {
				effectiveDayList.add(d);
				iter++;
			}
			if (iter > 10) {
				break;
			}
		}
		
		return effectiveDayList;
	}
	
	/**
	 * get a list of dates starting from startDay
	 * @param startDay
	 * @param count
	 * @return
	 */
	public static Map<Date, List<Integer>> getDateList(int count, int dailyAmount) {
/*		DateTime dt = new DateTime();
		List<Date> dayList = new ArrayList<Date>();
		for (int i = 0; i < 24; i++) {
			dayList.add(new Date(dt.plusDays(i).getMillis()));
		}
		
		Map<Date, List<Integer>> dayMap = new HashMap<Date, List<Integer>>();
		int iter = 1;
		for (Date d : dayList) {
			dt = new DateTime(d.getTime());
			if (dt.getDayOfWeek() <= 5) {
				if (iter <= 5) {
					dayMap.put(d, ImmutableList.of(5, 5));
				} else {
					dayMap.put(d, ImmutableList.of(5));
				}
				iter++;
			}
			if (iter > 15) {
				break;
			}
		}
		return dayMap;*/
		DateTime dt = new DateTime();
		int dayOfWeek = dt.getDayOfWeek();
		int bonusDay = 1;
		if (dayOfWeek > 5) { //this is the weekend
			bonusDay = 1;
		} else {
			bonusDay = dayOfWeek;
		}
		
		int counter = 0;
		int dayCount = 0;
		Map<Date, List<Integer>> dataMap = new HashMap<Date, List<Integer>>(); 
		while(counter < 5) {
			DateTime nextDt = dt.plusDays(dayCount++);
			if (nextDt.getDayOfWeek() == bonusDay) {
				counter++;
				dataMap.put(new Date(nextDt.getMillis()), ImmutableList.of(dailyAmount));
			}
		}
		
		return dataMap;
	}
	
	public static Date getPlusDay(Date startDay, int variance) {
		DateTime dt = new DateTime(startDay.getTime());
		return new Date(dt.plusDays(variance - 1).getMillis()); 
	}
	
	public static void main(String[] args) {
//		System.out.println(getCurrentHourOfDay());
		/*Map<Date, List<Integer>> dayMap = getDateList(15);
		for (Date d : dayMap.keySet()) {
			List<Integer> numberList = dayMap.get(d);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			System.out.println(sdf.format(d) );
			for (Integer i:numberList) {
				System.out.print(i+"  ");
			}
			System.out.println("");
		}*/
/*		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<Date, List<Integer>> map = getDateList(5, 5);
		
		for (Date d: map.keySet()) {
			System.out.println(sdf.format(d));
		}*/
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date targetDate = getPlusDay(new Date(), 7);
		System.out.println(sdf.format(targetDate));
	}
}
