package com.lxtx.fb.task.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lxtx.fb.task.service.ProxyService;

public class TestProxy {
	
	public static void main(String[] args){
	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
		
		ProxyService proxyService = (ProxyService)ctx.getBean("proxyService");
		
		while(proxyService.exec()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
