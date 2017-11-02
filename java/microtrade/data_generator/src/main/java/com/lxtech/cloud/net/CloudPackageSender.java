package com.lxtech.cloud.net;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.lxtech.cloud.db.CloudOrderHandler;
import com.lxtech.cloud.db.CloudTargetHandler;
import com.lxtech.cloud.db.CloudTargetStatHandler;
import com.lxtech.cloud.db.IndexChangeHandler;
import com.lxtech.cloud.db.model.CloudTargetIndexChange;
import com.lxtech.cloud.db.model.CloudTargetIndexStat;
import com.lxtech.cloud.util.TimeUtil;
import com.lxtech.cloud.util.cache.GlobalCacheUtil;

public class CloudPackageSender {
	
	private static final int TIMES = 100;
	
	private static final String BTC_LABEL = "BTC";
	
	private static final String LTC_LABEL = "LTC";
	
	private static int getTimes(String code) {
		return 1;
	}
	
	public static void pushIndexChange(String code, double index) {
		if (index == 0)  {
			return;
		}
		Map<String, Object> dataMap = new HashMap<String, Object>();
		int times = getTimes(code);
		
		Map<Object, Object> targetData = ImmutableMap.builder().put("Market", 17000).put("Code", code).put("Time", TimeUtil.getCurTimeGMT()).put("Now", index * times).build();
		dataMap.put("Symbol", ImmutableList.of(targetData));
		Gson gson = new Gson();
		String messageBody = gson.toJson(dataMap);
		String header = CloudPackageHeader.generateResponseHeader(messageBody.length(), CloudPackageType.MSG_TYPE_SERPushPrice, "2764926");
		String message = header + CloudNetPackage.PACKAGE_SPLITTER + messageBody;
		KestrelConnector.enqueue(message);
		
		//invoke http service
//		String updateUrl = CrawlerConfig.get("price.update.url");
//		HttpClient.post(updateUrl, ImmutableMap.of("data", messageBody));
		
		try {
			CloudOrderHandler.markOrderProcessed(code, index);
			//update the current index
			CloudTargetHandler.updateCloudTargetIndex(code, index);
			
			//should generate an index change
			CloudTargetIndexChange change = new CloudTargetIndexChange(code, new Timestamp(System.currentTimeMillis()), index);			
			IndexChangeHandler.saveData(change);
			//update daily stat table
			CloudTargetIndexStat stat = CloudTargetStatHandler.retrieveTargetStat(code);
			double high = stat.getHigh();//GlobalCacheUtil.getHighCache().get(code);
			if (index > high) {
				GlobalCacheUtil.getHighCache().put(code, index);
				//update db
				CloudTargetStatHandler.updateTargetTopIndex(code, index);
			}

			//check low index
			int low = (int)stat.getLow();//GlobalCacheUtil.getLowCache().get(code);
			if (low == 0 || index < low) {
				GlobalCacheUtil.getLowCache().put(code, index);
				//update db
				CloudTargetStatHandler.updateTargetLowIndex(code, index);
			}
			
			//check open index
			int open = (int)stat.getOpen();//GlobalCacheUtil.getOpenCache().get(code);
			if (open == 0) {
				GlobalCacheUtil.getOpenCache().put(code, index);
				CloudTargetStatHandler.updateTargetOpenIndex(code, index);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CloudPackageSender.pushIndexChange("test", 1234);
	}
}
