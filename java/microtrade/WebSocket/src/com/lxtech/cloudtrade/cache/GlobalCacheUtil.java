package com.lxtech.cloudtrade.cache;

import java.util.List;

public class GlobalCacheUtil {

	private static TrendDataCache trendCache;
	
	private static CurrentIndexCache indexCache;

	static {
		trendCache = new TrendDataCache(100, 60);
		indexCache = new CurrentIndexCache(100, 60);
	}

	public static TrendDataCache getTrendCache() {
		return trendCache;
	}
	
	public static CurrentIndexCache getIndexCache() {
		return indexCache;
	}

	public static void main(String[] args) {
		List dataList = GlobalCacheUtil.getTrendCache().get("gendata:BU");
		System.out.println(dataList.size());
//		System.out.println(GlobalCacheUtil.getIndexCache().get("gendata:BU"));
	}
}
