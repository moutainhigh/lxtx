
package com.lxtx.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.model.CloudNotice;
import com.lxtx.model.CloudUser;
import com.lxtx.service.user.CloudNoticeService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.net.CookieManager;
import com.lxtx.util.tool.EncryptUtil;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;
	@Autowired
	private CloudNoticeService noticeService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping("/showLogin")
	public ModelAndView showLogin() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("user/login");
		return mv;
	}

	@RequestMapping("/login")
	@ResponseBody
	public Object login(HttpServletRequest request, String pass, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		try {
			CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
			CloudUser cloudUser = userService.selectUserById(user.getId());
			if (EncryptUtil.matches(pass, cloudUser.getPassword())) {
				map.put("code", ErrorCode.SUCCESS);
				user.setLoginStatus("1");
				CookieManager.writeCookie("login_status" + cloudUser.getWxid(), "1", 3600, response);
				map.put("data", user);
			} else {
				user.setLoginStatus("0");
				map.put("Message", "交易密码错误!");
			}
			session.setAttribute(Constant.SESSION_USER, user);
		} catch (Exception e) {
			map.put("Message", "服务连接超时，请返回重试");
		}
		return map;
	}

	@RequestMapping("/checkNotice")
	@ResponseBody
	public Object checkNotice() {
		Map<String, Object> map = new HashMap<>();
		List<CloudNotice> notice = noticeService.queryNotice();
		if (notice == null) {
			map.put("needNotice", 0);
			return map;
		}
		map.put("needNotice", 1);
		map.put("notice", notice);
		return map;
	}
}
