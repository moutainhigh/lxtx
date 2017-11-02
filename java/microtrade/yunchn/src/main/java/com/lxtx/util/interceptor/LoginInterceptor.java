package com.lxtx.util.interceptor;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.lxtx.util.AjaxJson;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;


/**
 * 检查是否已经登录
 *
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {

            HttpSession session = request.getSession();
            String reqUri = request.getRequestURI();
            if (null == session.getAttribute(Constant.SESSION_USER)) {
                if (reqUri.endsWith(".json")) {
                    String requestType = request.getHeader("X-Requested-With");
                    if ("XMLHttpRequest".equalsIgnoreCase(requestType)) {
                        response.setHeader("sessionstatus", "timeout");
                        AjaxJson json = new AjaxJson();
                        json.setCode(ErrorCode.SESSION_LOST);
                        OutputStream os = response.getOutputStream();
                        JSONObject j = JSONObject.fromObject(json);
                        os.write(j.toString().getBytes("UTF-8"));
                        os.close();
                    }
                    return false;
                }

                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }



    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {

    }
}
