package com.neo.logic;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class WebTool {

	public static final String SESSION_USER = "session_user";
	
	public static String getReqValue(HttpServletRequest request, String parameter) { 
		return request.getParameter(parameter);
	}
	
	public static Object getSessionValue(HttpServletRequest request, String attrName) {
		return request.getSession().getAttribute(attrName);
	}
	
	public static void setSessionValue(HttpServletRequest request, String name, Object value) {
		request.getSession().setAttribute(name, value);
	}
	
	public static Long getDayNum(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return Long.valueOf(sdf.format(date));
	}
	
}
