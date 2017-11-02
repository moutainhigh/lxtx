package com.lxtech.util.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.CloudProfitService;

public class CloudProfitJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		doJob();
	}

	private static void doJob() {
		CloudProfitService job = new CloudProfitService();
		job.cloudProfit();
	}

	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
