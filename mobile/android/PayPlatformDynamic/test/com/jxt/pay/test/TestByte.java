package com.jxt.pay.test;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class TestByte {

	public static void main(String[] args){
//		String s = "E1";//5A8CAABC414131E213E97C324134A5AC06534EA136D80C5934E68638635326734AA7D369B059BD3DA739612D767FAED7F3A9D09C264B67775BF31E47DAEAC77BF04E733A5FD8BA134FCDA3EF73E89B6695C29F62D96B56863EEFC2A4EC2C63CEEF4D62165F7522C5CF68AF872EB3D9EFFB5BCE06";
//		
//		byte[] bytes = getByteContent(s);
//		
//		try {
//			s = new String(bytes,"utf-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println(s);
		
		Properties prop = new Properties();

		javax.naming.AuthenticationException e = null;
	}
	
	
	
	public static byte[] getByteContent(String sendcontent){
	    byte[] v0_1;
	    if(sendcontent == null || (sendcontent.equals(""))) {
	        v0_1 = null;
	    }else {
	        String v0 = sendcontent.toUpperCase();
	        int v2 = v0.length() / 2;
	        char[] v3 = v0.toCharArray();
	        v0_1 = new byte[v2];
	        int v1;
	        for(v1 = 0; v1 < v2; ++v1) {
	            int v4 = v1 * 2;
	            v0_1[v1] = ((byte)(checkPosion(v3[v4 + 1]) | checkPosion(v3[v4]) << 4));
	        }
	    }
	    return v0_1;
	}

	public static byte checkPosion(char arg1) { 
	    return ((byte)"0123456789ABCDEF".indexOf(arg1)); 
	}

	
}
