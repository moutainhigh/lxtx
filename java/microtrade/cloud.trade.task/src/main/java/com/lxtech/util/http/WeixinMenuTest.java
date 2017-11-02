package com.lxtech.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.lxtech.util.JsonUtil;

public class WeixinMenuTest {

	public static void generateMenu(String appId, String appSecret, String appName, String chnno, String domain) {
		String url = "https://api.weixin.qq.com/cgi-bin/token";
		String param = "appid=" + appId + "&secret=" + appSecret + "&grant_type=client_credential";
		String content = HttpRequest.sendGet(url, param);
		
		Map map = (Map) JsonUtil.convertStringToObject(content);
		String access_token = (String) map.get("access_token");
		
		System.out.println(access_token);
		
		try {
			String menu = getMenuContent();
			menu = String.format(menu, domain, appName, chnno);
			System.out.println(menu);
			content = HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+access_token, menu);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getMenuContent() throws IOException {
		InputStream is = WeixinMenuTest.class.getResourceAsStream("/menu.json");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder("");
		String line = "";
		while ((line = br.readLine()) != null) {  
			sb.append(line);
		}
		
		return sb.toString();
	}
}
