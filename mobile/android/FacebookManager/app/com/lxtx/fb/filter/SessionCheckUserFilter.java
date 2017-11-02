package com.lxtx.fb.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lxtx.fb.pojo.CsUser;
import com.lxtx.fb.util.SessionUtils;

/**
 * Servlet Filter implementation class SessionCheckUserFilter
 */
public class SessionCheckUserFilter implements Filter {

	private List<String> list;
	private String userType;
	
	public void init(FilterConfig fConfig) throws ServletException {
		list = new ArrayList<String>();
		list.add("/login.html");
		list.add("/user!login.do");
		
		userType = fConfig.getInitParameter("userType");
	}
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		String path = request.getServletPath();
		if(list.contains(path)){
			chain.doFilter(request, response);
			return;
		}
		
		CsUser loginUser = SessionUtils.getLoginUserFromSession(request);
		
		if(loginUser!=null){
			chain.doFilter(request, response);
		}else{
			String url =request.getServerName()+":"+request.getServerPort()+request.getContextPath();
			
			response.sendRedirect("http://"+url+"/login.html");
			return;
		}
	}

	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {

	}
}
