package com.lxtech.cloud.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.util.TimeUtil;

public class MinuteDataJob implements Job{
	
	private final static Logger logger = LoggerFactory.getLogger(MinuteDataJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("in MinuteDataJob, time is " + TimeUtil.getCurrentTime());
	}
}
