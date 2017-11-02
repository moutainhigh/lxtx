package com.lxtech.util.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.biz.MarkLogUtil;
import com.lxtech.biz.TranDayAnalyse;

public class TranDayAnalyseJob implements Job {
	private final static Logger logger = LoggerFactory.getLogger(TranDayAnalyseJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		doJob();
	}
	
	private static void doJob() {
		Date date = new Date();
		logger.info("=============executeTranDayAnalyseJob begin=============");
		TranDayAnalyse tranDayAnalyse = new TranDayAnalyse();
		tranDayAnalyse.analyseDayTran();
		Date end = new Date();
		MarkLogUtil.markLog(date, "TranDayAnalyse_task");
		logger.info(
				"=============executeTranDayAnalyseJob end,耗时:" + (end.getTime() - date.getTime()) + " ms=============");
	}
	
	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
