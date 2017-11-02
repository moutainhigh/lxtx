package com.lxtech.cloud.util.cache;

import java.sql.SQLException;

import com.lxtech.cloud.db.CloudTestCacheHandler;
import com.lxtech.cloud.db.model.CloudTradeException;

public class GlobalCacheUtil {

	private static TargetLowIndexCache lowCache;

	private static TargetTopIndexCache highCache;

	private static TargetOpenIndexCache openCache;

	private static TargetCloseIndexCache closeCache;
	
	private static CloudTradeExceptionCache exceptionCache;
	
	private static CloudTestCache testCache;
	
	private static CloudSystemConfigCache systemConfigCache;
	
	private static final int DEFAULT_TIMEOUT = 30;

	static {
		lowCache = new TargetLowIndexCache(100, DEFAULT_TIMEOUT);
		highCache = new TargetTopIndexCache(100, DEFAULT_TIMEOUT);
		openCache = new TargetOpenIndexCache(100, DEFAULT_TIMEOUT);
		closeCache = new TargetCloseIndexCache(100, DEFAULT_TIMEOUT);
		exceptionCache = new CloudTradeExceptionCache(100, DEFAULT_TIMEOUT);
		testCache =  new CloudTestCache(100, DEFAULT_TIMEOUT);
		systemConfigCache = new CloudSystemConfigCache(100, DEFAULT_TIMEOUT);
	}
	
	public static CloudTradeExceptionCache getCloudTradeExceptionCache() {
		return exceptionCache;
	}

	public static TargetLowIndexCache getLowCache() {
		return lowCache;
	}

	public static TargetTopIndexCache getHighCache() {
		return highCache;
	}

	public static TargetOpenIndexCache getOpenCache() {
		return openCache;
	}

	public static TargetCloseIndexCache getCloseCache() {
		return closeCache;
	}
	
	public static CloudTestCache getTestCache() {
		return testCache;
	}
	
	public static CloudSystemConfigCache getSystemConfigCache() {
		return systemConfigCache;
	}
	
	public static void main(String[] args) throws SQLException, InterruptedException {
//		GlobalCacheUtil.getLowCache().put("123", 123);
//		System.out.println(GlobalCacheUtil.getLowCache().get("123"));
		
		/*CloudTradeException exc = GlobalCacheUtil.getCloudTradeExceptionCache().get("20161125");
		if (exc != null) {
			System.out.println(exc.getDay() + "  " + exc.getHours() + "  " + exc.getHourList().size());
		}*/
		
/*		System.out.println(GlobalCacheUtil.getTestCache().get("top"));
		CloudTestCacheHandler.updateCache("top", 100);
		GlobalCacheUtil.getTestCache().put("top", 100l);
		System.out.println(GlobalCacheUtil.getTestCache().get("top"));
		Thread.sleep(30000);
		System.out.println(GlobalCacheUtil.getTestCache().get("top"));
		Thread.sleep(30000);
		System.out.println(GlobalCacheUtil.getTestCache().get("top"));*/
		
		Runnable run = new Runnable(){
			@Override
			public void run() {
				System.out.println(GlobalCacheUtil.getSystemConfigCache().get("index.vari.limit"));
				try {
					Thread.sleep(32000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(GlobalCacheUtil.getSystemConfigCache().get("index.vari.limit"));
			}};
		
		for (int i = 0 ;i<100; i++) {
			Thread t = new Thread(run);
			t.start();
		}
			
	}
}
