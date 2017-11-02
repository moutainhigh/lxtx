package com.neo.logic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println(">>>MyInterceptor2>>>>>>>在请求处理之前进行调用（Controller方法调用之前）");
        String uri = request.getRequestURI();
        if(uri.contains("/user")) {
        	return true;
        }
        
        if (WebTool.getSessionValue(request, Constants.SESSION_USER) == null) {
        	//return false;
        	response.sendRedirect(request.getContextPath() + "/user/showsignin");
        	return false;
        } else {
        	return true;// 只有返回true才会继续向下执行，返回false取消当前请求
        }
    }

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
	}
}