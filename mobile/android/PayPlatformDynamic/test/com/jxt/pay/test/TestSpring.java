package com.jxt.pay.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jxt.pay.appclient.service.dynamic.net.DynamicServiceFactory;
import com.jxt.pay.helper.BlackMobileHelper;

import junit.framework.TestCase;

public class TestSpring extends TestCase{

	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
	
	public void test(){
//		DynamicServiceFactory factory = (DynamicServiceFactory)ctx.getBean("dynamicServiceFactory");
		
//		String params = "url=http://112.25.8.238:8001/o/vn/16b35634c3d787c60589&type=nj&theNo=1&imsi460001153105779&imei=869460011612203&mobileId=2892&price=0.10&channel=ABCDABCDABCDABCD";
		
//		System.out.println(factory.dynamic(params));
		
//		BlackMobileHelper helper = (BlackMobileHelper)ctx.getBean("blackMobileHelper");
//		
//		System.out.println("isBlack : "+helper.isBlack("13811155779"));
		
		
	
	
	
	}
	
}
