package com.lxtech.util.job;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.lxtech.util.SystemProperty;

public class KCreatorJobScheduler {
	public void go() throws Exception {
		// 首先，必需要取得一个Scheduler的引用
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		// jobs可以在scheduled的sched.start()方法前被调用

		// k5
		JobDetail job1 = newJob(KCreatorJob_5.class).withIdentity("KCreatorJob_5", "group1").build();
		CronTrigger trigger = newTrigger().withIdentity("KCreatorJob_5_trigger", "group1")
				.withSchedule(cronSchedule("0 0/5 * * * ?")).build();
		Date ft = sched.scheduleJob(job1, trigger);
		System.out.println(
				job1.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());
		// k15
		JobDetail job2 = newJob(KCreatorJob_15.class).withIdentity("KCreatorJob_15", "group1").build();
		CronTrigger trigger2 = newTrigger().withIdentity("KCreatorJob_15_trigger", "group1")
				.withSchedule(cronSchedule("0 0/15 * * * ?")).build();
		Date ft2 = sched.scheduleJob(job2, trigger2);
		System.out.println(
				job2.getKey() + " 已被安排执行于: " + sdf.format(ft2) + "，并且以如下规则执行: " + trigger2.getCronExpression());
		// k30
		JobDetail job3 = newJob(KCreatorJob_30.class).withIdentity("KCreatorJob_30", "group1").build();
		CronTrigger trigger3 = newTrigger().withIdentity("KCreatorJob_30_trigger", "group1")
				.withSchedule(cronSchedule("0 0/30 * * * ?")).build();
		Date ft3 = sched.scheduleJob(job3, trigger3);
		System.out.println(
				job3.getKey() + " 已被安排执行于: " + sdf.format(ft3) + "，并且以如下规则执行: " + trigger3.getCronExpression());
		// k60
		JobDetail job4 = newJob(KCreatorJob_60.class).withIdentity("KCreatorJob_60", "group1").build();
		CronTrigger trigger4 = newTrigger().withIdentity("KCreatorJob_60_trigger", "group1")
				.withSchedule(cronSchedule("0 0/60 * * * ?")).build();
		Date ft4 = sched.scheduleJob(job4, trigger4);
		System.out.println(
				job4.getKey() + " 已被安排执行于: " + sdf.format(ft4) + "，并且以如下规则执行: " + trigger4.getCronExpression());
		// 今开昨收
		JobDetail job5 = newJob(CreateCloseAndOpenJob.class).withIdentity("CreateCloseAndOpen", "group1").build();
		CronTrigger trigger5 = newTrigger().withIdentity("CreateCloseAndOpen_trigger", "group1")
				.withSchedule(cronSchedule("0 55 8 * * ?")).build();
		Date ft5 = sched.scheduleJob(job5, trigger5);
		
		System.out.println(
				job5.getKey() + " 已被安排执行于: " + sdf.format(ft5) + "，并且以如下规则执行: " + trigger5.getCronExpression());
		
/*		JobDetail job6 = newJob(DataSourceInspector.class).withIdentity("DataSourceInspector", "group1").build();
		CronTrigger trigger6 = newTrigger().withIdentity("DataSourceInspector_trigger", "group1")
				.withSchedule(cronSchedule("0 0/1 * * * ?")).build();
		Date ft6 = sched.scheduleJob(job6, trigger6);
		System.out.println(
				job6.getKey() + " 已被安排执行于: " + sdf.format(ft6) + "，并且以如下规则执行: " + trigger6.getCronExpression());*/

		if (SystemProperty.getProperty("temp_msg_flag").equals("on")) {
			// 定时多人发送收益消息
			JobDetail job7 = newJob(SendAllMsgByTempJob.class).withIdentity("SendAllMsgByTemp", "group1").build();
			CronTrigger trigger7 = newTrigger().withIdentity("SendAllMsgByTemp_trigger", "group1")
					.withSchedule(cronSchedule("0 0 20 * * ?")).build();
			Date ft7 = sched.scheduleJob(job7, trigger7);

			System.out.println(
					job7.getKey() + " 已被安排执行于: " + sdf.format(ft7) + "，并且以如下规则执行: " + trigger7.getCronExpression());

			// 定时单人发送收益消息
			JobDetail job8 = newJob(SendSingleMsgByTempJob.class).withIdentity("SendSingleMsgByTemp", "group1").build();
			CronTrigger trigger8 = newTrigger().withIdentity("SendSingleMsgByTemp_trigger", "group1")
					.withSchedule(cronSchedule("0 0/2 * * * ?")).build();
			Date ft8 = sched.scheduleJob(job8, trigger8);

			System.out.println(
					job8.getKey() + " 已被安排执行于: " + sdf.format(ft8) + "，并且以如下规则执行: " + trigger8.getCronExpression());
			
		}
		
		// 行情数据监控
		JobDetail job9 = newJob(DataInspectorJob.class).withIdentity("TrendDataInspector", "group1").build();
		CronTrigger trigger9 = newTrigger().withIdentity("TrendDataInspector_trigger", "group1")
				.withSchedule(cronSchedule("0 0/5 * * * ?")).build();
		Date ft9 = sched.scheduleJob(job9, trigger9);
		
//		System.out.println(
//				job9.getKey() + " 已被安排执行于: " + sdf.format(ft9) + "，并且以如下规则执行: " + trigger9.getCronExpression());			
		// 30分钟无订单告警
//		JobDetail job10 = newJob(OrderEmptyByHalfHourJob.class).withIdentity("OrderEmptyByHalfHour", "group1").build();
//		CronTrigger trigger10 = newTrigger().withIdentity("OrderEmptyByHalfHour_trigger", "group1")
//				.withSchedule(cronSchedule("0 0/30 * * * ?")).build();
//		Date ft10 = sched.scheduleJob(job10, trigger10);
//		
//		System.out.println(
//				job10.getKey() + " 已被安排执行于: " + sdf.format(ft10) + "，并且以如下规则执行: " + trigger10.getCronExpression());			
		// 5分钟有订单亏损超1000告警
		JobDetail job11 = newJob(OrderBeyondLimitJob.class).withIdentity("OrderBeyondLimit", "group1").build();
		CronTrigger trigger11 = newTrigger().withIdentity("OrderBeyondLimit_trigger", "group1")
				.withSchedule(cronSchedule("0 0/5 * * * ?")).build();
		Date ft11 = sched.scheduleJob(job11, trigger11);
		
		System.out.println(
				job11.getKey() + " 已被安排执行于: " + sdf.format(ft11) + "，并且以如下规则执行: " + trigger11.getCronExpression());			
		// 15分钟充值全失败告警
		JobDetail job12 = newJob(FillFailedJob.class).withIdentity("FillFailed", "group1").build();
		CronTrigger trigger12 = newTrigger().withIdentity("FillFailed_trigger", "group1")
				.withSchedule(cronSchedule("0 0/15 * * * ?")).build();
		Date ft12 = sched.scheduleJob(job12, trigger12);
		
		System.out.println(
				job12.getKey() + " 已被安排执行于: " + sdf.format(ft12) + "，并且以如下规则执行: " + trigger12.getCronExpression());			
		// 5分钟有出金超过1000告警
		JobDetail job13 = newJob(RepayBeyondLimitJob.class).withIdentity("RepayBeyondLimit", "group1").build();
		CronTrigger trigger13 = newTrigger().withIdentity("RepayBeyondLimit_trigger", "group1")
				.withSchedule(cronSchedule("0 0/5 * * * ?")).build();
		Date ft13 = sched.scheduleJob(job13, trigger13);
		
		System.out.println(
				job13.getKey() + " 已被安排执行于: " + sdf.format(ft13) + "，并且以如下规则执行: " + trigger13.getCronExpression());			
		// 5分钟有入金超过3000告警
		JobDetail job14 = newJob(FillBeyondLimitJob.class).withIdentity("FillBeyondLimit", "group1").build();
		CronTrigger trigger14 = newTrigger().withIdentity("FillBeyondLimit_trigger", "group1")
				.withSchedule(cronSchedule("0 0/5 * * * ?")).build();
		Date ft14 = sched.scheduleJob(job14, trigger14);
		
		System.out.println(
				job14.getKey() + " 已被安排执行于: " + sdf.format(ft14) + "，并且以如下规则执行: " + trigger14.getCronExpression());			
		// 30分钟没有注册新用户告警
//		JobDetail job15 = newJob(UserInWarnJob.class).withIdentity("UserInWarn", "group1").build();
//		CronTrigger trigger15 = newTrigger().withIdentity("UserInWarn_trigger", "group1")
//				.withSchedule(cronSchedule("0 0/30 * * * ?")).build();
//		Date ft15 = sched.scheduleJob(job15, trigger15);
//		
//		System.out.println(
//				job15.getKey() + " 已被安排执行于: " + sdf.format(ft15) + "，并且以如下规则执行: " + trigger15.getCronExpression());			
//		
		// 每小时 壳模板消息发送
		JobDetail job16 = newJob(ShellTempMsgJob.class).withIdentity("ShellTempMsg", "group1").build();
		CronTrigger trigger16 = newTrigger().withIdentity("ShellTempMsg_trigger", "group1")
				.withSchedule(cronSchedule("0 0 * * * ?")).build();
		Date ft16 = sched.scheduleJob(job16, trigger16);
		
		System.out.println(
				job16.getKey() + " 已被安排执行于: " + sdf.format(ft16) + "，并且以如下规则执行: " + trigger16.getCronExpression());			

		
		// 日交易分析统计 每天凌晨6点执行
		JobDetail job17 = newJob(TranDayAnalyseJob.class).withIdentity("TranDayAnalyse", "group1").build();
		CronTrigger trigger17 = newTrigger().withIdentity("TranDayAnalyse_trigger", "group1")
				.withSchedule(cronSchedule("0 0 6 * * ?")).build();
		Date ft17 = sched.scheduleJob(job17, trigger17);
		
		System.out.println(
				job17.getKey() + " 已被安排执行于: " + sdf.format(ft17) + "，并且以如下规则执行: " + trigger17.getCronExpression());			
		// 用户活跃度分析统计 每周6凌晨6点执行
		JobDetail job18 = newJob(UserActivityJob.class).withIdentity("UserActivity", "group1").build();
		CronTrigger trigger18 = newTrigger().withIdentity("UserActivity_trigger", "group1")
				.withSchedule(cronSchedule("0 0 6 ? * 7")).build();
		Date ft18 = sched.scheduleJob(job18, trigger18);
		
		System.out.println(
				job18.getKey() + " 已被安排执行于: " + sdf.format(ft18) + "，并且以如下规则执行: " + trigger18.getCronExpression());			
		
		// 人工订单任务 交易时间 每5分钟执行一次
		JobDetail job19 = newJob(HumanOrderControlJob.class).withIdentity("HumanOrderControl", "group1").build();
		CronTrigger trigger19 = newTrigger().withIdentity("HumanOrderControl_trigger", "group1")
				.withSchedule(cronSchedule("0 0/5 * * * ?")).build();
		Date ft19 = sched.scheduleJob(job19, trigger19);
		
		System.out.println(
				job19.getKey() + " 已被安排执行于: " + sdf.format(ft19) + "，并且以如下规则执行: " + trigger19.getCronExpression());			
		
		// 每天凌晨00:09:00统计上一日各渠道总盈利
		JobDetail job20 = newJob(CloudProfitJob.class).withIdentity("CloudProfitJob", "group1").build();
		CronTrigger trigger20 = newTrigger().withIdentity("CloudProfitJob_trigger", "group1")
				.withSchedule(cronSchedule("0 9 0 * * ?")).build();
		Date ft20 = sched.scheduleJob(job20, trigger20);
		
		System.out.println(
				job20.getKey() + " 已被安排执行于: " + sdf.format(ft20) + "，并且以如下规则执行: " + trigger20.getCronExpression());			
		
		// 5分钟内微笑用户登录监控
		JobDetail job21 = newJob(UserOrderJob.class).withIdentity("UserOrderJob", "group1").build();
		CronTrigger trigger21 = newTrigger().withIdentity("UserOrderJob_trigger", "group1")
				.withSchedule(cronSchedule("0 0/5 * * * ?")).build();
		Date ft21 = sched.scheduleJob(job21, trigger21);
		
		System.out.println(
				job21.getKey() + " 已被安排执行于: " + sdf.format(ft21) + "，并且以如下规则执行: " + trigger21.getCronExpression());			
		
		sched.start();
		try {
			// 主线程等待一分钟
			Thread.sleep(2L * 1000L);
		} catch (Exception e) {
		}
		// 关闭定时调度，定时器不再工作
		// sched.shutdown(true);
	}

	public static void main(String[] args) throws Exception {
		KCreatorJobScheduler closeOrderJobScheduler = new KCreatorJobScheduler();
		closeOrderJobScheduler.go();
	}
}
