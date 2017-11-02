package com.lxtx.controller.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudUser;
import com.lxtx.service.coupon.CouponService;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.CommonUtil;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.SmsSendUtil;
import com.lxtx.util.StringUtil;
import com.lxtx.util.SystemSwitchConfig;
import com.lxtx.util.net.CookieManager;
import com.lxtx.util.tencent.UserInfo;
import com.lxtx.util.tencent.WXPayConstants;
import com.lxtx.util.tool.EncryptUtil;

@Controller
@RequestMapping(value = "/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CouponService couponService;

	/**
	 * 
	 * Description: 关注微信，动用返回微信Id，传入微信id
	 * 
	 * @author hecm
	 * @date 2016年10月28日 下午8:05:45
	 */
	@RequestMapping("/wxVisit")
	public ModelAndView wxVisit(HttpServletRequest request) {
		logger.info("in wxVisit");
		ModelAndView mv = new ModelAndView();
		UserInfo info = (UserInfo) request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		CloudUser user = userService.wxVisit(info);
		if (user == null) {
			// must subcribe again
			mv.setViewName("error/999");
			return mv;
		}

		// 根据user状态跳转页面
		HttpSession session = request.getSession();
		session.setAttribute(Constant.SESSION_USER, user);
		mv.setViewName("home");
		return mv;
	}

	/**
	 * 完成新手提示后 修改状态
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/finishFirstVisit")
	@ResponseBody
	public Object finishFirstVisit(HttpServletRequest request) {
		AjaxJson json = new AjaxJson();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		userService.updateVisitById(sessionUser.getId());
		sessionUser.setFirstVisit(1);
		session.setAttribute(Constant.SESSION_USER, sessionUser);
		json.setCode(ErrorCode.SUCCESS);
		return json;
	}

	@RequestMapping("/enterUserCenter")
	public ModelAndView enterUserCenter(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		int uid = sessionUser.getId();
		sessionUser.setBalance(userService.selectUserById(uid).getBalance());
		mv.addObject("user", sessionUser);
		mv.setViewName("/user/user_center");
		return mv;
	}

	@RequestMapping("/getCurrentUser")
	@ResponseBody
	public Object getCurrentUser(HttpServletRequest request,
			@RequestParam(value = "wxid", required = true) String wxid) {
		AjaxJson json = new AjaxJson();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		CloudUser user = userService.selectByWxid(wxid);
		String checkPwd = SystemSwitchConfig.getInstance().get("checkPwd");
		//无需输入交易密码
		if(checkPwd.equals("1")){
			user.setLoginStatus("1");
			user.setUserStatus(Constant.USER_STAT_NORMAL);
		}else{
			if (null != sessionUser) {
				logger.info("session user login status is: " + sessionUser.getWxid() + " " + sessionUser.getLoginStatus());
				if (StringUtils.isNotEmpty(sessionUser.getLoginStatus())) {
					user.setLoginStatus(sessionUser.getLoginStatus());
				} else {
					String cookieValue = CookieManager.getCookie(request, "login_status" + wxid);
					logger.info("retrieve cookie for " + "login_status" + wxid + " value is: " + cookieValue);
					if (!Strings.isNullOrEmpty(cookieValue)) {
						user.setLoginStatus("1");
						logger.info("update login_status for user" + wxid);
					}
				}
			} else {
				String cookieValue = CookieManager.getCookie(request, "login_status" + wxid);
				logger.info("retrieve cookie for " + "login_status" + wxid + " value is: " + cookieValue);
				if (!Strings.isNullOrEmpty(cookieValue)) {
					user.setLoginStatus("1");
					logger.info("update login_status for user" + wxid);
				}
			}
		}
		user.setSubjectCount(SystemSwitchConfig.getInstance().get("subject_count"));
		session.setAttribute(Constant.SESSION_USER, user);
		json.setData(user);
		return json;
	}

	/**
	 * 
	 * Description:进入手机设置界面
	 *
	 * @author hecm
	 * @date 2016年10月29日 下午2:28:40
	 */
	@RequestMapping("/toSetTel")
	public ModelAndView toSetTel(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/user/to_set_tel");
		return mv;
	}

	/**
	 * 
	 * Description:获取验证码
	 *
	 * @author hecm
	 * @date 2016年10月28日 下午9:13:01
	 */
	@RequestMapping("/getTelValidCode")
	@ResponseBody
	public Object getTelValidCode(HttpServletRequest request, String mobile) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		if (null == user) {
			map.put("code", 1);
			map.put("errorMsg", "连接服务器超时,请返回重试");
			return map;
		}
		// 发送4位随机验证码
		String validCode = StringUtil.generateRandomNumber(4);
		String resultStr = SmsSendUtil.sendMsg(mobile, validCode);
		if (resultStr.equals("1")) {
			logger.info("{} 验证码：【{}】 time:{}", mobile, validCode, StringUtil.formatDate(new Date()));
			user.setvCodeMobile(mobile);
			user.setValidCode(validCode);
			session.setAttribute(Constant.SESSION_USER, user);
			map.put("code", ErrorCode.SUCCESS);
			map.put("data", validCode);
		} else {
			map.put("code", 1);
			map.put("errorMsg", "短信发送失败,请联系客服");
		}
		return map;
	}

	/**
	 * 
	 * Description: 提交手机号校验 如果校验通过则入库
	 *
	 * @author hecm
	 * @date 2016年10月28日 下午9:22:45
	 */
	@RequestMapping("/submitMbl")
	@ResponseBody
	public Object submitMbl(HttpServletRequest request, String mobile, String validCode) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		if (user == null) {
			map.put("Message", "连接超时，请返回重试!");
			return map;
		}
		if (validCode.equals(user.getValidCode()) && mobile.equals(user.getvCodeMobile())) {
			// 验证通过 保存手机号
			user.setMobile(mobile);
			userService.updateTelById(user);
			user.setUserStatus(Constant.USER_STAT_ND_PWD);
			map.put("code", ErrorCode.SUCCESS);
		} else {
			// 验证失败
			map.put("Message", "手机号码或验证码错误!");
		}
		session.setAttribute(Constant.SESSION_USER, user);
		return map;
	}

	/**
	 * 
	 * Description:进入密码设置界面
	 *
	 * @author hecm
	 * @date 2016年10月29日 下午2:28:40
	 */
	@RequestMapping("/toSetPwd")
	public ModelAndView toSetPwd(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/user/to_set_pwd");
		return mv;
	}

	/**
	 * 
	 * Description: 交易密码设置
	 *
	 * @author hecm
	 * @date 2016年10月29日 上午11:02:23
	 */
	@RequestMapping("/submitPwd")
	@ResponseBody
	public Object submitPwd(HttpServletRequest request, String pwdOne, String pwdTwo) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		try {
			if (pwdOne.equals(pwdTwo)) {
				user.setPassword(EncryptUtil.endcodePassword(pwdOne));
				userService.updatePwdById(user);
				user.setLoginStatus("1");
				user.setUserStatus(Constant.USER_STAT_NORMAL);
				// 返回下一步
				map.put("code", ErrorCode.SUCCESS);
			} else {
				map.put("errorMsg", "交易密码设置失败，请返回重试");
			}
		} catch (Exception e) {
			map.put("code", 1);
			map.put("errorMsg", "交易密码设置失败，请返回重试");
		}
		session.setAttribute(Constant.SESSION_USER, user);
		return map;
	}

	@RequestMapping("/listOrder")
	@ResponseBody
	public Object listOrder(HttpServletRequest request) {
		String target = request.getParameter("target");
		AjaxJson json = new AjaxJson();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (user == null) {
			dataMap.put("orderList", "");
			dataMap.put("user", "");
			json.setData(dataMap);
			return json;
		}
		CloudUser userInfo = userService.selectUserById(user.getId());
		List<CloudOrder> orderList = orderService.listOrderByUidAndStatus(user.getId(), target, 0);
		int seconds = 0;
		json.setCode(ErrorCode.SUCCESS);
		if (orderList != null && orderList.size() > 0) {
			// get the left seconds
			int curSeconds = (int) (System.currentTimeMillis() / 1000);
			for (CloudOrder order : orderList) {
				String orderTime = order.getOrderTime();
				int gap = 60 - curSeconds + CommonUtil.getSeconds(orderTime);
				if (gap < 60 && gap > seconds) {
					seconds = gap;
				}
			}
		}

		dataMap.put("orderList", orderList);
		logger.info("seconds values is:" + seconds + " in /user/listOrder");
		dataMap.put("seconds", seconds);
		dataMap.put("user", userInfo);

		json.setData(dataMap);
		return json;
	}

	/**
	 * 忘记密码 Description:进入手机验证界面
	 *
	 */
	@RequestMapping("/toValidTelPage")
	public ModelAndView toValidTelPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		CloudUser currU = userService.selectUserById(user.getId());
		if (StringUtils.isEmpty(currU.getMobile())) {
			mv.setViewName("redirect:toSetTel");
		} else {
			mv.setViewName("/user/to_valid_tel");
		}
		return mv;
	}

	/**
	 * 忘记密码 Description: 手机验证码校验
	 *
	 */
	@RequestMapping("/checkValidForForgetPwd")
	@ResponseBody
	public Object checkValidForForgetPwd(HttpServletRequest request, String validCode) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		if (validCode.equals(user.getValidCode())) {
			map.put("code", ErrorCode.SUCCESS);
			map.put("errorMsg", "手机号验证成功！");
		} else {
			// 验证失败
			map.put("code", "1");
			map.put("errorMsg", "短信验证码错误!");
		}
		return map;
	}

	/**
	 * 忘记密码 Description:进入密码重设页面
	 *
	 */
	@RequestMapping("/toForgetPwdPage")
	public ModelAndView toForgetPwdPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/user/to_forget_pwd");
		return mv;
	}

	/**
	 * 
	 * Description: 重设密码
	 *
	 */
	@RequestMapping("/setForgetPwd")
	@ResponseBody
	public Object setForgetPwd(HttpServletRequest request, String pwdOne, String pwdTwo) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		try {
			if (pwdOne.equals(pwdTwo)) {
				user.setPassword(EncryptUtil.endcodePassword(pwdOne));
				userService.updatePwdById(user);
				user.setLoginStatus("1");
				user.setUserStatus(Constant.USER_STAT_NORMAL);
				// 返回下一步
				map.put("status", "0");
				map.put("errorMsg", "重设密码成功");
			} else {
				map.put("status", "1");
				map.put("errorMsg", "重设密码失败，请重试");
			}
		} catch (Exception e) {
			map.put("status", "1");
			map.put("errorMsg", "重设密码失败，请重试");
		}
		session.setAttribute(Constant.SESSION_USER, user);
		return map;
	}

	@RequestMapping("/checkcoupon")
	@ResponseBody
	public Object checkCoupon(HttpServletRequest request) {
		CloudUser user = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		Map responseMap = new HashMap();
		if (user == null) {
			responseMap.put(Constant.RESPONSE_CODE, ErrorCode.SESSION_LOST);
			return responseMap;
		} else {
			int count = couponService.queryCloudCouponCount(user.getId());
			if (count > 0) {
				responseMap.put(Constant.RESPONSE_CODE, ErrorCode.SUCCESS);
				return responseMap;
			}
			/*
			 * CloudCoupon coupon =
			 * couponService.queryCloudCouponByUid(user.getId(), new
			 * SimpleDateFormat("yyyy-MM-dd").format(new Date())); if (coupon !=
			 * null) { responseMap.put(Constant.RESPONSE_CODE,
			 * ErrorCode.SUCCESS); // responseMap.put("", value) String message
			 * = MessageConfig.getInstance().get("coupon.message"); String title
			 * = MessageConfig.getInstance().get("coupon.title");
			 * responseMap.put("message", String.format(message, count));
			 * responseMap.put("title", String.format(title,
			 * coupon.getCouponAmount())); //mark it coupon.setNotified(1);
			 * couponService.updateCloudCoupon(coupon); return responseMap; }
			 */
		}
		responseMap.put(Constant.RESPONSE_CODE, ErrorCode.ERROR);
		return responseMap;
	}

}
