package com.lxtx.fb.test;

import java.io.File;
import java.util.Random;

public class Test1 {

	public static void main(String[] args){
//		File file = new File("C:\\Users\\thinkpad\\Desktop\\海外投放\\账号\\France\\谷歌浏览器\\suzanne.ourziq@gmail.com.txt");
//		
//		System.out.println(file.exists());
		
		System.out.println(getPageName("Football-Fans,Football-club"));
	}
	
	private static String getPageName(String name){
    	
    	String[] arr = name.split(",");
    	
    	if(arr.length == 1){
    		return name;
    	}else{
    		return arr[new Random().nextInt(arr.length)];
    	}
    }
	
}
