package com.lxtech.util.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.OrderBeyondLimit;
import com.lxtech.util.TimeUtil;


public class OrderBeyondLimitJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		doJob();
	}
	
	private static void doJob() {
		if (!TimeUtil.inWorkingHours()) {
			return;
		}

		DateTime dt = new DateTime();
		int hourofday = dt.getHourOfDay();
		int minuteofhour = dt.getMinuteOfHour();
		if (hourofday == 9 && minuteofhour < 5) {
			//9点钟查询可能没有订单
			return;
		}
		// 5分钟订单亏损1000并告警
		OrderBeyondLimit.orderBeyondLimit();
	}
	
	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
