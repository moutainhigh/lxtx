package com.lxtech.wx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.lxtech.http.HttpRequest;
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
			content = HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token,
					menu);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void generateMajiangMenu(String coreAppId, String appId, String appSecret, String domain,
			String appName, String downurl, long gameid, String manageurl) {
		String url = "https://api.weixin.qq.com/cgi-bin/token";
		String param = "appid=" + appId + "&secret=" + appSecret + "&grant_type=client_credential";
		String content = HttpRequest.sendGet(url, param);

		Map map = (Map) JsonUtil.convertStringToObject(content);
		String access_token = (String) map.get("access_token");

		System.out.println(access_token);

		try {
			String menu = getMenuContent("menu_majiang.json");
			// 0524修改
			// menu = String.format(menu, coreAppId, domain, appName,
			// coreAppId, domain, gameid, coreAppId, domain, appName, downurl);
			menu = String.format(menu, domain, appName, domain, gameid, domain, appName, downurl, manageurl);
			System.out.println(menu);
			content = HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token,
					menu);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void generateGameMenu(String appId, String appSecret, String chnno) {
		String url = "https://api.weixin.qq.com/cgi-bin/token";
		String param = "appid=" + appId + "&secret=" + appSecret + "&grant_type=client_credential";
		String content = HttpRequest.sendGet(url, param);

		Map map = (Map) JsonUtil.convertStringToObject(content);
		String access_token = (String) map.get("access_token");

		System.out.println(access_token);

		try {
			String menu = getMenuContent("menu_game.json");
			menu = String.format(menu, chnno, chnno, chnno);
			System.out.println(menu);
			content = HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token,
					menu);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getMenuContent() throws IOException {
		InputStream is = WeixinMenuTest.class.getResourceAsStream("/menu3.json");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder("");
		String line = "";
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		return sb.toString();
	}

	private static String getMenuContent(String configFile) throws IOException {
		InputStream is = WeixinMenuTest.class.getResourceAsStream("/" + configFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder("");
		String line = "";
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		return sb.toString();
	}

	/**
	 * 没有活动页
	 * 
	 * @param coreAppId
	 * @param appId
	 * @param appSecret
	 * @param domain
	 * @param appName
	 * @param downurl
	 * @param gameid
	 */
	public static void generateMajiangNoActivityMenu(String coreAppId, String appId, String appSecret, String domain,
			String appName, String downurl, long gameid, String manageurl) {
		String url = "https://api.weixin.qq.com/cgi-bin/token";
		String param = "appid=" + appId + "&secret=" + appSecret + "&grant_type=client_credential";
		String content = HttpRequest.sendGet(url, param);

		Map map = (Map) JsonUtil.convertStringToObject(content);
		String access_token = (String) map.get("access_token");

		System.out.println(access_token);

		try {
			String menu = getMenuContent("menu_majiang_no_activity.json");
			menu = String.format(menu, domain, appName, downurl, manageurl);
			System.out.println(menu);
			content = HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token,
					menu);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void generateMajiangHasActivityAndNoRedMenu(String coreAppId, String appId, String appSecret,
			String domain, String appName, String downurl, long gameid, String manageurl) {
		String url = "https://api.weixin.qq.com/cgi-bin/token";
		String param = "appid=" + appId + "&secret=" + appSecret + "&grant_type=client_credential";
		String content = HttpRequest.sendGet(url, param);

		Map map = (Map) JsonUtil.convertStringToObject(content);
		String access_token = (String) map.get("access_token");

		System.out.println(access_token);

		try {
			String menu = getMenuContent("menu_majiang_has_activity_no_red.json");
			menu = String.format(menu, domain, appName, domain, gameid, downurl, manageurl);
			System.out.println(menu);
			content = HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token,
					menu);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
