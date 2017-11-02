package com.lxtech.crawler.memcache;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Cache;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.KestrelCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class MemcacheTest {
	private static final String QUEUE_NAME = "TOPIC";
	private static MemcachedClient memcachedClient;
	private String ketrelQueueName = "abc";

	@BeforeClass
	public static void init() {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("119.29.98.103:22134"));
		builder.setCommandFactory(new KestrelCommandFactory());
		/**
		 * 设置连接池大小，即客户端个数 In a high concurrent enviroment,you may want to pool
		 * memcached clients. But a xmemcached client has to start a reactor
		 * thread and some thread pools, if you create too many clients,the cost
		 * is very large. Xmemcached supports connection pool instreadof client
		 * pool. you can create more connections to one or more memcached
		 * servers, and these connections share the same reactor and thread
		 * pools, it will reduce the cost of system. 默认的pool
		 * size是1。设置这一数值不一定能提高性能，请依据你的项目的测试结果为准。初步的测试表明只有在大并发下才有提升。
		 * 设置连接池的一个不良后果就是，同一个memcached的连接之间的数据更新并非同步的
		 * 因此你的应用需要自己保证数据更新的原子性（采用CAS或者数据之间毫无关联）。
		 */
		builder.setConnectionPoolSize(2);
		try {
			memcachedClient = builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		memcachedClient.setOptimizeGet(false);
		memcachedClient.setConnectTimeout(60000);
	}

	/*@Test
	public void testPutValue() throws TimeoutException, InterruptedException, MemcachedException {
		
		 * boolean flag = memcachedClient.set(ketrelQueueName, 0, "1:111");
		 * System.out.println(flag);
		 
		for (int i = 0; i < 100; i++) {
			try {
				memcachedClient.set(QUEUE_NAME, 30000, i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetValue() throws TimeoutException, InterruptedException, MemcachedException {
		Integer value;
		try {
			while (true) {
				value = (Integer) memcachedClient.get(QUEUE_NAME);
				if (value == null) {
					break;
				}
				System.out.println(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	@Test
	public void testAddGet() throws TimeoutException, InterruptedException, MemcachedException {
		memcachedClient.set("key1", 0, "value1");
		System.out.println((String)memcachedClient.get("key1"));
		System.out.println((String)memcachedClient.get("key1"));
	}
	
	@Test
	public void testCache() throws ExecutionException, InterruptedException {
	Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(100)  
            .expireAfterWrite(5, TimeUnit.SECONDS).build();  		
		cache.put("12345", "12345");
		
		for (int i = 0; i < 100; i++) {
			cache.put(i+"", i+"");
		}
		
		for (int i = 100; i < 200; i++) {
			cache.put(i+"", i+"");
		}
		
		System.out.println("size:" + cache.size());
		
		ConcurrentMap<String, String> map = cache.asMap();
		Set<String> keySet = map.keySet();
		for (String key: keySet) {
			System.out.println(key);
		}
	}

}
