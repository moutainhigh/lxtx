package com.lxtech.util.message;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lxtech.util.Md5Util;
import com.lxtech.util.http.HttpRequest;

public class SMSender {

	public static void sendValidationMsg(String mobile, String validCode) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String url = "https://api.miaodiyun.com/20150822/industrySMS/sendSMS";
		String account_sid = "5b9b19ecfb40499081ed5c2de907bda8";
//MD5(ACCOUNT SID + AUTH TOKEN + timestamp)
		String AUTH_TOKEN = "1249ba1d899946c5b70f0f93ced2c584";
		String ts = sdf2.format(new Date());
		String encodedString = Md5Util.MD5Encode(account_sid+AUTH_TOKEN+ts, "UTF-8");
		String param = "accountSid=%s&smsContent=【九州微云】您的验证码为{%s}，请尽快验证&to=%s&timestamp=%s&sig=%s&respDataType=JSON";		
		String content = HttpRequest.sendPost(url, String.format(param, account_sid, validCode, mobile, ts, encodedString));
		System.out.println(content);
	}
	
	public static void main(String[] args) {
		sendValidationMsg("18310135821", "123456");
	}
}
