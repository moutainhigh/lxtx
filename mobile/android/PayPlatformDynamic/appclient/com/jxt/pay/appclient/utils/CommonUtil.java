package com.jxt.pay.appclient.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CommonUtil {

	private static final BASE64Decoder decoder = new BASE64Decoder();
	private static final BASE64Encoder encoder = new BASE64Encoder();
	
	public static String base64Decode(String s) throws UnsupportedEncodingException,IOException{
		return new String(decoder.decodeBuffer(s),"utf-8");
	}
	
	public static byte[] base64DecodeBytes(String s) throws IOException{
		return decoder.decodeBuffer(s);
	}
	
	public static String base64Encode(String s){
		try {
			return encoder.encodeBuffer(s.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String getRemortIP(HttpServletRequest request) { 
	    if (request.getHeader("x-forwarded-for") == null) { 
	        return request.getRemoteAddr(); 
	    } 
	    return request.getHeader("x-forwarded-for"); 
	}   
	
	public static String getUA(HttpServletRequest request){
		
		
		return "";
	}
	
	public static Properties getProp(String s){
		Properties prop = new Properties();
		
		try {
			prop.load(new ByteArrayInputStream(s.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return prop;
	}
	
	public static void main(String[] args){
		
		try {
//			System.out.println("ret:"+base64Decode("WlAqKioqKjFYMyoqKjEqKioqKiowKioqKioqKioqKioqaTVRVU9xUEtQTUE9CkJlZHVxUUYwVjdvLzZwclVrYm9TZmc9PQoxTzI0R1lQNzc2OTA2RkE3QjkyNkVGMTBCNkZDQzBEM0FEMDk4MjMwMTIzNDU2Nzg5YWJjZGVm"));
//			System.out.println(base64Decode("eyJzdGF0dXMiOiI5MDAifQ=="));
			
			String s = "YlxMi00eQTAQTSmLMZXVIc2JUx6VMaZtKrzNcMkjsRlsVnHFajU5jMnXRmy6vWxqxfo1U05+x/HsWryZn+bwwfvbngsfbbLlUqqXIfPkPFX8WsdpMmo5ls5h5+N7kxiUtefqePne6zWbtVvuFrPlYLZbLXaLAQ==";
			
			byte[] bytes = base64DecodeBytes(s);
			for(byte b : bytes){
				System.out.print(b+",");
			}
//			String ss = new String(bytes,"utf-8");
//			System.out.println(ss);
//			byte[] bytes1 = ss.getBytes("utf-8");
//			for(byte b : bytes1){
//				System.out.print(b+",");
//			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
