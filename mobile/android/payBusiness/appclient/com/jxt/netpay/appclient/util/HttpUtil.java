package com.jxt.netpay.appclient.util;

import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

	public static String getUrl(String url,Map<String, String> map){
		
		StringBuffer sb = new StringBuffer(url);
		
		int pos = url.indexOf("?"); 
		
		if(pos < 0){
			sb.append("?");
		}
		
		StringBuffer sb1 = new StringBuffer();
		for(Entry<String,String> entry : map.entrySet()){
			sb1.append("&").append(entry.getKey()).append("=").append(entry.getValue());
		}
		
		if(pos < url.length() && pos > 0){
			sb.append(sb1);
		}else{
			sb.append(sb1.substring(1));
		}
		
		return sb.toString();
	}
	
	public static String getUrl(String url,String paramString){
		StringBuffer sb = new StringBuffer(url);
		
		int pos = url.indexOf("?");
		
		if(pos < 0){
			sb.append("?");
		}else if(pos < url.length()){
			sb.append("&");
		}

		sb.append(paramString);
		
		return sb.toString();
	}
}
