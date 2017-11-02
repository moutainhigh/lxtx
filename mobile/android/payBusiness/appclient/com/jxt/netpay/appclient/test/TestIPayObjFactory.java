package com.jxt.netpay.appclient.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jxt.netpay.appclient.pojo.IPayObj;
import com.jxt.netpay.appclient.service.IPay;
import com.jxt.netpay.appclient.util.IPayObjFactory;

public class TestIPayObjFactory {

	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyyMMddhhmmss");
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
	
		IPayObjFactory payFactory = (IPayObjFactory)ctx.getBean("payFactory");
	
		IPayObj payObj = payFactory.getIPayObj(1);
		
		System.out.println(payObj.getCallBackUrl());
	}
	
}
