package com.lxtech.util.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.FillFailed;
import com.lxtech.biz.UserInWarnByHalfHour;
import com.lxtech.util.TimeUtil;


public class UserInWarnJob implements Job {

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
		if (hourofday == 9 && minuteofhour < 20) {
			//9点半前不执行
			return;
		}
		// 30分钟无注册用户告警
		UserInWarnByHalfHour.userInWarn();
	}
	
	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
