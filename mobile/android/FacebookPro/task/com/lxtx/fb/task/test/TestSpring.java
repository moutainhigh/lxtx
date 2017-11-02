package com.lxtx.fb.task.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lxtx.fb.task.service.TaskService;

public class TestSpring {

	public static void main(String[] args){
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
		
		TaskService taskService = (TaskService)ctx.getBean("taskService");
		
		taskService.exec();
	}
	
	
}
