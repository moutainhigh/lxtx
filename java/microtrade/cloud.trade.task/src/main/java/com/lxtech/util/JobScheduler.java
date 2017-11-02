package com.lxtech.util;

import java.text.SimpleDateFormat;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler { 
    public void go() throws Exception { 
        // 首先，必需要取得一个Scheduler的引用 
      SchedulerFactory sf = new StdSchedulerFactory(); 
        Scheduler sched = sf.getScheduler();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS"); 
        //jobs可以在scheduled的sched.start()方法前被调用 
         
//        //job 1将每隔20秒执行一次 
//        JobDetail job = newJob(myJob.class).withIdentity("job1", "group1").build(); 
//        CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1").withSchedule(cronSchedule("5 9,19,29,39,49,59 * * * ?")).build(); 
//        Date ft = sched.scheduleJob(job, trigger); 
//        System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression()); 
// 
//        JobDetail jobUpdate = newJob(YesterdayDataJob.class).withIdentity("job2", "group1").build();
//        CronTrigger trigger2 = newTrigger().withIdentity("trigger2", "group1").withSchedule(cronSchedule("0 10 0 * * ?")).build(); 
//        Date ft2 = sched.scheduleJob(jobUpdate, trigger2); 
//        System.out.println(jobUpdate.getKey() + " 已被安排执行于: " + sdf.format(ft2) + "，并且以如下规则执行: " + trigger2.getCronExpression());        
//        
//        JobDetail diffJob = newJob(DiffJob.class).withIdentity("job3", "group1").build();
//        CronTrigger trigger3 = newTrigger().withIdentity("trigger3", "group1").withSchedule(cronSchedule("50 29,59 * * * ?")).build(); 
//        Date ft3 = sched.scheduleJob(diffJob, trigger3); 
//        System.out.println(diffJob.getKey() + " 已被安排执行于: " + sdf.format(ft3) + "，并且以如下规则执行: " + trigger3.getCronExpression());
//        
//        JobDetail payJob = newJob(PayStatJob.class).withIdentity("job4", "group1").build();
//        CronTrigger trigger4 = newTrigger().withIdentity("trigger4", "group1").withSchedule(cronSchedule("0 10 0 * * ?")).build(); 
//        Date ft4 = sched.scheduleJob(payJob, trigger4); 
//        System.out.println(payJob.getKey() + " 已被安排执行于: " + sdf.format(ft4) + "，并且以如下规则执行: " + trigger4.getCronExpression());        
        
        sched.start(); 
        try { 
            //主线程等待一分钟 
            Thread.sleep(60L * 1000L); 
        } catch (Exception e) {}     
       //关闭定时调度，定时器不再工作 
       //sched.shutdown(true); 
} 
 
    public static void main(String[] args) throws Exception { 
        JobScheduler test = new JobScheduler(); 
        test.go(); 
    } 
}