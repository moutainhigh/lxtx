package com.lxtx.service.cache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.model.vo.PersonalOrderStat;

@Service
public class PersonalOrderCache {
	@Autowired
	private CloudOrderMapper cloudOrderMapper;

	private LoadingCache<String, PersonalOrderStat> cache
	// CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
			= CacheBuilder.newBuilder()
					// 设置并发级别为8，并发级别是指可以同时写缓存的线程数
					.concurrencyLevel(8)
					// 设置写缓存后1天后过期
					.expireAfterWrite(3600, TimeUnit.SECONDS)
					// 设置缓存容器的初始容量为10
					.initialCapacity(10)
					// 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
					.maximumSize(1000)
					// 设置要统计缓存的命中率
					.recordStats()
					// build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
					.build(new CacheLoader<String, PersonalOrderStat>() {
						@Override
						public PersonalOrderStat load(String key) throws Exception {
							// as key takes the form "5809_BTC"
							int idx = key.indexOf("_");
							if (idx > 0) {
								int uid = Integer.valueOf(key.substring(0, idx)).intValue();
								String subject = key.substring(idx + 1);
								
								//we only care about the orders created today
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								Date now = new Date();
								String startDate = sdf.format(now);
								String endDate = sdf.format(new Date(now.getTime() + 86400* 1000));
								PersonalOrderStat stat = cloudOrderMapper.getOrderStat(uid, subject, startDate, endDate);
								stat.setDay(startDate);								
								return stat;
							} else {
								PersonalOrderStat stat = new PersonalOrderStat();
								stat.setHideTimes(0);
								stat.setTotalAmount(0);
								stat.setTotalOrders(0);
								String day = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
								stat.setDay(day);
								return stat;
							}
						}
					});

	/**
	 * get the cache instance
	 * @return
	 */
	public Cache<String, PersonalOrderStat> getCache() {
		return cache;
	}
	
	public PersonalOrderStat getCache(int uid, String subject) {
		String key = getKey(uid, subject);
		try {
			return this.cache.get(key);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateCache(int uid, String subject, PersonalOrderStat stat) {
		this.cache.put(getKey(uid, subject), stat);
	}
	
	public void clearCache(int uid, String subject) {
		this.cache.invalidate(getKey(uid, subject));
	}
	
	private static String getKey(int uid , String subject) {
		return uid + "_" + subject;
	}
}