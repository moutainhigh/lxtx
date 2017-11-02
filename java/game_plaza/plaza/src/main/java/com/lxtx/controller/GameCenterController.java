package com.lxtx.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.model.CloudUser;
import com.lxtx.model.GNotice;
import com.lxtx.model.GSendFree;
import com.lxtx.model.GUserLogin;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.StringUtil;
import com.lxtx.util.net.CookieManager;

@Controller
public class GameCenterController {
	private static final Logger logger = LoggerFactory.getLogger(GameCenterController.class);
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/game_center/{wxid}/{chnno}")
	public ModelAndView game_center(HttpServletRequest request, @PathVariable("wxid") String wxid,
			@PathVariable("chnno") String chnno, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String accessType = (String) request.getSession().getAttribute("wxid");
		logger.info("session wxid:" + accessType);
		if (StringUtils.isEmpty(accessType) || !accessType.equals(wxid)) {
			mv.setViewName("error/999");
			return mv;
		}
		CloudUser user = userService.selectByWxid(wxid);
		request.getSession().setAttribute(Constant.SESSION_USER, user);
		mv.setViewName("game_center");
		mv.addObject("user", user);
		return mv;
	}

	/**
	 * 测试入口
	 * 
	 * @param request
	 * @param wxid
	 * @param chnno
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/game_center1/{wxid}/{chnno}")
	public ModelAndView game_center1(HttpServletRequest request, @PathVariable("wxid") String wxid,
			@PathVariable("chnno") String chnno, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		CloudUser user = userService.selectByWxid(wxid);
		request.getSession().setAttribute("wxid", wxid);
		request.getSession().setAttribute(Constant.SESSION_USER, user);
		mv.setViewName("game_center");
		mv.addObject("user", user);
		return mv;
	}

	@RequestMapping(value = "/getUserInfo/{wxid}")
	@ResponseBody
	public Object getUserInfo(HttpServletRequest request, @PathVariable("wxid") String wxid) {
		AjaxJson json = new AjaxJson();
		CloudUser user = userService.selectByWxid(wxid);
		json.setCode(ErrorCode.SUCCESS);
		json.setData(user);
		return json;
	}

	@RequestMapping(value = "/shop/{wxid}")
	public ModelAndView shop(@PathVariable("wxid") String wxid, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		CloudUser user = userService.selectByWxid(wxid);
		request.getSession().setAttribute(Constant.SESSION_USER, user);
		String shellCode = userService.selectConfigStr("shell.code.img");
		mv.addObject("shellCode", shellCode);
		mv.setViewName("shop");
		mv.addObject("source", "gamecenter");
		mv.addObject("user", user);
		return mv;
	}

	/**
	 * This link is for integrating other websites, such as red packet site
	 * 
	 * @param wxid
	 * @param source
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/shop/{wxid}/{source}")
	public ModelAndView shopforall(@PathVariable("wxid") String wxid, @PathVariable("source") String source,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String config = request.getParameter("config");
		CloudUser user = userService.selectByWxid(wxid);
		request.getSession().setAttribute(Constant.SESSION_USER, user);
		String shellCode = userService.selectConfigStr("shell.code.img");
		mv.addObject("shellCode", shellCode);
		mv.setViewName("shop");
		mv.addObject("source", source);
		logger.info("config1:"+config);
		mv.addObject("config", config);
		mv.addObject("user", user);
		return mv;
	}

	@RequestMapping(value = "/my/{wxid}")
	public ModelAndView my(@PathVariable("wxid") String wxid, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String accessType = (String) request.getSession().getAttribute("wxid");
		logger.info("session wxid:" + accessType);
		if (StringUtils.isEmpty(accessType) || !accessType.equals(wxid)) {
			mv.setViewName("error/999");
			return mv;
		}
		CloudUser user = userService.selectByWxid(wxid);
		request.getSession().setAttribute(Constant.SESSION_USER, user);
		mv.setViewName("my");
		mv.addObject("user", user);
		return mv;
	}

	@RequestMapping(value = "/shake/{wxid}")
	public ModelAndView shake(@PathVariable("wxid") String wxid, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String accessType = (String) request.getSession().getAttribute("wxid");
		logger.info("session wxid:" + accessType);
		if (StringUtils.isEmpty(accessType) || !accessType.equals(wxid)) {
			mv.setViewName("error/999");
			return mv;
		}
		CloudUser user = userService.selectByWxid(wxid);
		if (null == user) {
			mv.setViewName("error/999");
			return mv;
		}
		HttpSession session = request.getSession();
		// 创建或新增cookie
		GUserLogin uLogin = new GUserLogin();
		String cookie = CookieManager.getCookie(request, "shake_home_login_auth");
		if (StringUtils.isNotEmpty(cookie)) {
			GUserLogin uL = userService.selectGuserByCookie(cookie);
			if (null == uL) {
				cookie = UUID.randomUUID().toString();
			} else {
				if (user.getId().intValue() == uL.getUid().intValue()) {
				} else {
					cookie = UUID.randomUUID().toString();
				}
			}
		} else {
			cookie = UUID.randomUUID().toString();
		}
		uLogin.setCookie(cookie);
		logger.info("dice create cookie:uid=" + user.getId() + ",cookie=" + uLogin.getCookie());
		uLogin.setUid(user.getId());
		uLogin.setLoginTime(new Date());
		int uptCount = userService.updateGUserLogin(uLogin);
		if (uptCount == 0) {
			userService.insertGUserLogin(uLogin);
		}
		session.setAttribute(Constant.SESSION_USER, user);
		CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);
		mv.setViewName("shake_home");
		return mv;
	}

	@RequestMapping(value = "/luxurycar/{wxid}")
	public ModelAndView luxurycar(@PathVariable("wxid") String wxid, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String accessType = (String) request.getSession().getAttribute("wxid");
		logger.info("session wxid:" + accessType);
		if (StringUtils.isEmpty(accessType) || !accessType.equals(wxid)) {
			mv.setViewName("error/999");
			return mv;
		}
		CloudUser user = userService.selectByWxid(wxid);
		if (null == user) {
			mv.setViewName("error/999");
			return mv;
		}
		HttpSession session = request.getSession();
		// 创建或新增cookie
		GUserLogin uLogin = new GUserLogin();
		String cookie = CookieManager.getCookie(request, "shake_home_login_auth");
		if (StringUtils.isNotEmpty(cookie)) {
			GUserLogin uL = userService.selectGuserByCookie(cookie);
			if (null == uL) {
				cookie = UUID.randomUUID().toString();
			} else {
				if (user.getId().intValue() == uL.getUid().intValue()) {
				} else {
					cookie = UUID.randomUUID().toString();
				}
			}
		} else {
			cookie = UUID.randomUUID().toString();
		}
		uLogin.setCookie(cookie);
		logger.info("car create cookie:uid=" + user.getId() + ",cookie=" + uLogin.getCookie());
		uLogin.setUid(user.getId());
		uLogin.setLoginTime(new Date());
		int uptCount = userService.updateGUserLogin(uLogin);
		if (uptCount == 0) {
			userService.insertGUserLogin(uLogin);
		}
		session.setAttribute(Constant.SESSION_USER, user);
		CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);
		mv.setViewName("luxurycar_home");
		return mv;
	}

	@RequestMapping(value = "/animal/{wxid}")
	public ModelAndView animal(@PathVariable("wxid") String wxid, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String accessType = (String) request.getSession().getAttribute("wxid");
		logger.info("session wxid:" + accessType);
		if (StringUtils.isEmpty(accessType) || !accessType.equals(wxid)) {
			mv.setViewName("error/999");
			return mv;
		}
		CloudUser user = userService.selectByWxid(wxid);
		if (null == user) {
			mv.setViewName("error/999");
			return mv;
		}
		HttpSession session = request.getSession();
		// 创建或新增cookie
		GUserLogin uLogin = new GUserLogin();
		String cookie = CookieManager.getCookie(request, "shake_home_login_auth");
		if (StringUtils.isNotEmpty(cookie)) {
			GUserLogin uL = userService.selectGuserByCookie(cookie);
			if (null == uL) {
				cookie = UUID.randomUUID().toString();
			} else {
				if (user.getId().intValue() == uL.getUid().intValue()) {
				} else {
					cookie = UUID.randomUUID().toString();
				}
			}
		} else {
			cookie = UUID.randomUUID().toString();
		}
		uLogin.setCookie(cookie);
		logger.info("animal create cookie:uid=" + user.getId() + ",cookie=" + uLogin.getCookie());
		uLogin.setUid(user.getId());
		uLogin.setLoginTime(new Date());
		int uptCount = userService.updateGUserLogin(uLogin);
		if (uptCount == 0) {
			userService.insertGUserLogin(uLogin);
		}
		session.setAttribute(Constant.SESSION_USER, user);
		CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);
		mv.setViewName("animal_home");
		return mv;
	}

	@RequestMapping(value = "/dig/{wxid}")
	public ModelAndView dig(@PathVariable("wxid") String wxid, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String accessType = (String) request.getSession().getAttribute("wxid");
		logger.info("session wxid:" + accessType);
		if (StringUtils.isEmpty(accessType) || !accessType.equals(wxid)) {
			mv.setViewName("error/999");
			return mv;
		}
		HttpSession session = request.getSession();

		CloudUser user = userService.selectByWxid(wxid);
		if (null == user) {
			mv.setViewName("error/999");
			return mv;
		}
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		if (null == sessionUser) {
			session.setAttribute(Constant.SESSION_USER, user);
		} else {
			user.setUserStatus(sessionUser.getUserStatus());
			if (StringUtils.isNotEmpty(sessionUser.getLoginStatus())) {
				user.setLoginStatus(sessionUser.getLoginStatus());
			}
		}
		session.setAttribute(Constant.SESSION_USER, user);
		mv.addObject("user", user);
		mv.setViewName("home");
		return mv;
	}

	@RequestMapping("checkNotice")
	@ResponseBody
	public Object checkNotice(HttpServletRequest request, HttpServletResponse response) {
		String now = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		Map<String, Object> map = new HashMap<>();
		GNotice notice = userService.getNotice(now);
		if (notice == null) {
			map.put("needNotice", 0);
			return map;
		}
		String cookie = CookieManager.getCookie(request, now + "_" + notice.getId());
		if (StringUtils.isNotEmpty(cookie)) {
			map.put("needNotice", 0);
			return map;
		} else {
			CookieManager.setCookie(now + "_" + notice.getId(), "", 3600 * 24, response);
		}
		map.put("needNotice", 1);
		map.put("notice", notice);
		return map;
	}

	@RequestMapping("checkFreeAmount/{uid}")
	@ResponseBody
	public Object checkFreeAmount(@PathVariable("uid") int uid) {
		Map<String, Object> map = new HashMap<>();
		// 查询配置
		Integer sendCount = userService.selectConfig("send.day.sum");
		Integer money = userService.selectConfig("send.money");
		if (sendCount == null || money == null) {
			map.put("checkFree", 0);
			return map;
		}
		try {
			GSendFree gSendFree = userService.checkFreeAmount(uid);
			if (gSendFree == null) {
				// 插入一条
				GSendFree gSend = new GSendFree();
				gSend.setDate(StringUtil.dateCommon(new Date(), -1));
				gSend.setFreeCount(0);
				gSend.setUid(uid);
				gSend.setMoney(money);
				userService.insertSendFree(gSend);
				map.put("checkFree", 1);
				map.put("gSendFree", gSend);
				return map;
			} else {
				gSendFree.setMoney(money);
				// 比较时间，如果小于今天且小于send.day.sum，则送;
				if (gSendFree.getFreeCount() < sendCount.intValue()) {
					String now = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
					if (StringUtil.compareTwoStrDate(now, gSendFree.getDate1())) {
						map.put("checkFree", 1);
						map.put("gSendFree", gSendFree);
						return map;
					}
				}
				map.put("checkFree", 0);
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("checkFree", 0);
			return map;
		}
	}

	@RequestMapping("getFreeAmount/{uid}")
	@ResponseBody
	public Object getFreeAmount(@PathVariable("uid") int uid) {
		Map<String, Object> map = new HashMap<>();
		try {
			int money = userService.getFreeAmount(uid);
			map.put("code", 0);
			map.put("money", money);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			map.put("code", -1);
			return map;
		}
	}

	@RequestMapping("checkIssubscribe/{wxid}")
	public String checkIssubscribe(@PathVariable("wxid") String wxid, HttpServletRequest request) {
		CloudUser u = userService.selectByWxid(wxid);
		String source = request.getParameter("source");
//		if (StringUtils.isEmpty(u.getHeadimgurl())) {
//			return "redirect:/enterShellCodePage/" + wxid + "/" + source;
//		}
		return "redirect:/wx/duobaoredirect/" + wxid + "/transferfromgame?source=" + source;
	}

	@RequestMapping("enterShellCodePage/{wxid}/{source}")
	public ModelAndView enterShellCodePage(@PathVariable("wxid") String wxid, @PathVariable("source") String source) {
		ModelAndView mv = new ModelAndView();
		String shellCode = userService.selectConfigStr("shell.code.img");
		mv.addObject("shellCode", shellCode);
		mv.addObject("wxid", wxid);
		mv.addObject("source", source);
		mv.setViewName("shellCodePage");
		return mv;
	}
}
