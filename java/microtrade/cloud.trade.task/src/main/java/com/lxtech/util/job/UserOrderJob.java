package com.lxtech.util.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.CloudProfitService;
import com.lxtech.biz.UserOrderService;

public class UserOrderJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		doJob();
	}

	private static void doJob() {
		UserOrderService job= new UserOrderService();
		job.userOrder();
	}

	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
