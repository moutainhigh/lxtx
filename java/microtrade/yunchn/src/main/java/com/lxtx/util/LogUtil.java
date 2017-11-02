package com.lxtx.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.model.CUser;

/**
 * @author hecm
 * 
 */
public class LogUtil {
	public static final Logger log = LoggerFactory.getLogger(LogUtil.class);

	public static void loginfo(HttpServletRequest request, String method, Object obj) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		StringBuffer sb = new StringBuffer().append(cuser.getuCode()).append("\t").append(StringUtil.formatDate(new Date()))
				.append("\t").append("method:" + method).append("\t");

		if (null != obj) {
			if (obj.getClass().equals(ArrayList.class)) {
				List<Object> objs = (List<Object>) obj;
				for (Object o : objs) {
					sb.append("Object infos are:");
					sb.append("\r" + o.toString());
				}
			}
		}
		log.info(sb.toString());
	}
}
