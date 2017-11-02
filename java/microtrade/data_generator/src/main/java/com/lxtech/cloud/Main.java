package com.lxtech.cloud;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.google.common.collect.ImmutableList;
import com.lxtech.cloud.jobs.DynamicDataJob;
import com.lxtech.cloud.jobs.MinuteDataJob;

public class Main {
	public void go() throws Exception {
		// 首先，必需要取得一个Scheduler的引用
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		// jobs可以在scheduled的sched.start()方法前被调用

		// job 1将每分钟的59秒时执行一次
		/*JobDetail job = newJob(MinuteDataJob.class).withIdentity("job1", "group1").build();
		CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1").withSchedule(cronSchedule("59 * * * * ?"))
				.build();
		Date ft = sched.scheduleJob(job, trigger);
		System.out.println(
				job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());*/

		List<String> codeList = ImmutableList.of("BTC", "LTC");
		
		for (String code : codeList) {
			String jobName = "job" + code;
			String triggerName = "trigger" + code;
			JobDetail dataJob = newJob(DynamicDataJob.class).withIdentity(jobName, "group1").build();
			dataJob.getJobDataMap().put("code", code);
			CronTrigger trigger2 = newTrigger().withIdentity(triggerName, "group1")
					.withSchedule(cronSchedule("0/1 * * * * ?")).build();
			Date ft2 = sched.scheduleJob(dataJob, trigger2);
			System.out.println(
					dataJob.getKey() + " 已被安排执行于: " + sdf.format(ft2) + "，并且以如下规则执行: " + trigger2.getCronExpression());			
		}
		sched.start();
		try {
			// 主线程等待一分钟
			Thread.sleep(60L * 1000L);
		} catch (Exception e) {
		}
		// 关闭定时调度，定时器不再工作
		// sched.shutdown(true);
	}
	
	public static void main(String[] args) {
		try {
			new Main().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
