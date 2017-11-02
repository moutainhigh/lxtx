package com.lxtech.inspect.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.inspect.LotteryCrawler;

public class LotteryCrawlerJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		new LotteryCrawler().mainLogic();
	}

}
