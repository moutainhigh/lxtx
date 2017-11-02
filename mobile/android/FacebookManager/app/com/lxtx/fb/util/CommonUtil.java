package com.lxtx.fb.util;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

public class CommonUtil {

	public static String getServerUrl(HttpServletRequest request){
		
		String serverUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
		
		return serverUrl;
		
	}

	public static int getDay(int off){
		
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DAY_OF_MONTH, off);
		
		return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
	}
	
}
