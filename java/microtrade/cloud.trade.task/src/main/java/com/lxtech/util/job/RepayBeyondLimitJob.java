package com.lxtech.util.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.FillFailed;
import com.lxtech.util.TimeUtil;

public class RepayBeyondLimitJob implements Job {

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

		if (hourofday < 9) {
			// 9点钟之前不能提现
			return;
		}

		// 5分钟内有提现超1000的告警
		FillFailed.repayBeyondLimit();
	}

	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
