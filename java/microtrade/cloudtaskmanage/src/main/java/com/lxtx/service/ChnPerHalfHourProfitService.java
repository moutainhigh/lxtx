package com.lxtx.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudChnDayProfitMapper;
import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.dao.CloudPerHalfHourProfitMapper;
import com.lxtx.dao.CloudSystemTaskDetailMapper;
import com.lxtx.model.CloudChnDayProfit;
import com.lxtx.model.CloudPerHalfHourProfit;
import com.lxtx.model.CloudSystemTaskDetail;
import com.lxtx.util.StringUtil;

@Service
public class ChnPerHalfHourProfitService {
	private static final Logger logger = LoggerFactory.getLogger(ChnPerHalfHourProfitService.class);
	@Autowired
	private CloudOrderMapper cloudOrderMapper;

	@Autowired
	private CloudPerHalfHourProfitMapper cloudPerHalfHourProfitMapper;
	@Autowired
	private CloudSystemTaskDetailMapper cloudSystemTaskDetailMapper;

	@Autowired
	private CloudChnDayProfitMapper chnDayProfitMapper;

	public void chnPerHalfHourProfitTask() {
		Date now = new Date();
		now.setSeconds(0);
		String preHalfHour = StringUtil.getPreHalfHour(now);
		String end = StringUtil.formatDate(now, "yyyy-MM-dd HH:mm:00");
		logger.info("chnPerHalfHourProfitTask Task begin:" + end);
		// 获取系统各渠道半小时总盈亏入库
		Date date = new Date();
		date.setSeconds(0);
		List<CloudPerHalfHourProfit> hHourProfits = cloudOrderMapper.queryPerHalfHourProfitGroupByChn(preHalfHour, end);
		for (CloudPerHalfHourProfit cloudPerHalfHourProfit : hHourProfits) {
			if (date.getHours() == 0 && date.getMinutes() < 30) {
				Calendar c = Calendar.getInstance();
				date.setMinutes(0);
				date.setSeconds(0);
				c.setTimeInMillis(date.getTime() - 1000);
				date = c.getTime();
			}
			cloudPerHalfHourProfit.setDate(date);
			cloudPerHalfHourProfitMapper.insertSelective(cloudPerHalfHourProfit);
		}
		markLog(now, "chnPerHalfHourProfitTask");
	}

	/**
	 * 任务日志记录
	 * 
	 * @param begin_clear
	 * @param taskNm
	 */
	private void markLog(Date begin_clear, String taskNm) {
		Date close_end = new Date();
		long closeCom = (close_end.getTime() - begin_clear.getTime());
		CloudSystemTaskDetail taskDetail = new CloudSystemTaskDetail();
		taskDetail.setExecDay(StringUtil.formatDate(close_end, "yyyy-MM-dd"));
		taskDetail.setExecTime(StringUtil.formatDate(close_end, "HH:mm:ss"));
		taskDetail.setTaskNm(taskNm);
		int i = cloudSystemTaskDetailMapper.updateByDayAndTaskNm(taskDetail);
		if (i == 0) {
			cloudSystemTaskDetailMapper.insertSelective(taskDetail);
		}
		logger.info(taskNm + " Task end:" + StringUtil.formatDate(close_end) + " used time:" + closeCom + " ms");
	}

	public void chnDayProfitTask() {
		Date now = new Date();
		logger.info("chnDayProfitTask Task begin:" + StringUtil.formatDate(now));
		String day = StringUtil.getPreDay(now);
		// 获取系统各渠道半小时总盈亏入库
		List<CloudChnDayProfit> dayProfits = cloudPerHalfHourProfitMapper.queryChnDayProfitGroupByChn(day);
		for (CloudChnDayProfit dayProfit : dayProfits) {
			dayProfit.setDate(StringUtil.parseStringToDate10(day));
			chnDayProfitMapper.insertSelective(dayProfit);
		}
		markLog(now, "chnDayProfitTask");
	}

}
