package com.lxtech.cloud.net;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.settlement.config.SettlementQueueConfig;

//import com.lxtech.cloud.util.CrawlerConfig;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.KestrelCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * This connector provided function to push price update information to a
 * message queue
 * 
 * @author wangwei
 *
 */
public class KestrelConnector {

	public static String SETTLEMENT_CTRL_QUEUE;
	public static String SETTLEMENT_OPER_QUEUE;
	private final static Logger logger = LoggerFactory.getLogger(KestrelConnector.class);
	private static MemcachedClient memcachedClient;

	static {
		String addr = SettlementQueueConfig.SERVER_ADDRESS + ":" + SettlementQueueConfig.SERVER_PORT;
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
		try {
			memcachedClient = builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		memcachedClient.setOptimizeGet(false);
		memcachedClient.setConnectTimeout(60000);
	}

	public static void enqueue(String message) {
		enqueue(SETTLEMENT_OPER_QUEUE, message);
	}

	public static String dequeue() {
		return dequeue(SETTLEMENT_CTRL_QUEUE);

	}

	public static void enqueue(String queueName, String message) {
		try {// SETTLEMENT_OPER_QUEUE
			logger.info("c2s protocal msg:{}", message);
			memcachedClient.set(queueName, 0, message);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			e.printStackTrace();
		}
	}

	public static String dequeue(String queueName) {
		try {// SETTLEMENT_CTRL_QUEUE
			String msg = (String) memcachedClient.get(queueName, 500);
			if (null != msg && !msg.isEmpty()) {
				logger.info("s2c protocal msg:{}", msg);
			}
			return msg;
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			e.printStackTrace();
		}
		return "";
	}

}
