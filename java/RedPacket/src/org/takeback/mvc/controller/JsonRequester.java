package org.takeback.mvc.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;
import org.takeback.core.user.AccountCenter;
import org.takeback.util.ApplicationContextHolder;
import org.takeback.util.ReflectUtil;
import org.takeback.util.context.Context;
import org.takeback.util.context.ContextUtils;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exception.CodedBaseRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.takeback.mvc.ResponseUtils.createBody;

@RestController("jsonRequester")
public class JsonRequester {

	private static final Logger log = LoggerFactory.getLogger(JsonRequester.class);

	private static final String SERVICE = "service";
	private static final String METHOD = "method";
	private static final String PARAMETERS = "parameters";

	@RequestMapping(value = "/**/*.jsonRequest", method = RequestMethod.POST, headers = "content-type=application/json")
	public Map<String, Object> handle(@RequestBody Map<String, Object> request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		String service = (String) request.get(SERVICE);
		String method = (String) request.get(METHOD);
		if (StringUtils.isEmpty(service) || StringUtils.isEmpty(method)) {
			return createBody(400, "missing service or method.");
		}
		if (!ApplicationContextHolder.containBean(service)) {
			return createBody(401, "service is not defined in spring.");
		}
		String uid = (String) WebUtils.getSessionAttribute(httpServletRequest, Context.UID);
		Long urt = (Long) WebUtils.getSessionAttribute(httpServletRequest, Context.USER_ROLE_TOKEN);
		if(uid == null || urt == null){
			return createBody(403, "notLogon");
		}
		ContextUtils.put(Context.USER_ROLE_TOKEN, AccountCenter.getUser(uid).getUserRoleToken(urt));
		ContextUtils.put(Context.HTTP_REQUEST, httpServletRequest);
		ContextUtils.put(Context.HTTP_RESPONSE, httpServletResponse);
		Object s = ApplicationContextHolder.getBean(service);
		Object b = request.get(PARAMETERS);
		Object[] parameters = null;
		if (b != null) {
			if (b instanceof List<?>) {
				List<?> ps = (List<?>) b;
				int len = ps.size();
				parameters = new Object[len];
				for (int i = 0; i < len; i++) {
					parameters[i] = ps.get(i);
				}
			} else {
				parameters = new Object[1];
				parameters[0] = b;
			}
		}
		Method m = null;
		Object r = null;
		try {
			if (parameters == null) {
				m = s.getClass().getDeclaredMethod(method);
				r = m.invoke(s);
			} else {
				Method[] ms = s.getClass().getDeclaredMethods();
				for (Method mm : ms) {
					if(!method.equals(mm.getName())){
						continue;
					}
					if (isCompatible(mm, parameters)) {
						m = mm;
						break;
					}
				}
				r = m.invoke(s, convertToParameters(m.getParameterTypes(), parameters));
			}
			return createBody(r);
		} catch (CodedBaseRuntimeException e){
			log.error("execute service[" + service + "] with " + method + " failed.", e);
			return createBody(e.getCode(), e.getMessage());
		} catch (Exception e) {
			log.error("execute service[" + service + "] with " + method + " failed.", e);
			if(e.getCause() != null){
				return createBody(402, e.getCause().getMessage());
			}
			return createBody(402, "execute service[" + service + "] with " + method + " failed.");
		} finally {
			ContextUtils.clear();
		}
	}

	private boolean isCompatible(Method m, Object[] parameters) {
		boolean r = true;
		Class<?>[] cls = m.getParameterTypes();
		if (parameters == null) {
			if (cls.length != 0) {
				r = false;
			}
		} else {
			if (parameters.length != cls.length) {
				r = false;
			} else {
				for (int i = 0; i < cls.length; i++) {
					if (!ReflectUtil.isCompatible(cls[i], parameters[i])) {
						r = false;
						break;
					}
				}
			}
		}
		return r;
	}

	private Object[] convertToParameters(Class<?>[] parameterTypes, Object[] args) {
		Object[] parameters = new Object[parameterTypes.length];
		int i = 0;
		for (Class<?> type : parameterTypes) {
			parameters[i] = ConversionUtils.convert(args[i], type);
			i++;
		}
		return parameters;
	}

	public String a() {
		return "a";
	}

	public int a(int a, String b) {
		return a;
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Method m = JsonRequester.class.getDeclaredMethod("a");
		System.out.println(m.getParameterTypes().length);
		// System.out.println(ReflectUtil.getMethodParameterNames(m)[0]);

	}

}
