package com.lxtx.fb.task.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lxtx.fb.task.service.UserProxyService;

public class TestUserProxy {

	public static void main(String[] args){
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
		
		UserProxyService userProxyService = (UserProxyService)ctx.getBean("userProxyService");
		
		while(userProxyService.exec()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
