package com.lxtech.game.plaza.cache;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Lists;

public class OnlineUserCache extends SystemCache<Long, Long>{
	public OnlineUserCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	public ConcurrentMap<Long, Long> getDataMap() {
		return this.cache.asMap();
	}
	
	public List<Long> getActiveUserList() {
		ConcurrentMap<Long, Long> map = this.getDataMap();
		List<Long> uidList = Lists.newArrayList();
		for (Long uid : map.keySet()) {
			Long connId = map.get(uid);
			if (connId != null && connId > 0l) {
				uidList.add(uid);
			}
		}
		return uidList;
	}
	
	public static void main(String[] args) {
		OnlineUserCache cache = new OnlineUserCache(100, 10);
		cache.put(123l, 1234l);
		cache.put(124l, 1235l);
		
		System.out.println(cache.get(123l));
		
		try {
			Thread.sleep(13 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(cache.get(123l));
	}
}