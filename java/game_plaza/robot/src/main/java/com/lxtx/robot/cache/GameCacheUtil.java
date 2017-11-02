package com.lxtx.robot.cache;

public class GameCacheUtil {

	private static SystemConfigCache systemConfigCache;

	static {
		systemConfigCache = new SystemConfigCache(500, 60);
	}

	public static SystemConfigCache getSystemConfigCache() {
		return systemConfigCache;
	}
}
