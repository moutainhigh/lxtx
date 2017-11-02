package com.lxtech.cloud.main;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lxtech.cloud.util.CrawlerConfig;
import com.lxtech.cloud.util.cache.CloudSystemConfigCache;

public class WebSocketRunner implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(WebSocketRunner.class);
	
	private static final String WS_ADDR = "ws.addr";
	private static final String WS_PORT = "ws.port";

	private String addr;

	private int port;

	private int extraSecs;

	public WebSocketRunner(String addr, int port, int extraSecs) {
//		this.addr = addr;
//		this.port = port;
		this.extraSecs = extraSecs;
	}

	@Override
	public void run() {
		while (true) {
			try {
					String dbAddr = CloudSystemConfigCache.getSystemConfig("ws.addr");
					String dbPort = CloudSystemConfigCache.getSystemConfig("ws.port");
					
					if (!Strings.isNullOrEmpty(dbAddr) && !Strings.isNullOrEmpty(dbPort)) {
						logger.info("read config from cache, addr is :" + dbAddr + " port is: " + dbPort);
						this.addr = dbAddr;
						this.port = Integer.valueOf(dbPort);
					} else {
						String addr = CrawlerConfig.get(WS_ADDR);
						int port = Integer.valueOf(CrawlerConfig.get(WS_PORT)).intValue();
						this.addr = addr;
						this.port = Integer.valueOf(port);
					}
					
					WebSocketEndpoint endpoint = new WebSocketEndpoint(this.addr, this.port);
					endpoint.connect();
					logger.info("try to connect to socket:"+this.addr + ":" + this.port);
					Thread.sleep(2 * 60 * 1000 + extraSecs * 1000);
					endpoint.close();
					endpoint = null;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
/*			String dbAddr = CloudSystemConfigHandler.readSystemConfig(WS_ADDR);
			String dbPort = CloudSystemConfigHandler.readSystemConfig(WS_PORT);*/
		String addr = CrawlerConfig.get(WS_ADDR);
		int port = Integer.valueOf(CrawlerConfig.get(WS_PORT)).intValue();			
		String dbAddr = CloudSystemConfigCache.getSystemConfig("ws.addr");
		String dbPort = CloudSystemConfigCache.getSystemConfig("ws.port");
		
		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++");
		logger.info(dbAddr);
		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		if (!Strings.isNullOrEmpty(dbAddr) && !Strings.isNullOrEmpty(dbPort)) {
			logger.info("read config from cache, addr is :" + dbAddr + " port is: " + dbPort);
			addr = dbAddr;
			port = Integer.valueOf(dbPort);
		}

		String crawlerActive = CloudSystemConfigCache.getSystemConfig("ws.crawler.active");
		if (Strings.isNullOrEmpty(crawlerActive) || crawlerActive.equals("1")) { //it will be launched as default
			Thread t1 = new Thread(new WebSocketRunner(addr, port, 11));
			Thread t2 = new Thread(new WebSocketRunner(addr, port, 23));
			Thread t3 = new Thread(new WebSocketRunner(addr, port, 32));
			t1.start();
			t2.start();
			t3.start();			
		} else {
			Thread t = new Thread(new DataCopyer("BU"));
			t.start();
			Thread t2 = new Thread(new DataCopyer("AG"));
			t2.start();
			Thread t3 = new Thread(new DataCopyer("CU"));
			t3.start();			
		}
		
		/*String copySwitch = CloudSystemConfigCache.getSystemConfig("data.copy.switch");
		if (!Strings.isNullOrEmpty(copySwitch) && copySwitch.equals("1")) {
			Thread t = new Thread(new DataCopyer("BU"));
			t.start();			
		}*/
	}
}