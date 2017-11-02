package com.lxtx.util.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.lxtx.util.SystemSwitchConfig;
import com.lxtx.util.tencent.WXPayConfig;

/**
 * 检查是否已经登录
 *
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String sessionVersion = (String) request.getSession().getAttribute("version");
		if (StringUtils.isEmpty(sessionVersion)) {
			String time = WXPayConfig.getInstance().get("version");
			request.getSession().setAttribute("version", time);
		}

		request.getSession().setAttribute("timetip", System.currentTimeMillis()+"");
		
		// 客户端是否是微信浏览器校验
		// String ua = ((HttpServletRequest)
		// request).getHeader("user-agent").toLowerCase();
		// if (ua.indexOf("micromessenger") > 0) {// 是微信浏览器
		// } else {
		// response.sendRedirect(request.getContextPath() + "/error/999.jsp");
		// return false;
		// }
		String systemFlag = SystemSwitchConfig.getInstance().getSysSwitch().toLowerCase();
		if ("on".equals(systemFlag)) {
			return true;
		} else {
			response.sendRedirect(request.getContextPath() + "/maintainace");
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}
}