// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.interceptor;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.takeback.util.JSONUtils;
import com.google.common.collect.ImmutableMap;
import java.util.Date;
import java.io.Serializable;
import org.takeback.chat.store.user.User;
import org.takeback.util.annotation.AuthPassport;
import org.springframework.web.method.HandlerMethod;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.user.UserStore;
import org.slf4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuthInterceptor extends HandlerInterceptorAdapter
{
    private static final Logger LOGGER;
    @Autowired
    private UserStore userStore;
    
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            return true;
        }
        final AuthPassport authPassport = (AuthPassport)((HandlerMethod)handler).getMethodAnnotation((Class)AuthPassport.class);
        if (authPassport == null || !authPassport.value()) {
            return true;
        }
        final String uid = request.getHeader("x-access-uid");
        if (uid == null || uid.equals("null")) {
            this.writeAuthorizeFailResponse(response);
            AuthInterceptor.LOGGER.error("No x-access-uid found, failed to authorize.");
            return false;
        }
        final User user = this.userStore.get(Integer.valueOf(uid));
        if (user == null) {
            this.writeAuthorizeFailResponse(response);
            AuthInterceptor.LOGGER.error("User not found with id: {}, failed to authorize.", (Object)uid);
            return false;
        }
        final String token = request.getHeader("x-access-token");
        if (token == null) {
            this.writeAuthorizeFailResponse(response);
            AuthInterceptor.LOGGER.error("No x-access-token found, failed to authorize.");
            return false;
        }
        if (!token.equals(user.getAccessToken())) {
            this.writeAuthorizeFailResponse(response);
            AuthInterceptor.LOGGER.error("Token is not validate, failed to authorize.");
            return false;
        }
        if (user.getTokenExpireTime().compareTo(new Date()) >= 0) {
            if (request.getSession(true).getAttribute("$uid") == null) {
                request.getSession().setAttribute("$uid", (Object)user.getId());
            }
            return true;
        }
        this.writeAuthorizeFailResponse(response);
        return false;
    }
    
    private void writeAuthorizeFailResponse(final HttpServletResponse response) throws IOException {
        final String json = JSONUtils.toString(ImmutableMap.of((Object)"code", (Object)401, (Object)"msg", (Object)"\u8bf7\u767b\u5f55\u8d26\u53f7\u3002"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AuthInterceptor.class);
    }
}
