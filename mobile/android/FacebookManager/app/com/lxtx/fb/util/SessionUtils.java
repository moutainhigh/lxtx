package com.lxtx.fb.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.lxtx.fb.pojo.CsUser;


public class SessionUtils {

	/**
	 * 保存当前登录用户信息到session中
	 * @param request
	 * @param user
	 */
	public static void setLoginUserToSession(HttpServletRequest request, CsUser csUser){
		HttpSession session = request.getSession();
		if(csUser == null){
			return;
		}
		session.setAttribute("loginUser", csUser);
	}
	
	public static CsUser getLoginUserFromSession(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session == null){
			return null;
		}
		return (CsUser) session.getAttribute("loginUser");
	}
}
