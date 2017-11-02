package com.lxtech.cloudtrade.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.yanf4j.core.impl.StandardSocketOption;
import com.google.common.base.Strings;
import com.lxtech.cloudtrade.util.CrawlerConfig;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.MemcachedClientStateListener;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.KestrelCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * This connector provided function to push price update information to a message queue
 * @author wangwei
 *
 */
public class KestrelConnector {
	private static final Logger logger = LoggerFactory.getLogger(KestrelConnector.class);
	
	public static final String QUEUE_NAME = "TOPIC";
	public static final String QUEUE_NAME_GEN = "GENDATA";
	
	private static MemcachedClient memcachedClient;
	
	private static long timeCounter;
	
	private static final long DEFAULT_TIME_GAP = 300*1000;
	
	static {
		memcachedClient = createMemcacheClient();
		timeCounter = System.currentTimeMillis();
		/*MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(addr));
		builder.setCommandFactory(new KestrelCommandFactory());
		*//**
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
		 *//*
		builder.setSocketOption(StandardSocketOption.TCP_NODELAY,  false); 
		builder.setConnectionPoolSize(2);
		try {
			memcachedClient = builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		memcachedClient.setOptimizeGet(false);
		memcachedClient.setConnectTimeout(60000);
		memcachedClient.setMergeFactor(10);
		memcachedClient.setOptimizeMergeBuffer(false);*/
	}
	
	private static MemcachedClient createMemcacheClient() {
		logger.info("now create a new instance of kestrel client.");
		String addr = CrawlerConfig.get("mq.server.addr") + ":" + CrawlerConfig.get("mq.server.port");
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(addr));
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
		builder.setSocketOption(StandardSocketOption.TCP_NODELAY,  false); 
		builder.setConnectionPoolSize(2);
		MemcachedClient	client  = null;
		try {
			client = builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.setOptimizeGet(false);
		client.setConnectTimeout(60000);
		client.setMergeFactor(1);
		client.setOptimizeMergeBuffer(false);		
		return client;
	}
	
	public static void shutdown() {
		if (memcachedClient != null) {
			try {
				//now shutdown the old client
				memcachedClient.shutdown();
				logger.info("shutdown the kestrel client");
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public static void enqueue(String message) {
		try {
			memcachedClient.set(QUEUE_NAME, 0, message);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			e.printStackTrace();
		}
	}
	
	public static void enqueue(String queueName, String message) {
		try {
			memcachedClient.set(queueName, 0, message);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			e.printStackTrace();
		}
	}	

	public static String dequeue(String queueName) {
		long curtime = System.currentTimeMillis(); 
		long gap = curtime - timeCounter;
		if (gap >= DEFAULT_TIME_GAP) {
			timeCounter = curtime;
			KestrelConnector.shutdown();
			memcachedClient = createMemcacheClient();
		}
		
		try {
			return (String)memcachedClient.get(queueName, 1000);
		} catch (InterruptedException | MemcachedException e) {
			logger.error(e.getMessage());
		} catch (TimeoutException ex) {
			logger.error(ex.getMessage());
		}	
		return "";
	}
	
	public static String dequeue() {
		try {
			return (String)memcachedClient.get(QUEUE_NAME, 1000);
		} catch (InterruptedException | MemcachedException e) {
			logger.error(e.getMessage());
		} catch (TimeoutException ex) {
			logger.error(ex.getMessage());
		}
		return "";
	}
	
	public static Map getStats() {
		Map<InetSocketAddress, Map<String, String>>  map = null;
		try {
			map = memcachedClient.getStats();
		} catch (MemcachedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void main(String[] args) {
		/*try {
			Map<InetSocketAddress, Map<String, String>> map = memcachedClient.getStatsByItem(QUEUE_NAME);
		} catch (MemcachedException | InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}*/
		while(true) {
			try {
				String message = KestrelConnector.dequeue("GENDATA");
				if (!Strings.isNullOrEmpty(message)) {
					logger.info("retrieved message:" + message);
				}
				Thread.sleep(1);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}		
		
	}
}
