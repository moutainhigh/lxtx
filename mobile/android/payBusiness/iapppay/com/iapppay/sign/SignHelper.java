package com.iapppay.sign;

import org.apache.log4j.Logger;

public class SignHelper
{
	private static Logger logger = Logger.getLogger(SignHelper.class);
	
	// 字符编码格式 ，目前支持  utf-8
	public static String input_charset = "utf-8";
	
	public static boolean verify(String content, String sign, String pubKey)
	{
		// 目前版本，只支持RSA
		boolean ret = RSA.verify(content, sign, pubKey, input_charset);
		
		logger.error("verify:"+content+";"+sign+";"+pubKey+"-->"+ret);
		
		return ret;
	}
	
	public static String sign(String content, String privateKey)
	{
		String sign = RSA.sign(content, privateKey, input_charset);
		
		logger.error("sign : "+content+";"+privateKey+"->"+sign);
		
		return sign;
	}
	
	public static String md5(String s){
		
		return RSA.md5s(s);
		
	}
}
