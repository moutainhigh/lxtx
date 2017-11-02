package com.baidu.alipay.script.sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuardHelper {
	
	public static boolean filterSmsNoLeft(String smsno,String smsnoLeft){
		
		if(smsnoLeft == null || smsnoLeft.length() == 0){
			return true;
		}
		
		return smsno.startsWith(smsnoLeft);
	}
	
	public static boolean filterSmsNoRight(String smsno,String smsnoRight){
		
		if(smsnoRight == null || smsnoRight.length() == 0){
			return true;
		}
		
		return smsno.endsWith(smsnoRight);
	}
	
	public static boolean filterContent(String sms,String smsContent){
		if(smsContent == null || smsContent.length() == 0){ 
			return true;
		}
		
		Pattern pattern = Pattern.compile(smsContent,Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(sms);
		
		return matcher.find();
	}

}
