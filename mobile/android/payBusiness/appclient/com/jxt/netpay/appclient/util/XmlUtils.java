package com.jxt.netpay.appclient.util;

import java.util.Map;

public class XmlUtils {

	public static String getNodeValue(String xml,String node){
		
		String prefix = "<"+node+">";
		String postfix = "</"+node+">";
		
		int pos0 = xml.indexOf(prefix);
		int pos1 = xml.indexOf(postfix,pos0);
		
		if(pos0 >= 0 && pos1 >= 0){
			String ret = xml.substring(pos0+prefix.length(),pos1);
			
			if(ret.startsWith("<![CDATA[") && ret.endsWith("]]>")){
				ret = ret.substring("<![CDATA[".length(),ret.length() - "]]>".length());
			}
			
			return ret;
		}
		
		return "";
	}
	
	public static String toXml(Map<String,String> map){
		if(map != null && map.size() > 0){
			StringBuffer sb = new StringBuffer();
		
			sb.append("<xml>");
			
			for(String key : map.keySet()){
				sb.append("<").append(key).append(">");
				sb.append("<![CDATA[").append(map.get(key)).append("]]>");
				sb.append("</").append(key).append(">");
			}
			
			sb.append("</xml>");
			
			return sb.toString();
		}
		
		return "";
	}
	
}
