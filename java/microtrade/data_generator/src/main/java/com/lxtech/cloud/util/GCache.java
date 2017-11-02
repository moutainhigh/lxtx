package com.lxtech.cloud.util;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class GCache<K,V> {
	//at most maxSize keys will be stored in the cache
	@SuppressWarnings("unused")
	private int maxSize = 0;
	//the value will be invalidated after expire seconds
	@SuppressWarnings("unused")
	private int expire = 0;
	
	private Cache<K, V> cache;
	
	public GCache(int maxSize, int expire) {
		this.maxSize = maxSize;
		this.expire = expire;
		this.cache = CacheBuilder.newBuilder().maximumSize(this.maxSize)  
	            .expireAfterWrite(this.expire, TimeUnit.SECONDS).build();
	}
	
	public V get(K key) {
		return this.cache.getIfPresent(key);
	}
	
	public void put(K key, V value) {
		this.cache.put(key, value);
	}
	
	public static void main(String[] args) throws InterruptedException {
		GCache<String, String> cache = new GCache<String, String>(100, 5);
		cache.put("key", "value");
		System.out.println(cache.get("key"));
		Thread.sleep(5000);
		System.out.println(cache.get("key"));
	}
}
