package com.lxtx.fb.task.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lxtx.fb.handler.TaskHandler;
import com.lxtx.fb.pojo.Task;
import com.lxtx.fb.task.service.TaskService;

public class TestTask {

	public static void main(String[] args){
		

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
		
		TaskHandler taskHandler = (TaskHandler)ctx.getBean("taskHandler");
		
		Task task = taskHandler.select(1225l);
		
		TaskService taskService = (TaskService)ctx.getBean("taskService");
		
		taskService.execOne(task);
		
		
	}
	
	
}
