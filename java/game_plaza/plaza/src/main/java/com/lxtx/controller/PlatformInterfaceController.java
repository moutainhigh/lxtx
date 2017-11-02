package com.lxtx.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.utils.URLEncodedUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lxtx.model.AmountChangeHistory;
import com.lxtx.model.CloudUser;
import com.lxtx.service.user.UserService;
import com.lxtx.util.Constant;
import com.lxtx.util.IntegrationConfig;
import com.lxtx.util.Md5Util;
import com.lxtx.util.db.DomainHandler;

@Controller
public class PlatformInterfaceController {
	private static final Logger logger = LoggerFactory.getLogger(PlatformInterfaceController.class);
	private static final boolean __is_test__ = false;
	@Autowired
	private UserService userService;

	private String MD5_SECRET_KEY = "platform_ddz";
	private String REQUEST_MD5_SECRET_KEY = "Jiumu!@#";

	@RequestMapping(value = "/platform/interface")
	@ResponseBody
	public Object platformInterface(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/xml;charset=UTF-8");
		String action = (String) request.getParameter("action");
		if (action.equals("playerinfo")) {// 获取用户信息
			return getPlayerInfo(request);
		} else if (action.equals("amountchange")) {// 金币变化
			return amountChange(request);
		}
		return makeFailedReturnStr(-1, "error action");
	}

	@RequestMapping(value = "/platform/ddzLogin")
	public ModelAndView ddzLogin(HttpServletRequest request) {
		if (__is_test__) {
			String userIdStr = (String) request.getParameter("userid");
			int userId = Integer.parseInt(userIdStr.trim());
			CloudUser user = userService.selectUserById(userId);
			request.getSession().setAttribute(Constant.SESSION_USER, user);
		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/ddzhome");
		return mv;
	}

	@RequestMapping(value = "/platform/ddzLoginCheck")
	@ResponseBody
	public Object ddzLoginCheck(HttpServletRequest request) {
		try {
			Map<String, Object> map = new HashMap<>();
			CloudUser sessionUser = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
			if (sessionUser == null) {
				map.put("code", 110);
				map.put("Message", "session已经失效");
				return map;
			}
			int userId = sessionUser.getId();
			HttpClient client = new HttpClient();
			client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			client.getParams().setParameter("http.protocol.single-cookie-header", true);

			String action = "loginWithChannel";
			String channel = "10";
			String verifyCode = Md5Util.MD5Encode(userId + action + channel + REQUEST_MD5_SECRET_KEY, "utf-8")
					.toUpperCase();
			String path = "http://47.93.85.160:803/interface.aspx?username=" + userId + "&action=" + action
					+ "&channel=" + channel + "&verifyCode=" + verifyCode;
			HttpMethod httpMethod = new PostMethod(path);
			httpMethod.addRequestHeader("Accept-Encoding", "*");
			client.executeMethod(httpMethod);

			InputStream in = httpMethod.getResponseBodyAsStream();
			String content = readFromInputStream(in);
			logger.info(content);
			// SAXReader saxReader = new SAXReader();
			content = content.replaceAll("&", "###");
			Document document = DocumentHelper.parseText(content);// saxReader.read(in);
			Element root = document.getRootElement();
			Element errCodeElement = root.element("errcode");
			int errCode = Integer.parseInt(errCodeElement.getTextTrim());
			if (errCode != 0) {
				Element errTextElement = root.element("errtext");
				map.put("code", errCode);
				map.put("Message", errTextElement.getText());
				return map;
			} else {
				Element result = root.element("result");
				map.put("code", 0);
				map.put("ddzpath", result.getText().replaceAll("###", "&"));
				logger.info("" + result.getText());
			}
			return map;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	@RequestMapping(value = "/platform/redpacket")
	@ResponseBody
	public void redPacketLogin(HttpServletRequest request, HttpServletResponse response) {
		try {
			int userId;
			if (__is_test__) {
				userId = 1;
			} else {
				CloudUser sessionUser = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
				if (sessionUser == null) {
					response.sendRedirect(request.getContextPath() + "/999.jsp");
					return;
				}
				userId = sessionUser.getId();
			}
			CloudUser user = userService.selectUserById(userId);
			String config = request.getParameter("config");
			if (config == null) {
				config = "";
			}
			String wxid = user.getWxid();
			int gameUserId = user.getId();
			double balance = user.getCarryAmount() / 10000.0;
			String token = Md5Util.MD5Encode(wxid + "game", "utf-8").toLowerCase();
			//String host = IntegrationConfig.getRedpacketHost();
			String host = DomainHandler.getRedPacketActiveDomain();
			if (Strings.isNullOrEmpty(host)) {
				host = IntegrationConfig.getRedpacketHost();
			}
 			String nickname = URLEncoder.encode(user.getWxnm(), "UTF-8");
			logger.warn("redirect to redpacket, nickname is:" + nickname);
			String path = "http://" + host + "/accessfromgame?wxid=" + wxid + "&token=" + token
					+ "&gameUserId=" + gameUserId + "&test=1&balance=" + balance + "&nickname=" + nickname + "&chnno="+user.getChnno();
			if (!Strings.isNullOrEmpty(config)) {
				path += "&config=" + config;
			}
			response.sendRedirect(path);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private String readFromInputStream(InputStream in) {
		String result = null;
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "utf8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + '\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		result = sb.toString();
		return result;
	}

	public Object getPlayerInfo(HttpServletRequest request) {
		String userIdStr = (String) request.getParameter("userid");
		String action = (String) request.getParameter("action");
		String timestamp = (String) request.getParameter("timestamp");
		String verifyCode = (String) request.getParameter("verifycode");

		String token = Md5Util.MD5Encode(userIdStr + action + timestamp + MD5_SECRET_KEY, "utf-8").toLowerCase();
		if (!__is_test__ && !token.equals(verifyCode.toLowerCase())) {
			return makeFailedReturnStr(-2, "error verifycode");
		}
		Map<String, Object> values = new HashMap<>();
		int userId = Integer.parseInt(userIdStr.trim());
		CloudUser user = userService.selectUserById(userId);
		values.put("userid", user.getId());
		values.put("wxnm", user.getWxnm());
		values.put("headimgurl", user.getHeadimgurl());
		values.put("chnno", user.getChnno());
		values.put("crt_tm", user.getCrtTm().getTime());
		values.put("carry_amount", user.getCarryAmount());
		return makeSuccessReturnStr(values);
	}

	public Object amountChange(HttpServletRequest request) {
		String userIdStr = (String) request.getParameter("userid");
		String action = (String) request.getParameter("action");
		String amountStr = (String) request.getParameter("amount");
		String timestamp = (String) request.getParameter("timestamp");
		String verifyCode = (String) request.getParameter("verifycode");

		String token = Md5Util.MD5Encode(userIdStr + action + amountStr + timestamp + MD5_SECRET_KEY, "utf-8")
				.toLowerCase();
		if (!__is_test__ && !token.equals(verifyCode.toLowerCase())) {
			return makeFailedReturnStr(-2, "error verifycode");
		}
		int userId = Integer.parseInt(userIdStr.trim());
		int amount = Integer.parseInt(amountStr.trim());
		if (amount == 0) {
			return makeFailedReturnStr(1, "error amount");
		}
		Map<String, Object> values = new HashMap<>();
		CloudUser user = userService.selectUserById(userId);
		if (user.getCarryAmount() + amount >= 0) {
			userService.reduceCoinById(-amount, userId);
			AmountChangeHistory history = new AmountChangeHistory();
			history.setUser_id(userId);
			history.setAmount_change(amount);
			history.setPlatform("ddz");
			history.setOperator_time(new Date());
			userService.insertAmountChangeHistory(history);
			values.put("userid", user.getId());
			values.put("carry_amount", user.getCarryAmount() + amount);
			return makeSuccessReturnStr(values);
		} else {
			return makeFailedReturnStr(1, "carray amount not enough");
		}
	}

	private String makeFailedReturnStr(int code, String msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml version=\"1.0\" encoding=\"utf-8\">");
		sb.append("<response>");
		sb.append("<errcode>");
		sb.append(code);
		sb.append("</errcode>");
		sb.append("<errtext>");
		sb.append(msg);
		sb.append("</errtext>");
		sb.append("</response>");
		sb.append("</xml>");
		return sb.toString();
	}

	private String makeSuccessReturnStr(Map<String, Object> values) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml version=\"1.0\" encoding=\"utf-8\">");
		sb.append("<response>");
		sb.append("<errcode>0</errcode>");
		sb.append("<errtext>success</errtext>");
		for (Entry<String, Object> entry : values.entrySet()) {
			sb.append("<");
			sb.append(entry.getKey());
			sb.append(">");
			sb.append(entry.getValue());
			sb.append("</");
			sb.append(entry.getKey());
			sb.append(">");
		}
		sb.append("</response>");
		sb.append("</xml>");
		String str = sb.toString();
		return str;
	}
}
