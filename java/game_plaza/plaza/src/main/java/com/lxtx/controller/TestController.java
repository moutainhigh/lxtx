package com.lxtx.controller;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lxtx.model.CloudChnCode;
import com.lxtx.model.CloudUser;
import com.lxtx.model.CloudWxServiceProvider;
import com.lxtx.model.GUserLogin;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.net.CookieManager;
import com.lxtx.util.net.KestrelConnector;
import com.lxtx.util.net.MessageConfig;
import com.lxtx.util.tencent.UserInfo;
import com.lxtx.util.tencent.WXPayConstants;
import com.lxtx.util.tool.JsonUtil;

@Controller
public class TestController {
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/home")
	public ModelAndView home(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		CloudUser user = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		mv.setViewName("home");
		mv.addObject("user", user);
		return mv;
	}

	@RequestMapping(value = "/skhome")
	public ModelAndView skhome(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("shake_home");
		return mv;
	}
	@RequestMapping(value = "/luxurycarhome")
	public ModelAndView luxurycarhome(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("luxurycar_home");
		return mv;
	}
	@RequestMapping(value = "/animalhome")
	public ModelAndView sxhome(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("animal_home");
		return mv;
	}

	@RequestMapping(value = "/")
	public String local(HttpServletRequest request) {
		return "redirect:index";
	}

	@RequestMapping(value = "/maintainace")
	public ModelAndView maintainace(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("maintainace");
		return mv;
	}

	@RequestMapping(value = "/testexception")
	public ModelAndView testexception(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("maintainace");
		int a = 6 / 0;
		return mv;
	}

	@RequestMapping(value = "/index")
	public ModelAndView ind(HttpServletRequest request, String wxid) {
		// TODO wxid 参数为测试代码
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession();
		UserInfo userInfo = (UserInfo) session.getAttribute(WXPayConstants.WX_USER_INFO);
		CloudUser clouduser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		if (null == clouduser) {
			if (Strings.isNullOrEmpty(wxid)) {
				wxid = "oDfCIw_VvuiEz9Ic2hTw8_p4Aets";
			}

			userInfo = new UserInfo();
			userInfo.setOpenid(wxid);
			clouduser = userService.selectByWxid(wxid);
			if (clouduser == null) {
				mv.setViewName("error/999");
				return mv;
			}
			userInfo.setOpenid(clouduser.getWxid());
			userInfo.setHeadimgurl(clouduser.getHeadimgurl());
			userInfo.setNickname(clouduser.getWxnm());
			session.setAttribute(WXPayConstants.WX_USER_INFO, userInfo);
			session.setAttribute(Constant.SESSION_USER, clouduser);
		}

		mv.addObject("user", clouduser);
		mv.setViewName("home");
		return mv;
	}

	@RequestMapping(value = "/guest")
	public ModelAndView showGuestPage(HttpServletRequest request) {
		logger.info("in show guest page.");
		CloudUser user = userService.selectByWxid("opPADwBmRX-6Uz9RsGCDs44Bb6Do");
		request.getSession().setAttribute(Constant.SESSION_USER, user);
		logger.info("in showguestpage, step1");
		// 发送登录消息到队列
		String queueName = MessageConfig.getInstance().get("activity.queue");
		if (Strings.isNullOrEmpty(queueName)) {
			queueName = "cloud_trade_activity";
		}
		logger.info("in showguestpage, step2");
		Map<String, String> map = ImmutableMap.of("user_id", user.getWxid(), "msg_type", "login");
		String message = JsonUtil.convertObjToStr(map);
		KestrelConnector.enqueue(queueName, message);
		logger.info("in showguestpage, step3");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("guesthome");
		return mv;
	}

	@RequestMapping(value = "/testSave")
	@ResponseBody
	public Object testSave() {
		logger.info("in test save.");
		UserInfo userInfo = new UserInfo();
		userInfo.setChnno("1050");
		userInfo.setHeadimgurl(
				"http://wx.qlogo.cn/mmopen/ajNVdqHZLLC1egRslJj4VFziaa3WMib0ZViaoavRYmAfdpiagqMD3iclnK6PbCe1bIcLCQw4VldaribQstnCfZAvQfEA/0");
		userInfo.setNickname("哇居然是邵峰邵千万啊");
		userInfo.setOpenid("oZCnuwmkKOOEPu47mmXmfuL6Pfxo");
		userInfo.setWxProviderId(1);
		userService.wxVisit(userInfo);
		return "123";
	}
	
	@RequestMapping(value = "/testEcho", produces="text/html;charset=UTF-8")
	@ResponseBody
	public Object testEcho(HttpServletRequest request, String wxid, HttpServletResponse response) {
		response.setContentType("application/xml;charset=UTF-8");
		return "中文";
	}

	@RequestMapping(value = "/shake_home")
	public ModelAndView shake_home(HttpServletRequest request, String wxid, HttpServletResponse response) {
		// TODO wxid 参数为测试代码
		ModelAndView mv = new ModelAndView();
		// 创建或新增cookie
		GUserLogin uLogin = new GUserLogin();
		uLogin.setCookie(UUID.randomUUID().toString());

		String baseUid = request.getParameter("baseuid");
		if (!Strings.isNullOrEmpty(baseUid)) {
			uLogin.setUid(Integer.valueOf(baseUid));
		} else {
			uLogin.setUid(2);
		}
		
		uLogin.setLoginTime(new Date());
		int uptCount = userService.updateGUserLogin(uLogin);
		if (uptCount == 0) {
			userService.insertGUserLogin(uLogin);
		}
		CloudUser u = userService.selectUserById(uLogin.getUid());
		request.getSession().setAttribute(Constant.SESSION_USER, u);
		CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);

		mv.setViewName("shake_home");
		return mv;
	}
	
	@RequestMapping(value = "/luxurycar_home")
	public ModelAndView luxurycar_home(HttpServletRequest request, String wxid, HttpServletResponse response) {
		// TODO wxid 参数为测试代码
		ModelAndView mv = new ModelAndView();
		// 创建或新增cookie
		GUserLogin uLogin = new GUserLogin();
		uLogin.setCookie(UUID.randomUUID().toString());
		
		String baseUid = request.getParameter("baseuid");
		if (!Strings.isNullOrEmpty(baseUid)) {
			uLogin.setUid(Integer.valueOf(baseUid));
		} else {
			uLogin.setUid(2);
		}
		
		uLogin.setLoginTime(new Date());
		int uptCount = userService.updateGUserLogin(uLogin);
		if (uptCount == 0) {
			userService.insertGUserLogin(uLogin);
		}
		CloudUser u = userService.selectUserById(uLogin.getUid());
		request.getSession().setAttribute(Constant.SESSION_USER, u);
		CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);
		
		mv.setViewName("luxurycar_home");
		return mv;
	}
	
	@RequestMapping(value = "/animal_home")
	public ModelAndView animal_home(HttpServletRequest request, String wxid, HttpServletResponse response) {
		// TODO wxid 参数为测试代码
		ModelAndView mv = new ModelAndView();
		// 创建或新增cookie
		GUserLogin uLogin = new GUserLogin();
		uLogin.setCookie(UUID.randomUUID().toString());
		
		String baseUid = request.getParameter("baseuid");
		if (!Strings.isNullOrEmpty(baseUid)) {
			uLogin.setUid(Integer.valueOf(baseUid));
		} else {
			uLogin.setUid(2);
		}
		
		uLogin.setLoginTime(new Date());
		int uptCount = userService.updateGUserLogin(uLogin);
		if (uptCount == 0) {
			userService.insertGUserLogin(uLogin);
		}
		CloudUser u = userService.selectUserById(uLogin.getUid());
		request.getSession().setAttribute(Constant.SESSION_USER, u);
		CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);
		
		mv.setViewName("animal_home");
		return mv;
	}
	
	@RequestMapping(value = "/getQRCode")
	@ResponseBody
	public Object createChnnoQrCode(HttpServletRequest request, HttpServletResponse response, String chnno) {
		CloudWxServiceProvider provider = userService.getActiveServiceProvider();
		AjaxJson json = new AjaxJson();
		try {
			CloudChnCode chno = userService.createChnnoQrCode(chnno, provider);
			response.sendRedirect(chno.getCodeUrl());
		} catch (Exception e) {
			e.printStackTrace();
			json.setCode(ErrorCode.ERROR);
			json.setData(e.getMessage());
		}
		return json;
	}
}
