package com.lxtx.util.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Strings;
import com.lxtx.util.IntegrationConfig;

public class JumpInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(JumpInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//TODO
		String url = request.getRequestURL().toString();
		String entryHost = IntegrationConfig.getPlazaEntryHost();
		String bodyHost = IntegrationConfig.getPlazaBodyHost();
		//如果配置没有准备好，就不执行
		if (Strings.isNullOrEmpty(entryHost) || Strings.isNullOrEmpty(bodyHost)) {
			return true;
		}
		
		if (url.contains(entryHost)) {//need to redirect
			String requestUrl = request.getRequestURL().toString();
			logger.info("now redirect should work!");
			String newurl = requestUrl.replace(entryHost, bodyHost);
			response.sendRedirect(newurl);
			return false;
		}
		
		return true;
	}
}
