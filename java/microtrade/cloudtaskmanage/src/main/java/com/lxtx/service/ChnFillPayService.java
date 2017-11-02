package com.lxtx.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudChnDayFillPayMapper;
import com.lxtx.dao.CloudChnFillPayMapper;
import com.lxtx.dao.CloudChnUserDayProfitMapper;
import com.lxtx.dao.CloudSystemTaskDetailMapper;
import com.lxtx.model.CloudChnDayFillPay;
import com.lxtx.model.CloudChnFillPay;
import com.lxtx.model.CloudChnUserDayProfit;
import com.lxtx.model.CloudSystemTaskDetail;
import com.lxtx.util.StringUtil;

@Service
public class ChnFillPayService {
	private static final Logger logger = LoggerFactory.getLogger(ChnFillPayService.class);
	@Autowired
	private CloudSystemTaskDetailMapper cloudSystemTaskDetailMapper;

	@Autowired
	private CloudChnFillPayMapper cloudChnFillPayMapper;
	@Autowired
	private CloudChnDayFillPayMapper cloudChnDayFillPayMapper;
	@Autowired
	private CloudChnUserDayProfitMapper chnUserDayProfitMapper;

	/**
	 * 渠道半小时统计
	 */
	public void chnFillPayTask() {
		Date now = new Date();
		String begin = StringUtil.getPreHalfHour(now);
		String end = StringUtil.getPreHour(now, 0);
		logger.info("chnFillPayHalfHourTask Task begin:" + end);
		// 统计
		Date date = new Date();
		date.setSeconds(0);
		List<CloudChnFillPay> list = cloudChnFillPayMapper.queryChnFillPay(begin, end);

		for (CloudChnFillPay cloudChnFillPay : list) {
			if (date.getHours() == 0 && date.getMinutes() < 30) {
				Calendar c = Calendar.getInstance();
				date.setMinutes(0);
				date.setSeconds(0);
				c.setTimeInMillis(now.getTime() - 1000);
				date = c.getTime();
			}
			cloudChnFillPay.setDate(date);
			logger.info(cloudChnFillPay.getChnno() + ":" + cloudChnFillPay.getFillAmount());
			cloudChnFillPayMapper.insertSelective(cloudChnFillPay);
		}
		// 入库
		markLog(now, "chnFillPayHalfHourTask");

	}

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

	public void chnUserDayProfitTask() {
		Date now = new Date();
		String begin = StringUtil.formatDate(now, "yyyy-MM-dd");
		logger.info("chnUserDayProfitTask Task begin:" + StringUtil.formatDate(now));
		// 统计
		Date date = new Date();
		date.setSeconds(0);
		List<CloudChnUserDayProfit> list = chnUserDayProfitMapper.queryUserDayProfit(begin);
		if (list != null && list.size() > 0) {
			for (CloudChnUserDayProfit userDayProfit : list) {
				int i = chnUserDayProfitMapper.updateByUidAndSubAndLimitAndDate(userDayProfit);
				if (i == 0) {
					chnUserDayProfitMapper.insertSelective(userDayProfit);
				}
			}
		}
		markLog(now, "chnUserDayProfitTask");
	}

	/**
	 * 渠道日充值提现、订单统计
	 */
	public void chnDayFillPayTask() {
		Date now = new Date();
		String day = StringUtil.formatDate(now, "yyyy-MM-dd");
		logger.info("chnDayFillPayTask Task begin:" + StringUtil.formatDate(now));
		List<CloudChnDayFillPay> list = cloudChnDayFillPayMapper.queryChnDayFillPay(day);
		Date date = StringUtil.parseStringToDate10(day);
		for (CloudChnDayFillPay chnDayFillPay : list) {
			chnDayFillPay.setDate(date);
			int i = cloudChnDayFillPayMapper.updateByChnAndDate(chnDayFillPay);
			if (i == 0) {
				cloudChnDayFillPayMapper.insertSelective(chnDayFillPay);
			}
		}
		// 入库
		markLog(now, "chnDayFillPayTask");
	}

	/**
	 * 渠道日充值提现、订单统计 生成
	 */
	public void chnDayFillPayTaskgen() {
		Date begin = StringUtil.parseStringToDate10("2016-11-18");
		String querDate = StringUtil.formatDate(begin, "yyyy-MM-dd");
		for (int i = 0; i < 30; i++) {
			if (querDate.equals(StringUtil.formatDate(new Date(), "yyyy-MM-dd"))) {
				break;
			}
			querDate = StringUtil.formatDateCommon(begin, "yyyy-MM-dd", i);
			List<CloudChnDayFillPay> list = cloudChnDayFillPayMapper.queryChnDayFillPay(querDate);
			Date date = StringUtil.parseStringToDate10(querDate);
			for (CloudChnDayFillPay chnDayFillPay : list) {
				chnDayFillPay.setDate(date);
				int count = cloudChnDayFillPayMapper.updateByChnAndDate(chnDayFillPay);
				if (count == 0) {
					cloudChnDayFillPayMapper.insertSelective(chnDayFillPay);
				}
			}
		}

		// 入库
		// markLog(now, "chnDayFillPayTask");
	}
}
