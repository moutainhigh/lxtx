package com.lxtech.cloud.cache;

import com.lxtech.cloud.db.model.CloudUser;

public class GlobalCacheUtil {

	private static UserCache userCache;
	
	private static AccessTokenCache atCache;
	
	static {
		userCache = new UserCache(100, 120);
		atCache = new AccessTokenCache(100, 3600);
	}

	public static UserCache getUserCache() {
		return userCache;
	}
	
	public static AccessTokenCache getATCache() {
		return atCache;
	}

	public static void main(String[] args) {
//		List dataList = GlobalCacheUtil.getTrendCache().get("gendata:BU");
//		System.out.println(dataList.size());
		CloudUser user = GlobalCacheUtil.getUserCache().get("opPADwBmRX-6Uz9RsGCDs44Bb6Do");
		System.out.println(user.getHeadimgurl());
	}
}
