package com.lxtech.cloud.main;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.util.CrawlerConfig;

public class MainExecutor {
	
	private final static Logger logger = LoggerFactory.getLogger(MainExecutor.class);
	
	public void execute() throws InterruptedException {
		String addr = CrawlerConfig.get("ws.addr");
		int port = Integer.valueOf(CrawlerConfig.get("ws.port")).intValue();
		try {
			WebSocketEndpoint endpoint = new WebSocketEndpoint(addr, port);
			endpoint.connect();
			logger.info("Socket connection has been created.");
		} catch (URISyntaxException e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		try {
			new MainExecutor().execute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
	}
}