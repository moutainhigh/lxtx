package com.lxtx.controller.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.model.CloudFundHistory;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudUser;
import com.lxtx.model.LotteryOrder;
import com.lxtx.service.order.LotteryOrderService;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.Constant;
import com.lxtx.util.SmsSendUtil;
import com.lxtx.util.StringUtil;
import com.lxtx.util.SystemSwitchConfig;
import com.lxtx.util.TimeUtil;
import com.lxtx.util.tool.EncryptUtil;

@Controller
@RequestMapping(value = "/user")
public class UserCenterController {
	private static final Logger logger = LoggerFactory.getLogger(UserCenterController.class);
	@Autowired
	private LotteryOrderService orderService;

	@Autowired
	private UserService userService;

	// 1.查询交易轨迹
	@RequestMapping("/userOperRecord")
	public ModelAndView userOperRecord(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/user/user_oper_record");
		return mv;
	}

	@RequestMapping("/queryOperRecord")
	@ResponseBody
	public Object queryUserOperRecord(HttpServletRequest request, Integer id) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		List<LotteryOrder> orders = new ArrayList<>();
		try {
			orders = orderService.getOrdersByUid(sessionUser.getId(), id);
			for (LotteryOrder cloudOrder:orders) {
				if (cloudOrder.getState() == 1) {
					int pos = cloudOrder.getSettlementResult() - cloudOrder.getMoney();
					cloudOrder.setPosition((int)pos/100);
				}
				cloudOrder.setMoney((int)cloudOrder.getMoney()/100);
				cloudOrder.setOrderTime1(TimeUtil.getTimeStr24H(cloudOrder.getOrderTime()));
				if (cloudOrder.getSettlementTime() != null) {
					cloudOrder.setSettlementTime1(TimeUtil.getTimeStr24H(cloudOrder.getSettlementTime()));
				}
				
				switch (cloudOrder.getState()) {
				case 0:
					cloudOrder.setDealkind("等待成交");
					cloudOrder.setPerStyle("earn-zorn");
					break;
				case 1:
					cloudOrder.setDealkind("已平仓");
					if (cloudOrder.getSettlementResult() > 0) {
						cloudOrder.setPerStatus("赚");
						cloudOrder.setPerStyle("earn-up");
					} else {
						cloudOrder.setPerStatus("亏");
						cloudOrder.setPerStyle("earn-down");
					}
					break;
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("Data", orders);
		return map;
	}

	// 2.查询出入金记录
	@RequestMapping("/userAssetRecord")
	public ModelAndView userAssetRecord(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/payment/user_asset_record");
		return mv;
	}

	@RequestMapping("/queryAssetRecord")
	@ResponseBody
	public Object queryAssetRecord(HttpServletRequest request, Integer id) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		List<CloudFundHistory> funds = new ArrayList<>();
		try {
			funds = orderService.getFundByUid(sessionUser.getId(), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("Data", funds);
		map.put("success", true);

		return map;

	}

	// 3.个人设置
	/**
	 * 进入个人设置页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/userSetting")
	public ModelAndView userSetting(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		mv.setViewName("/user/user_setting");
		mv.addObject("mobile", sessionUser.getMobile());
		mv.addObject("checkPwd",SystemSwitchConfig.getInstance().get("checkPwd"));
		return mv;
	}

	/**
	 * 进入密码修改界面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/alterPwd")
	public ModelAndView alterPwd(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/user/alter_pwd");
		return mv;
	}

	/**
	 * 
	 * Description: 交易密码修改
	 *
	 * @author hecm
	 * @date 2016年10月29日 上午11:41:39
	 */
	@RequestMapping("/changePwd")
	@ResponseBody
	public Object changePwd(HttpServletRequest request, String oldPwd, String newPwd) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		CloudUser user1 = userService.selectUserById(user.getId());
		if (EncryptUtil.matches(oldPwd, user1.getPassword())) {
			try {
				user1.setPassword(EncryptUtil.endcodePassword(newPwd));
				userService.updatePwdById(user1);
				map.put("success", true);
			} catch (Exception e) {
				map.put("success", false);
				map.put("erro", "交易密码设置失败，请重试!");
			}
		} else {
			map.put("success", false);
			map.put("erro", "原交易密码错误");
		}
		return map;
	}

	/**
	 * 进入手机号修改界面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/alterTel")
	public ModelAndView alterTel(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/user/alter_tel");
		return mv;
	}

	/**
	 * 修改手机号 发送验证码
	 * 
	 * @param request
	 * @param mobile
	 * @return
	 */
	@RequestMapping("/sendUpTelNo")
	@ResponseBody
	public Object sendUpTelNo(HttpServletRequest request, String mobile) {
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		Map<String, Object> map = new HashMap<>();
		// 根据手机号查询是否已注册过 ，如果注册过，则这个手机号已被绑定 否则，生成验证码
		int count = userService.checkTelExist(mobile);
		if (count == 0) {
			// 发送4位随机验证码
			String validCode = StringUtil.generateRandomNumber(4);
			String resultStr = SmsSendUtil.sendMsg(mobile, validCode);
			if (resultStr.equals("1")) {
				logger.info("{} 验证码：【{}】 time:{}", mobile, validCode, StringUtil.formatDate(new Date()));
				user.setValidCode(validCode);
				session.setAttribute(Constant.SESSION_USER, user);
				map.put("success", true);
				map.put("code", "0");
				map.put("data", validCode);
			} else {
				map.put("success", false);
				map.put("erro", "短信发送失败,请联系客服");
			}
		} else {
			map.put("success", false);
			map.put("erro", "这个手机号已被绑定");
		}
		return map;
	}

	/**
	 * 
	 * Description: 个人手机号修改
	 *
	 * @author hecm
	 * @date 2016年10月29日 上午11:30:23
	 */
	@RequestMapping("/changeTel")
	@ResponseBody
	public Object changeTel(HttpServletRequest request, String newTel, String validCode) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		if (validCode.equals(user.getValidCode())) {
			// 验证通过 保存手机号
			user.setMobile(newTel);
			userService.updateTelById(user);
			map.put("success", true);
			user.setMobile(newTel);
			session.setAttribute(Constant.SESSION_USER, user);
		} else {
			// 验证失败
			map.put("Message", "手机号码或验证码错误!");
		}
		return map;
	}

}
