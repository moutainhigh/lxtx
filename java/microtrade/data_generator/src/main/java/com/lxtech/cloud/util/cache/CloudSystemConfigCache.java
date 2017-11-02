package com.lxtech.cloud.util.cache;

import java.sql.SQLException;

import com.lxtech.cloud.db.CloudSystemConfigHandler;

public class CloudSystemConfigCache extends SystemCache<String, String>{
	public CloudSystemConfigCache(int maxSize, int expire) {
		super(maxSize, expire, false);
	}

	@Override
	public String loadFromStore(String k) {
		try {
			return CloudSystemConfigHandler.readSystemConfig(k);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
