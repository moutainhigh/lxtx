package com.lxtech.cloudtrade.cache;

import java.util.List;

import com.lxtech.cloudtrade.db.HqDataHandler;
import com.lxtech.cloudtrade.db.model.IndexMinuteData;
import com.lxtech.cloudtrade.util.TimeUtil;

public class TrendDataCache extends SystemCache<String, List>{
	public TrendDataCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	/**
	 * k follows this format: 
	 * appname:subject
	 */
	@Override
	public List loadFromStore(String k) {
		String day = TimeUtil.getMostRecentWorkday(false);
		String[] keyArr = k.split(SystemCache.KEY_SEPARATOR);
		List<IndexMinuteData> dataList = null;
		try {
			dataList = HqDataHandler.queryMinuteData(keyArr[0], day, keyArr[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
}
