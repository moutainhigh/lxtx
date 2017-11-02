package com.lxtx.robot.cache;

import java.sql.SQLException;

import com.lxtx.robot.handler.SystemConfigHandler;

public class SystemConfigCache extends AbstractLoadingCache<String, String> {
	public SystemConfigCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	public int getInt(String key) {
		String v = this.get(key);
		return Integer.parseInt(v.trim());
	}

	public long getLong(String key) {
		String v = this.get(key);
		return Long.parseLong(v.trim());
	}
	
	public boolean getBoolean(String key){
		String v = this.get(key);
		return Boolean.parseBoolean(v.trim());
	}

	@Override
	public String loadFromStore(String k) {
		try {
			return SystemConfigHandler.queryByKey(k).getValue();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
