package com.lxtech.inspect.scheduler;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.lxtech.dbmodel.LotteryCqsscData;
import com.lxtech.inspect.LotteryCrawler;
import com.lxtech.inspect.job.LotteryCrawlerJob;

public class LotteryScheduler {
	public void go() throws SchedulerException {
		// 首先，必需要取得一个Scheduler的引用
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		// jobs可以在scheduled的sched.start()方法前被调用

		// job 1将每隔20秒执行一次
		JobDetail job = newJob(LotteryCrawlerJob.class).withIdentity("job1", "group1").build();
		CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1")
				.withSchedule(cronSchedule("1 0/1 * * * ? ")).build();
		Date ft = sched.scheduleJob(job, trigger);
		System.out.println(
				job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		sched.start();
	}

	public static void main(String[] args) {
		try {
			new LotteryScheduler().go();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		// LotteryCrawler crawler = new LotteryCrawler();
		// List<LotteryCqsscData> datas = new ArrayList<LotteryCqsscData>();
		// LotteryCqsscData data = new LotteryCqsscData();
		// data.setId(3194);
		// data.setDate(20170118);
		// data.setSerial_number(61);
		// data.setOpen_code("2,8,1,1,4");
		// data.setOpen_time(new Date());
		// datas.add(data);
		// crawler.settlement(datas);
	}
}
