package com.lxtech.util.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.biz.HumanControlService;
import com.lxtech.util.TimeUtil;

public class HumanOrderControlJob implements Job {
	private final static Logger logger = LoggerFactory.getLogger(HumanOrderControlJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		doJob();
	}

	private static void doJob() {
		if (!TimeUtil.inWorkingHours()) {
			return;
		}
		Date date = new Date();
		logger.info("=============executeHumanOrderControlJob begin=============");

		HumanControlService.humanAdjuster();
		Date end = new Date();
		logger.info("=============executeHumanOrderControlJob end,耗时:" + (end.getTime() - date.getTime())
				+ " ms=============");
	}

	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
