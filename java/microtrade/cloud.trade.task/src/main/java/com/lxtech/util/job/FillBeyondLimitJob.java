package com.lxtech.util.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.FillFailed;
import com.lxtech.util.TimeUtil;

public class FillBeyondLimitJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		doJob();
	}

	private static void doJob() {
		if (!TimeUtil.inWorkingHours()) {
			return;
		}
		// 5分钟内有入金超3000的告警
		FillFailed.FillBeyondLimit();
	}

	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
