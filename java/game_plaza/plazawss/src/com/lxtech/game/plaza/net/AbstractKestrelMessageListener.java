package com.lxtech.game.plaza.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public abstract class AbstractKestrelMessageListener implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(AbstractKestrelMessageListener.class);
	
	protected String queueName;
	
	public AbstractKestrelMessageListener(String queueName) {
		this.queueName = queueName;
	}
	
	public abstract void handleReceivedMessage(String message);
	
	@Override
	public void run() {
		while(true) {
			try {
				String message = KestrelConnector.dequeue(this.queueName);
				if (!Strings.isNullOrEmpty(message)) {
					long time1 = System.currentTimeMillis();
					this.handleReceivedMessage(message);
					System.out.println("cost time:" + (System.currentTimeMillis() - time1) + message);
				} else {
					Thread.sleep(200);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

}
