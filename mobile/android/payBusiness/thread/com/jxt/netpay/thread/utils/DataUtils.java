package com.jxt.netpay.thread.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.springframework.util.StringUtils;

/**
 * 
 * @author leoliu
 *
 */
public class DataUtils {

	/**
	 * 把字符串 true/false转为boolean
	 * @param str
	 * @return
	 */
	public static boolean strToBoolean(String str){
		str = str.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "").trim();
		if(StringUtils.hasText(str)){
			try{
				return new Boolean(str).booleanValue();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 内容解析
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
