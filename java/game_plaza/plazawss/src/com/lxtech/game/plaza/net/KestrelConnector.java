package com.lxtech.game.plaza.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.yanf4j.core.impl.StandardSocketOption;
import com.google.common.base.Strings;
import com.lxtech.game.plaza.util.CrawlerConfig;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
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
	
//	private static MemcachedClient memcachedClient;
	
	private static ConcurrentHashMap<String, MemcachedClient> clientMap;
	
	private static long timeCounter;
	
	private static final long DEFAULT_TIME_GAP = 300*1000;
	
	static {
//		memcachedClient = createMemcacheClient();
		timeCounter = System.currentTimeMillis();
		clientMap = new ConcurrentHashMap<String, MemcachedClient>();
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
		builder.setConnectionPoolSize(1);
		MemcachedClient	client  = null;
		try {
			client = builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.setOptimizeGet(false);
		client.setOpTimeout(30000);
		client.setConnectTimeout(60000);
		client.setMergeFactor(1);
		client.setOptimizeMergeBuffer(false);		
		return client;
	}
	
	private static MemcachedClient getMemcachedClient(String queueName) {
		MemcachedClient client = clientMap.get(queueName); 
		if (client != null) {
			return client; 
		} else {
			client = createMemcacheClient();
			clientMap.put(queueName, client);
			return client;
		}
	}
	
	public static void shutdown(String queueName) {
		MemcachedClient memcachedClient = clientMap.get(queueName); 
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
	
	public static void enqueue(String queueName, String message) {
		MemcachedClient memcachedClient = getMemcachedClient(queueName);
		boolean result = false;
		int retry = 0;
		while(!result && retry < 3) {
			try {
				memcachedClient.set(queueName, 0, message);
				break;
			} catch (TimeoutException | InterruptedException | MemcachedException e) {
				logger.error(e.getMessage());
				try {
					memcachedClient.shutdown();
				} catch (IOException e1) {
					logger.error(e1.getMessage());
				}
				memcachedClient = createMemcacheClient();//getMemcachedClient(queueName);
				clientMap.put(queueName, memcachedClient);
				retry++;
			}
		}
	}	

	public static String dequeue(String queueName) {
		MemcachedClient memcachedClient = getMemcachedClient(queueName);
		int retry = 0;
		String val = "";
		while(retry < 3) {
			try {
				val = (String)memcachedClient.get(queueName, 1000);
				break;
			} catch (InterruptedException | MemcachedException | TimeoutException ex) {
				logger.error(ex.getMessage());
				retry++;
				memcachedClient = createMemcacheClient();//getMemcachedClient(queueName);
				clientMap.put(queueName, memcachedClient);
			}			
		}
		return val;
	}
	
	public static void main(String[] args) {
/*		while(true) {
			try {
				String message = KestrelConnector.dequeue("GENDATA");
				if (!Strings.isNullOrEmpty(message)) {
					logger.info("retrieved message:" + message);
				}
				Thread.sleep(1);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}		*/
		
		KestrelConnector.enqueue("testqueue", "hello");
		System.out.println(KestrelConnector.dequeue("testqueue"));
		KestrelConnector.shutdown("testqueue");
	}
}
