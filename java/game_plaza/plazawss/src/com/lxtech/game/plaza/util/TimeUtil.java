package com.lxtech.game.plaza.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;

public class TimeUtil {

	/**
	 * Get the current time in "yyyy-M-d H:m:s"
	 * @return
	 */
	public static String getCurTimeGMT() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:m:s");
		return sdf.format(new Date(System.currentTimeMillis() - 480 * 60 * 1000));
	}
	
	public static String getDayStr(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		return sdf.format(d);
	}

	//TODO
	//this function needs to be refactored to provide the most recent four workdays
	//if this is the weekend, just include Tuesday to Friday
	public static List<String> getRecentWorkdays() {
//		return new ImmutableList.Builder().add("2016-11-8 0:0:0").add("2016-11-9 0:0:0").add("2016-11-10 0:0:0").add("2016-11-11 0:0:0").build();
		Calendar calendar = Calendar.getInstance();
		DateTime dt = new DateTime(calendar);
		int dow = dt.getDayOfWeek();
		
		List<Date> dateList = new ArrayList<Date>(); 
		if (dow >= 4) { //weekend
			for (int i=2; i<=5; i++) {
				dateList.add(dt.withDayOfWeek(i).toDate());
			}
		} else {
			for (int i=1; i<=4; i++) {
				dateList.add(dt.withDayOfWeek(i).toDate());
			}
		}
		
		List<String> dStrList = new ArrayList<String>();
		for (Date d : dateList) {
			dStrList.add(new SimpleDateFormat("yyyy-M-d").format(d) + " 0:0:0");
		}
		
		return dStrList;
	}

	public static String getMostRecentWorkday() {
	  return getMostRecentWorkday(true);
	}
	
	public static String getMostRecentWorkday(boolean standardDate) {
    int dayInWeek = getDayInWeek();
    int hoursOfDay = getHoursOfDay();
    if (hoursOfDay < 9){
      if (dayInWeek >= 2 && dayInWeek <= 5) {
        if (standardDate) {
          return getDayWithGap(1);
        } else {
          return getDayWithGap2(1);
        }
      } else {
        int dayGap = 0;
        switch (dayInWeek) {
        case 1:
          dayGap = 3;
          break;
        case 7:
          dayGap = 2;
          break;
        case 6:
          dayGap = 1;
          break;
        default:
          dayGap = 1;
          break;
        }
        if (standardDate) {
          return getDayWithGap(dayGap);
        } else {
          return getDayWithGap2(dayGap);
        }
      }
    } else {
      if (dayInWeek >= 6) {
        if (standardDate) {
          return getDayWithGap(dayInWeek - 5);
        } else {
          return getDayWithGap2(dayInWeek - 5);
        }
      } else {
        if (standardDate) {
          return getDayWithGap(0);
        } else {
          return getDayWithGap2(0);
        }
      }
    }
	}
	
	public static String getDayWithGap(int dayCnt) {
		Calendar calendar = Calendar.getInstance();
		DateTime dt = new DateTime(calendar);
		dt = dt.minusDays(dayCnt);
		Date d = dt.toDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		return sdf.format(d);
	}
	
	 public static String getDayWithGap2(int dayCnt) {
	    Calendar calendar = Calendar.getInstance();
	    DateTime dt = new DateTime(calendar);
	    dt = dt.minusDays(dayCnt);
	    Date d = dt.toDate();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    return sdf.format(d);
	  }

	public static int getDayInWeek() {
        Calendar calendar = Calendar.getInstance();
        DateTime dt = new DateTime(calendar);
        return dt.getDayOfWeek();
    }
	
	public static int getHoursOfDay() {
		Calendar calendar = Calendar.getInstance();
        DateTime dt = new DateTime(calendar);
        return dt.getHourOfDay();
	}
	
	public static void main(String[] args) {
//		System.out.println(TimeUtil.getDayInWeek());
//		System.out.println(TimeUtil.getHoursOfDay());
//		System.out.println(getMostRecentWorkday());
//		System.out.println(getCurTimeGMT());
		
//		Calendar c = Calendar.getInstance();
//		DateTime dt = new DateTime(c);
//		System.out.println(dt.getDayOfWeek());
		System.out.println(TimeUtil.getDayStr(new Date()));
	}
	
}
