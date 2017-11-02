package com.lxtech.util.wx;

import com.lxtech.util.http.HttpRequest;

public class UnionIdTest {
	public static void main(String[] args) {
		//https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
//		String appId = "wxb0ee2119ffabb89e";
//		String appSecret = "271e0c6dbbe3fe77447d6a6a18ae76d5";
//		String credentials = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token",
//				"grant_type=client_credential&appid=" + appId + "&secret=" + appSecret);
//		System.out.println(credentials);
		
		//https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
		String response = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/user/info",
				"access_token=T1Ztfux-4gl0gf11W7cTHjFV-OK-MJji1lrrbvSRId-gJGEm2QunHWzaDKzvB5WbgZXRMnd7aVVM-4Dy9YP1J3_JB36eYi9fCo9hQdcMQeYCIZjADALRI&openid=opPADwD8S5k9nanXrkvgLf4jXSss&lang=zh_CN");
		System.out.println(response);
	}
}