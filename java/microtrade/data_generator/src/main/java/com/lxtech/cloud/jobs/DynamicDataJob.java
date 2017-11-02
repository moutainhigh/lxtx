package com.lxtech.cloud.jobs;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lxtech.cloud.algorithm.IndexChangeAdjuster;
import com.lxtech.cloud.db.CloudTargetHandler;
import com.lxtech.cloud.db.model.CloudTarget;
import com.lxtech.cloud.db.model.CloudTradeException;
import com.lxtech.cloud.net.CloudPackageSender;
import com.lxtech.cloud.util.TimeUtil;
import com.lxtech.cloud.util.cache.GlobalCacheUtil;

public class DynamicDataJob implements Job{
	private final static Logger logger = LoggerFactory.getLogger(DynamicDataJob.class);

	private static Map<String, Double> startIndexMap = new ConcurrentHashMap<String, Double>();
	
	private static Map<String, Double> currentIndexMap = new ConcurrentHashMap<String, Double>();
	
	private static Map<String, Integer> currentStepMap = new ConcurrentHashMap<String, Integer>();
	
	private static Map<String, Double> diffMap = new ConcurrentHashMap<String, Double>();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		int dayOfWeek = TimeUtil.getCurrentDayOfWeek();
		DateTime dt = new DateTime();
		int hourOfDay = dt.getHourOfDay();
		/*if (hourOfDay >= 4 && hourOfDay <= 8) { //所有的4点-9点不执行 
			return;
		}*/
		
		String day = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
		CloudTradeException exc = GlobalCacheUtil.getCloudTradeExceptionCache().get(day);
		if (exc != null && !Strings.isNullOrEmpty(exc.getHours()) && exc.getHourList().contains(hourOfDay)) {
			return;
		}
		
		//周六9点开始，周日全天，周一 9点前不执行
		/*if ((dayOfWeek == 6 && hourOfDay>8) || dayOfWeek == 7 || (dayOfWeek == 1 && hourOfDay < 9)) {
			return;
		}*/
		
		JobDataMap map = context.getJobDetail().getJobDataMap();
		String code = (String)map.get("code");
		logger.info("in DynamicDataJob.exec, code is : " + code + " time is" + TimeUtil.getCurrentTime());
		
		int step = 0;
		if (!currentStepMap.containsKey(code)) {
			currentStepMap.put(code, 0);
		} else {
			step = currentStepMap.get(code);
		}
		
		if (step == 0) {
			CloudTarget target;
			try {
				target = CloudTargetHandler.retrieveOriginalTargetData(code);
				double originalIndex = target.getCurrent_index();
				
				if (CloudTargetHandler.retrieveGeneratedTargetData(code).getCurrent_index() != originalIndex) {
					CloudPackageSender.pushIndexChange(code, originalIndex);
					//startIndex = currentIndex = originalIndex;
					startIndexMap.put(code, originalIndex);
					currentIndexMap.put(code, originalIndex);
					diffMap.put(code, 0.0d);
				} else {
					startIndexMap.put(code, originalIndex);
					currentIndexMap.put(code, originalIndex);
					diffMap.put(code, 0.0d);
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		} else {
			double startIndex = startIndexMap.get(code);
			double currentIndex = currentIndexMap.get(code);
			double diff = diffMap.get(code);
			double newIndex = IndexChangeAdjuster.calculateNewIndex(code, startIndex, diff, step);
			if (newIndex != currentIndex) {
				CloudPackageSender.pushIndexChange(code, newIndex);
				currentIndex = newIndex;
				currentIndexMap.put(code, currentIndex);
				diffMap.put(code, (newIndex - startIndex));
			}
		}
		step = (step + 1) % 4;
		currentStepMap.put(code, step);
	}
}
