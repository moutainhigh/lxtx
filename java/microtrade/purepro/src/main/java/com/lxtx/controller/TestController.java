package com.lxtx.controller;

import java.util.List;
import java.util.Map;

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
import com.lxtx.controller.user.UserController;
import com.lxtx.model.CloudChnBackCommission;
import com.lxtx.model.CloudChnCode;
import com.lxtx.model.CloudUser;
import com.lxtx.model.CloudWxServiceProvider;
import com.lxtx.service.cache.GeneralCache;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
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
		
		//写入队列
		Map<String, String> map = ImmutableMap.of("user_id", user.getWxid(), "msg_type", "login");
		String message = JsonUtil.convertObjToStr(map);
		KestrelConnector.enqueue("cloud_trade_activity", message);
		logger.info("in TestController, add into queue:"+message);
		
		mv.setViewName("home");
		mv.addObject("user", user);
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

	@RequestMapping(value = "/index")
	public ModelAndView ind(HttpServletRequest request, String wxid) {
		// TODO wxid 参数为测试代码
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession();
		UserInfo userInfo = (UserInfo) session.getAttribute(WXPayConstants.WX_USER_INFO);
		CloudUser clouduser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		if (null == clouduser) {
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

		//写入队列
		Map<String, String> map = ImmutableMap.of("user_id", clouduser.getWxid(), "msg_type", "login");
		String message = JsonUtil.convertObjToStr(map);
		KestrelConnector.enqueue("cloud_trade_activity", message);
		logger.info("in TestController, add into queue:"+message);
		
		mv.addObject("user", clouduser);
		mv.setViewName("home");
		return mv;
	}

	@RequestMapping(value = "/guest")
	public ModelAndView showGuestPage(HttpServletRequest request) {
	  logger.info("in show guest page.");
		CloudUser user = userService.selectByWxid("opPADwPAX7FBXT5KHMPDxbNifbz4");
		request.getSession().setAttribute(Constant.SESSION_USER, user);
		logger.info("in showguestpage, step1");
		//发送登录消息到队列
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
	  userInfo.setHeadimgurl("http://wx.qlogo.cn/mmopen/ajNVdqHZLLC1egRslJj4VFziaa3WMib0ZViaoavRYmAfdpiagqMD3iclnK6PbCe1bIcLCQw4VldaribQstnCfZAvQfEA/0");
	  userInfo.setNickname("哇居然是邵峰邵千万啊");
	  userInfo.setOpenid("oZCnuwmkKOOEPu47mmXmfuL6Pfxo");
	  userInfo.setWxProviderId(1);
	  userService.wxVisit(userInfo);
	  return "123";
	}
	
	@RequestMapping(value = "/testPrint")
	@ResponseBody
	  public Object testPrint() {
		  logger.info("in test save.");
		  UserInfo userInfo = new UserInfo();
		  userInfo.setChnno("1050");
		  userInfo.setHeadimgurl("http://wx.qlogo.cn/mmopen/ajNVdqHZLLC1egRslJj4VFziaa3WMib0ZViaoavRYmAfdpiagqMD3iclnK6PbCe1bIcLCQw4VldaribQstnCfZAvQfEA/0");
		  userInfo.setNickname("哇居然是邵峰邵千万啊");
		  userInfo.setOpenid("oZCnuwmkKOOEPu47mmXmfuL6Pfxo");
		  userInfo.setWxProviderId(1);
		  return userInfo;
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

	@RequestMapping(value = "/getCommissionByDate")
	@ResponseBody
	public Object getCommissionByDate(HttpServletRequest request, int date) {
		AjaxJson json = new AjaxJson();
		try {
			List<CloudChnBackCommission> list = orderService.getCommissionByDate(date);
			json.setCode(ErrorCode.SUCCESS);
			json.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

}
