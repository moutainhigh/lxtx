package com.jxt.pay.appclient.utils;

import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.net.DynamicServiceFactory;
import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;

public class DynamicUtils {

	public static String parseWait(){
		return parseWait(5);
	}
	
	public static String parseWait(int seconds){
		Repeat repeat = new Repeat();
		repeat.setStep(1);
		repeat.setSleep(seconds);
		return XstreamHelper.toXml(repeat).toString();
	}

	public static String parseWait(Map<String, String> map){
		return parseWait(5,map);
	}
	
	public static String parseWait(int seconds,Map<String, String> map){

		
		StringBuffer sb = new StringBuffer("<wait>").append(seconds).append("</wait>");
		
		sb.append("<gets><url>\"").append(map.get(DynamicServiceFactory.ACTIONURL)).append("\"</url>");
		sb.append("<params>");
		
		int i = 0 ; 
		for(String key : map.keySet()){
			if(!key.equals(DynamicServiceFactory.ACTIONURL) && !key.equals(Constants.IPPARAM)){
				if(i > 0){
					sb.append("&");
				}
				sb.append(key).append("=").append(map.get(key));
				i ++;
			}
		}
		
		sb.append("</params></gets>");
		
		return sb.toString();
	}
	
	public static String parseError(String status){
		
		Error error = new Error();
		error.setErrorCode(status);
		
		return XstreamHelper.toXml(error).toString();
	}
	
}
