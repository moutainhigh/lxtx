package com.lxtx.robot.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * system cache is used to cache some system-level values
 * @author wangwei
 */
public abstract class AbstractLoadingCache<K, V> {
	private int maxSize = 0;
	//the value will be invalidated after expire seconds
	private int expire = 0;
	
	private LoadingCache<K, V> cache;
	
	public abstract V loadFromStore(K k);
	
	public AbstractLoadingCache(int maxSize, int expire) {
		this.maxSize = maxSize;
		this.expire = expire;
		
		this.cache
        //CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
        = CacheBuilder.newBuilder()
        //设置并发级别为8，并发级别是指可以同时写缓存的线程数
        .concurrencyLevel(8)
        //设置写缓存后8秒钟过期
        .expireAfterWrite(this.expire, TimeUnit.SECONDS)
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
        //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
        .build(
                new CacheLoader<K, V>() {
                    @Override
                    public V load(K key) throws Exception {
                        System.out.println("load " + key);
                        V value = AbstractLoadingCache.this.loadFromStore(key);
                        System.out.println("value is:" + value.toString());
                        return value;
                    }
                }
        );
	}

	public V get(K key) {
		try {
			return this.cache.get(key);
		} catch (ExecutionException e) {
			return null;
		}
	}
	
	public void put(K key, V value) {
		this.cache.put(key, value);
	}
}
