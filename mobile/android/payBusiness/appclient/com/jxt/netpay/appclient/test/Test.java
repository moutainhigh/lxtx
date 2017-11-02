package com.jxt.netpay.appclient.test;

import java.net.URLDecoder;

public class Test {

	public static void main(String[] args){
		String s = "http3A2F2F115.28.52.433A90202Fpay2Fsynch2Fnetpay2FiapppayNotify.do";
		
		System.out.println(URLDecoder.decode(s));
	}
	
}
