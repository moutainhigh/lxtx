package com.lxtech.cloudtrade.cache;

import java.sql.SQLException;

import com.lxtech.cloudtrade.db.CloudTargetHandler;
import com.lxtech.cloudtrade.db.model.CloudTarget;

public class CurrentIndexCache extends SystemCache<String, Integer>{

	public CurrentIndexCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public Integer loadFromStore(String k) {
		String[] keyArr = k.split(SystemCache.KEY_SEPARATOR);
		CloudTarget target;
		try {
			target = CloudTargetHandler.retrieveTargetData(keyArr[0], keyArr[1]);
			return target.getCurrent_index();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void main(String[] args) {
		
	}

}
