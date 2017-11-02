package com.lxtx.util.net;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.util.SystemSwitchConfig;

public class CookieManager {
	private static final Logger logger = LoggerFactory.getLogger(CookieManager.class);
	
	public static void writeCookie(String key, String value, int expire, HttpServletResponse response) {
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		cookie.setMaxAge(expire);
		String domain = SystemSwitchConfig.getInstance().get("system.domain");
		cookie.setDomain(domain);
		logger.info("write cookie: " + key + " " + value + "  " + expire+ " " + domain);
		response.addCookie(cookie);
	}
	
	public static String getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookieList = request.getCookies();
		for (Cookie c: cookieList) {
			if (c.getName().equals(cookieName)) {
				logger.info("get cookie "  + cookieName + ":" + c.getValue());
				return c.getValue();
			}
		}
		return "";
	}
}
