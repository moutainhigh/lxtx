package com.lxtech.util.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lxtech.biz.DataInspector;
import com.lxtech.util.TimeUtil;

public class DataInspectorJob implements Job {

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
			// 9点刚开始的数据不一定有
			return;
		}

		// 查看行情数据是否生成
		DataInspector.inspectGeneratedData();
		//查看队列是否发生堆积
		DataInspector.inspectKestrelQueue();
	}
	
	public static void main(String[] args) throws JobExecutionException {
		doJob();
	}
}
