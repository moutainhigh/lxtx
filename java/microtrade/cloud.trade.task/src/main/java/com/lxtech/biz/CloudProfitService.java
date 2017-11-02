package com.lxtech.biz;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudUserDao;
import com.lxtech.model.CloudProfit;
import com.lxtech.util.TimeUtil;

public class CloudProfitService {
	private final static Logger logger = LoggerFactory.getLogger(CloudProfitService.class);

	public void cloudProfit() {
		Date now = new Date();
		logger.info("cloudProfit Task begin:" + TimeUtil.formatDate(now));
		String beginStr = TimeUtil.formatDateCommon(now, "yyyy-MM-dd", -1);
		String endStr = TimeUtil.formatDate(now, "yyyy-MM-dd");
		try {
			List<CloudProfit> list = CloudUserDao.queryProfit(beginStr, endStr);
			for (CloudProfit cloudProfit : list) {
				CloudUserDao.saveProfit(cloudProfit);
			}
			Date close_end = new Date();
			long closeCom = (close_end.getTime() - now.getTime());
			logger.info(
					"cloudProfit Task end: " + TimeUtil.formatDateString(close_end) + " used time:" + closeCom + " ms");
		} catch (SQLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void cloudProfit(Date now) {
		String beginStr = TimeUtil.formatDateCommon(now, "yyyy-MM-dd", -1);
		String endStr = TimeUtil.formatDate(now, "yyyy-MM-dd");
		try {
			List<CloudProfit> list = CloudUserDao.queryProfit(beginStr, endStr);
			for (CloudProfit cloudProfit : list) {
				CloudUserDao.saveProfit(cloudProfit);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		CloudProfitService s = new CloudProfitService();
		Date beginDate = TimeUtil.parseString10ToDate("2017-01-16");
		boolean flag = true;
		while (flag) {
			String nextDate = TimeUtil.formatDateCommon(beginDate, "yyyy-MM-dd", 1);
			if (TimeUtil.compareDate1(nextDate, "2017-02-21")) {
				flag = false;
			} else {
				beginDate = TimeUtil.parseString10ToDate(nextDate);
				s.cloudProfit(beginDate);
			}
		}
	}

}
