package com.lxtech.game.plaza.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * system cache is used to cache some system-level values
 * @author wangwei
 *
 */
public abstract class SystemCache<K, V> {
	private int maxSize = 0;
	//the value will be invalidated after expire seconds
	private int expire = 0;
	
	protected Cache<K, V> cache;
	
	public SystemCache(int maxSize, int expire) {
		this.maxSize = maxSize;
		this.expire = expire;
		
		this.cache
        //CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
        = CacheBuilder.newBuilder()
        //设置并发级别为8，并发级别是指可以同时写缓存的线程数
        .concurrencyLevel(20)
        //设置写缓存后n秒钟过期
        .expireAfterAccess(this.expire, TimeUnit.SECONDS)
        //设置缓存容器的初始容量为10
        .initialCapacity(10)
        //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
        .maximumSize(this.maxSize)
        //设置要统计缓存的命中率
        .recordStats()
        //设置缓存的移除通知
        .removalListener(new RemovalListener<Object, Object>() {
            @Override
            public void onRemoval(RemovalNotification<Object, Object> notification) {
                System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
            }
        })
        .build();
	}

	public V get(K key) {
		return this.cache.getIfPresent(key);
	}
	
	public void put(K key, V value) {
		System.out.println("put value for key:"+this.get(key));
		this.cache.put(key, value);
	}
}
