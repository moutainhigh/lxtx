package com.jxt.pay.helper;

import org.springframework.beans.factory.InitializingBean;

public abstract class BaseHelper implements InitializingBean{

	private static final int DEFAULTSLEEPMINUTES = 2;
	
	private int sleepMinutes = DEFAULTSLEEPMINUTES;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
		
		if(sleepMinutes > 0){
			new Thread(){
				public void run(){
					while(true){
						try {
							Thread.sleep(1000 * 60 * sleepMinutes);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						init();
					}
				}
			}.start();		
		}
	}
	
	protected abstract void init();

	public void setSleepMinutes(int sleepMinutes) {
		this.sleepMinutes = sleepMinutes;
	}

	
}
