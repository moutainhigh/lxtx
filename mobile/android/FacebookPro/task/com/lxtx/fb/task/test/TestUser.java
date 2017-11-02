package com.lxtx.fb.task.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lxtx.fb.task.service.AccountService;

public class TestUser {

	public static void main(String[] args){
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
		
		AccountService accountService = (AccountService)ctx.getBean("accountService");
		
		accountService.exec();
	}
	
	
}
