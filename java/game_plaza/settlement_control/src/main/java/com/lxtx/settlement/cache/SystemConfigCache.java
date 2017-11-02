package com.lxtx.settlement.cache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lxtx.settlement.dbmodel.SystemConfigData;
import com.lxtx.settlement.handler.SystemConfigHandler;

public class SystemConfigCache {

	private static SystemConfigCache inst;

	public static SystemConfigCache getInstance() {
		if (inst == null) {
			inst = new SystemConfigCache(60 * 1000);
		}
		return inst;
	}

	private SystemConfigCache(int interval) {
		this.loadInterval = interval;
		this.configMap = new HashMap<>();
	}

	public int getInt(String key) {
		String v = this.get(key);
		return Integer.parseInt(v.trim());
	}

	public long getLong(String key) {
		String v = this.get(key);
		return Long.parseLong(v.trim());
	}

	public boolean getBoolean(String key) {
		String v = this.get(key);
		return Boolean.parseBoolean(v.trim());
	}

	public String get(String key) {
		this.check();
		return this.configMap.get(key);
	}

	private void check() {
		if (this.configMap.size() <= 0 || System.currentTimeMillis() - lastLoadTime >= loadInterval) {
			this.loadFromStore();
		}
	}

	public void loadFromStore() {
		try {
			List<SystemConfigData> datas = SystemConfigHandler.queryAll();
			this.lastLoadTime = System.currentTimeMillis();
			this.configMap.clear();
			for (SystemConfigData data : datas) {
				this.configMap.put(data.getKey(), data.getValue());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private int loadInterval;
	private long lastLoadTime;
	private Map<String, String> configMap;
}
