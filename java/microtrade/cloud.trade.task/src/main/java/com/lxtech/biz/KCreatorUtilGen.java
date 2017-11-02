package com.lxtech.biz;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudKPeriodDao;
import com.lxtech.dao.CloudTargetDao;
import com.lxtech.dao.CloudTargetIndexMinuteDao;
import com.lxtech.model.CloudKPeriod;
import com.lxtech.model.CloudTarget;
import com.lxtech.model.CloudTargetIndexMinute;
import com.lxtech.util.Constants;
import com.lxtech.util.TimeUtil;

public class KCreatorUtilGen {
	private static final Logger logger = LoggerFactory.getLogger(KCreatorUtilGen.class);

	public void KCreatorFactory(int type, String now) throws SQLException {

		// 根据当前时间计算index 区间 8点为0 后每小时加60
		Date ss = TimeUtil.parseStringToDate(now);
		if (ss.getHours() >= 4 && ss.getHours() < 8) {
			logger.info("close period,do not create k-line data!");
		} else {
			int endIndex = getEndIndex(ss, type);
			int beginIndex = endIndex - type;
			logger.debug("endIndex:" + endIndex);
			logger.debug("beginIndex:" + beginIndex);
			String beginTm = getTimeByType(now, beginIndex);
			String endTm = getTimeByType(now, endIndex);
			logger.debug("beginTm:" + beginTm);
			logger.debug("endTm:" + endTm);
			List<CloudTarget> subjects = CloudTargetDao.queryTarget();
			for (CloudTarget cloudTarget : subjects) {
				List<Integer> li = new ArrayList<>();
				List<CloudTargetIndexMinute> list = CloudTargetIndexMinuteDao.queryIndexsByIndexGen(cloudTarget.getName(),
						beginIndex, endIndex, beginTm, endTm);
				for (CloudTargetIndexMinute cloudTargetIndexMinute : list) {
					li.add(cloudTargetIndexMinute.getIdx());
				}
				if (li != null && li.size() > 0) {
					CloudKPeriod kPeriod = new CloudKPeriod();
					kPeriod.setTime(endTm);
					kPeriod.setSubject(cloudTarget.getName());
					kPeriod.setType(Constants.kTypeMap.get(type));
					kPeriod.setOpen(li.get(0));
					kPeriod.setClose(li.get(li.size() - 1));
					kPeriod.setHigh(Collections.max(li));
					kPeriod.setLow(Collections.min(li));
					CloudKPeriodDao.saveDataGen(kPeriod);
					logger.debug(kPeriod.toString());
				}
			}
		}
	}

	public static int getEndIndex(Date now, int type) {
		int hour = now.getHours();
		int mimute = now.getMinutes();
		int index = hour * 60;
		if (hour >= 0 && hour < 8) {
			index += 16 * 60;
		}
		if (hour >= 8) {
			index -= 480;
		}

		int endPeriod = index + (mimute / type) * type;
		return endPeriod;
	}

	public static String getTimeByType(Date dd, int index) {
		int hour = index / 60 + 8;
		if (hour >= 24) {
			hour = hour - 24;
			dd.setDate(dd.getDate() + 1);
		}
		dd.setHours(hour);
		dd.setMinutes(index % 60);
		dd.setSeconds(0);
		return TimeUtil.formatDate(dd, "yyyy-MM-dd HH:mm:ss");
	}

	public static String getTimeByType(String dd, int index) {
		Date ddd = TimeUtil.parseStringToDate(dd);
		int hour = index / 60 + 8;
		if (hour >= 24) {
			hour = hour - 24;
		}
		ddd.setHours(hour);
		ddd.setMinutes(index % 60);
		ddd.setSeconds(0);
		return TimeUtil.formatDate(ddd, "yyyy-MM-dd HH:mm:ss");
	}

	public static void main(String[] args) {
		Date date1 = TimeUtil.parseStringToDate("2016-11-14 09:00:00");
		batchInsertHistoryData(date1, 5);
		Date date2 = TimeUtil.parseStringToDate("2016-11-14 09:00:00");
		batchInsertHistoryData(date2, 15);
		Date date3 = TimeUtil.parseStringToDate("2016-11-14 09:00:00");
		batchInsertHistoryData(date3, 30);
		Date date4 = TimeUtil.parseStringToDate("2016-11-14 09:00:00");
		batchInsertHistoryData(date4, 60);

	}

	public static Date batchInsertHistoryData(Date date, int type) {
		for (int i = 0; i < 1000; i++) {
			KCreatorUtilGen creatorUtil = new KCreatorUtilGen();
			String d = TimeUtil.formatDateString(date);
			try {
				creatorUtil.KCreatorFactory(type, d);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long l = date.getTime() + type * 60 * 1000l;
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(l);
			if (l > new Date().getTime()) {
				break;
			}
			date = c.getTime();
		}
		return date;
	}

}
