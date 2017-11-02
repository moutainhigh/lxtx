package com.lxtx.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmsSendUtil {
	public static String sendMsg(String mobile,String validCode){
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobiles", mobile);
		map.put("content", "【微云九州】尊敬的用户：您好，您的本次验证码是"+validCode+"，请不要告诉任何人，感谢使用。");
		map.put("secret", "beijing");
		map.put("time", ""+new Date().getTime());
		String res = HttpClient.post("http://m.ninestate.com.cn/index.php/mobile/async/sendSMSMsg", map);
		res = res.replaceAll("\"", "");
		res = res.substring(2, res.length()-1);
		String[] s  = res.split(",");
		return s[0];
	}
}
