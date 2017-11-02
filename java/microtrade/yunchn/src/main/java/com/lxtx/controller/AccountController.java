package com.lxtx.controller;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.model.CUser;
import com.lxtx.service.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.SystemConfig;

/**
 * 账户相关controller 登录注册
 * 
 * @author hecm
 *
 */
@Controller
public class AccountController extends BaseController {

	@Autowired
	private UserService userService;

	private static final Logger log = Logger.getLogger(AccountController.class);

	/**
	 * 进入登录页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login")
	public ModelAndView enterLoginPage() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("login");
		return mv;
	}

	/**
	 * 进入登录页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public ModelAndView logout(HttpSession session) {
		session.removeAttribute(Constant.SESSION_USER);
		session.removeAttribute("usrInfo");
		session.removeAttribute("userProfitFlag");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("login");
		return mv;
	}

	/**
	 * 登录前的ajax验证
	 * 
	 * @return
	 */
	@RequestMapping(value = "/loginAjax")
	@ResponseBody
	public Object loginAjax(CUser user, HttpSession session) {
		AjaxJson json = new AjaxJson();
		if (userService.verifyUser(user)) {
			user = userService.selectByCode(user.getuCode());
			json.setCode(ErrorCode.SUCCESS);

			/* 校验成功，写session */
			session.setAttribute("usrInfo", user);
			session.setAttribute(Constant.SESSION_USER, user);
			session.setAttribute("userProfitFlag", SystemConfig.getInstance().getSysMenu());
			log.info("登录：" + user.getuCode());

		} else {
			json.setCode(ErrorCode.USER_OR_PWD_ERROR);
			log.info("登录失败，密码错误：" + user.getuCode());
		}

		return json;
	}

	/**
	 * 登录后进入首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/enterIndexPage")
	public String enterIndexPage() {
		/* 重定向到当前可访问的一个页面 */
		return "redirect:/blank";
	}

	@RequestMapping(value = "/blank")
	public ModelAndView blank() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("blank");
		return mv;
	}

	@RequestMapping(value = "/noPower")
	public ModelAndView noPower() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("msg", "您没有访问该功能的权限，请联系管理员！");
		mv.setViewName("noPower");
		return mv;
	}

	@RequestMapping(value = "/")
	public String enterBlack() {
		return "redirect:/blank";
	}
}
