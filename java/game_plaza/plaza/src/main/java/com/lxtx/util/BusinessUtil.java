package com.lxtx.util;

import java.util.Date;

public class BusinessUtil {

	/**
	 * 周一~周五09:00 到04:00
	 * <p/>
	 * 如果是周六 则4点以后不允许交易 如果是周日 则全天不允许
	 * <p/>
	 * 如果是周一 则9点以前不允许 如果在4点到9点 则不允许交易
	 * 
	 * @return true 不允许交易
	 */
	public static boolean validTimeLimit() {
		Date now = new Date();
		String nowDate = StringUtil.formatDate(now, "yyyy-MM-dd");
		Date begin = StringUtil.parseStringToDate(nowDate.concat(" 04:00:00"));
		Date end = StringUtil.parseStringToDate(nowDate.concat(" 09:00:00"));
		String week = "";
		int day = now.getDay();
		if (day == 0)
			week = "0";
		if (day == 1)
			week = "1";
		if (day == 6)
			week = "6";
		if (week.equals("0")) {
			return true;
		} else if (week.equals("1")) {
			if (now.getTime() <= end.getTime()) {
				return true;
			}
		} else if (week.equals("6")) {
			if (now.getTime() >= begin.getTime()) {
				return true;
			}
		}
		if (begin.getTime() <= now.getTime() && now.getTime() <= end.getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 周一~周五09:00 到23.55:00
	 * 
	 * @return true 不允许提现
	 */
	public static boolean validRepayTime() {
		Date now = new Date();
		String nowDate = StringUtil.formatDate(now, "yyyy-MM-dd");
		Date begin = StringUtil.parseStringToDate(nowDate.concat(" 09:00:00"));
		Date end = StringUtil.parseStringToDate(nowDate.concat(" 23:55:00"));
		String week = "";
		int day = now.getDay();
		if (day == 0)
			week = "0";
		if (day == 6)
			week = "6";
		if (week == "0" || week == "6") {
			return true;
		}
		// 小于开始时间，或大于结束时间，不能提现
		if (now.getTime() <= begin.getTime() || now.getTime() >= end.getTime()) {
			return true;
		} else {
			return false;
		}
	}

}
