package com.lxtech.cloud.util.cache;

import java.sql.SQLException;

import com.lxtech.cloud.db.CloudTargetStatHandler;
import com.lxtech.cloud.db.model.CloudTargetIndexStat;

/**
 * 用于获取某标的的行情最高值
 * @author wangwei
 *
 */
public class TargetOpenIndexCache extends SystemCache<String, Double>{
	public TargetOpenIndexCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public Double loadFromStore(String k) {
		try {
			CloudTargetIndexStat stat = CloudTargetStatHandler.retrieveTargetStat(k);
			if (stat != null) {
				return (double)stat.getOpen();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return CacheConstant.INVALID_INDEX_VALUE;
	}
}