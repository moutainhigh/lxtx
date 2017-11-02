package com.lxtech.cloud.okcoin.main;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.lxtech.cloud.okcoin.WebSocketService;

/**
 * WebSocket API
 * 
 * @author okcoin
 * 
 */
public class Main {
	private static Logger log = Logger.getLogger(Main.class);
	
	static class DataTask extends TimerTask {
		private WebSoketClient client;

		@Override
		public void run() {
			log.info("now executing the data sync task, ");
			// 国际站WebSocket地址 注意如果访问国内站 请将 real.okcoin.com 改为 real.okcoin.cn
			String url = "wss://real.okcoin.cn:10440/websocket/okcoinapi";
			if (this.client == null) {
				// 订阅消息处理类,用于处理WebSocket服务返回的消息
				WebSocketService service = new BuissnesWebSocketServiceImpl();
				// WebSocket客户端
				WebSoketClient client = new WebSoketClient(url, service);
				// 启动客户端
				client.start();
				// 添加订阅
				client.addChannel(BuissnesWebSocketServiceImpl.CHN_BTC);
				client.addChannel(BuissnesWebSocketServiceImpl.CHN_LTC);				
			} else {
				log.info("reconnect now");
				this.client.reConnect();
			}
		}
	}
	
	public static void main(String[] args) {
		Timer timer = new Timer();
		DataTask task = new DataTask();
		timer.scheduleAtFixedRate(task, 100, 180 * 1000);
	}
}
