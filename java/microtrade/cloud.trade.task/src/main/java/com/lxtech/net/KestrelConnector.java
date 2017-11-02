package com.lxtech.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.lxtech.util.SystemProperty;

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
	public static final String QUEUE_NAME = "GENDATA";
	private MemcachedClient memcachedClient;
//	private static MemcachedClient memcachedClient2;
	
	private MemcachedClient createMemcacheClient(String addr) {
		//String addr = CrawlerConfig.get("mq.server.addr") + ":" + CrawlerConfig.get("mq.server.port");
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
		builder.setConnectionPoolSize(2);
		MemcachedClient	client  = null;
		try {
			client = builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.setOptimizeGet(false);
		client.setConnectTimeout(60000);
		return client;
	}
	
	public KestrelConnector() {
		String addr = SystemProperty.getProperty("mq.server.addr") + ":" + SystemProperty.getProperty("mq.server.port");
		memcachedClient = createMemcacheClient(addr);
	}
	
	public void enqueue(String message) {
		try {
			memcachedClient.set(QUEUE_NAME, 0, message);
//			memcachedClient2.set(QUEUE_NAME, 0, message);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			e.printStackTrace();
		}
	}

	public Map<InetSocketAddress, Map<String, String>> getStats() throws MemcachedException, InterruptedException, TimeoutException {
		Map<InetSocketAddress, Map<String, String>>  map = memcachedClient.getStats();
		return map;
	}
	
	public String dequeue() {
		try {
			return (String)memcachedClient.get(QUEUE_NAME, 500);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void shutdown() throws IOException {
		this.memcachedClient.shutdown();
	}
	
	public static void main(String[] args) {
		try {
			KestrelConnector connector = new KestrelConnector();
			Map<InetSocketAddress, Map<String, String>> map = connector.getStats();
			System.out.println("hello");
			connector.shutdown();
		} catch (MemcachedException | InterruptedException | TimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
