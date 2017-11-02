package com.lxtech.util.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.OrderEmptyByHalfHour;
import com.lxtech.util.TimeUtil;

public class OrderEmptyByHalfHourJob implements Job {

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
		if (hourofday == 9 && minuteofhour < 10) {
			//9点钟查询可能没有订单
			return;
		}
		// 查看半小时前订单并告警
		OrderEmptyByHalfHour.halfHourOrdered();
	}
	
	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
