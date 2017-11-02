package com.lxtech.cloud.util.cache;

import java.sql.SQLException;

import com.lxtech.cloud.db.CloudTradeExceptionHandler;
import com.lxtech.cloud.db.model.CloudTradeException;

public class CloudTradeExceptionCache extends SystemCache<String, CloudTradeException>{

	public CloudTradeExceptionCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public CloudTradeException loadFromStore(String k) {
		//return null;
		try {
			CloudTradeException exc = CloudTradeExceptionHandler.retrieveTradeExceptionByDay(Long.valueOf(k));
			if (exc == null) {
				return new CloudTradeException();
			}
			return exc;
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		return new CloudTradeException();
	}

}
