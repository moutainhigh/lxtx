package com.jxt.pay.appclient.utils;

import java.util.Random;

public class StringUtils {

	/**
	 * 判断a,b是否有交集
	 * @param a **,**,**
	 * @param b **|**|**
	 * @return
	 */
	public static boolean cross(String a,String b){
		
//		System.out.println("cross : "+a+" ; "+b);
		
		if(a != null && b != null && a.length() > 0 && b.length() > 0){
			String[] a1 = a.split(",");
			b = "|"+b+"|";
			
			for(String _a : a1){
				if(b.contains("|"+_a+"|")){
					return true;
				}
			}
		}
		
		return false;
	}
	

	public static String defaultString(String s){
		if(s == null){
			s = "";
		}
		
		return s;
	}
	
	private static final String[] arr = new String[]{"0","1","2","3","4","5","6","7","8","9"};

	public static String getRandom(int length){
		String random = "";
		
		for(int i = 0 ; i < length ; i ++){
			random += arr[new Random().nextInt(arr.length)];
		}
		
		return random;
	}
	
}
