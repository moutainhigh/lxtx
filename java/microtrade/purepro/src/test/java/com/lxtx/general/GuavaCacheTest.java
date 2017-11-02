package com.lxtx.general;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class GuavaCacheTest {
	private static Cache<String, String> cache = CacheBuilder.newBuilder()
				// 设置cache的初始大小为10，要合理设置该值
				.initialCapacity(10)
				// 设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
				.concurrencyLevel(5)
				// 设置cache中的数据在写入之后的存活时间为10秒
				.expireAfterWrite(10, TimeUnit.SECONDS)
				// 构建cache实例
				.build();
	
	public static void main(String[] args) {
		cache.put("key1", "value1");
		
		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getName() + ":" + cache.getIfPresent("key1"));
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName() + ":" + cache.getIfPresent("key1"));
				}
			});
			t.start();
		}
	}
	
}
