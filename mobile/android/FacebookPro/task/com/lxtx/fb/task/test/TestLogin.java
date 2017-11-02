package com.lxtx.fb.task.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lxtx.fb.handler.UserHandler;
import com.lxtx.fb.pojo.User;
import com.lxtx.fb.task.service.FbService;

public class TestLogin {

	public static void main(String[] args){
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
		
		UserHandler userHandler = (UserHandler)ctx.getBean("userHandler");
		
		User user = userHandler.select(379l);
		
		FbService fbService = (FbService)ctx.getBean("fbService");
		
		fbService.init(user);
	}
	
}
