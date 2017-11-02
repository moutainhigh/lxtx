package com.baidu.alipay.script.sdk;

import java.util.Calendar;

public class SdkUtils {

	public static String getNodeValue(String str,String nodeName){
		try {
            String s1 = "<" + nodeName + ">";
            String s2 = "</" + nodeName + ">";
            int start = str.indexOf(s1);
            int end = str.indexOf(s2);
            return str.substring(start + s1.length(), end);
        } catch (Exception ex) {
            return "";
        }
	}
	
	public static String getDay(){
		Calendar cal = Calendar.getInstance();
		
		int day = cal.get(Calendar.YEAR)*10000 + (cal.get(Calendar.MONTH)+1)*100 + cal.get(Calendar.DAY_OF_MONTH);
				
		return day + "";
	}
	
}
