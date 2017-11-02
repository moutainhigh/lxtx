package com.jxt.pay.appclient.utils;

public class SendSms {

	public static String send(String msg){
		
		SMSRemoteHandler handler = new SMSRemoteHandler();
		handler.setServiceURL("http://sms.4006555441.com/webservice.asmx");
		handler.setPwd("bggd89");
		handler.setSn("SDK-ZQ-0375");
		
		return handler.mt("13450224885", msg);
//		return handler.mt("13811155779", msg);
	}
	
	public static void main(String[] args){
		System.out.println(send("247:1330"));
	}
}
