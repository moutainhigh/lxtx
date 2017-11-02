package com.jxt.netpay.appclient.util;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

public class CommonUtil {

	public static String getRemortIP(HttpServletRequest request) { 
		String ip = request.getHeader("X-Forwarded-For");
		if(isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
		//多次反向代理后会有多个ip值，第一个ip才是真实ip 
			int index = ip.indexOf(",");
		 	if(index != -1){
		 		return ip.substring(0,index);
		 	}else{
		 		return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if(isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
			return ip;
		}

		return request.getRemoteAddr();
	}   
	
	private static boolean isNotEmpty(String s){
		return s != null && s.length() > 0;
	}
	
	public static int getDay(){
		Calendar cal = Calendar.getInstance();
		
		return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH)+1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
	}
}
