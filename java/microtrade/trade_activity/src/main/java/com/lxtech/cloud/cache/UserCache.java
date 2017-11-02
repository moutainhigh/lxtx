package com.lxtech.cloud.cache;

import java.sql.SQLException;

import com.lxtech.cloud.db.CloudUserHandler;
import com.lxtech.cloud.db.model.CloudUser;

public class UserCache extends SystemCache<String, CloudUser>{
	public UserCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	/**
	 * k follows this format: 
	 * appname:subject
	 */
	@Override
	public CloudUser loadFromStore(String k) {
		try {
			return CloudUserHandler.getCloudUser(k);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
