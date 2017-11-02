package com.jxt.pay.appclient.utils;

public class SingleXmlUtil {

	public static String getNodeValue(String src,String node){
		if(src != null && src.length() > 0){
			String starts = "<"+node+">";
			String ends = "</"+node+">";
			
			int startPos = src.indexOf(starts);
			
			if(startPos >= 0){
				int endPos = src.indexOf(ends,startPos+1);
				
				if(endPos > 0){
					return src.substring(startPos+starts.length(),endPos);
				}
			}
		}
		
		return null;
	}
}
