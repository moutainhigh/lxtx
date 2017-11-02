package com.lxtech.cloud.util.cache;

import java.sql.SQLException;

import com.lxtech.cloud.db.CloudTestCacheHandler;

public class CloudTestCache extends SystemCache<String, Long>{

	public CloudTestCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public Long loadFromStore(String k) {
		try {
			com.lxtech.cloud.db.model.CloudTestCache cache = CloudTestCacheHandler.retrieveCache();
			if (k.equals("top")) {
				return cache.getTop();
			} else if (k.equals("bottom")) {
				return cache.getBottom();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0l;
	}
}
