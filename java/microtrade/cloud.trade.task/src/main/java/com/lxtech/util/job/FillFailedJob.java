package com.lxtech.util.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.FillFailed;
import com.lxtech.util.TimeUtil;


public class FillFailedJob implements Job {

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
			//9点钟不执行
			return;
		}
		// 15分钟充值全失败告警
		FillFailed.fillFailed();
	}
	
	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
