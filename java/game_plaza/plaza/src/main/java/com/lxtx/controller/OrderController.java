package com.lxtx.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudUser;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/enterCreateOrder")
	public ModelAndView enterCreateOrder() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/order/createorder");
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param order
	 * @return
	 */
	@RequestMapping(value = "/createUserOrderPoint")
	@ResponseBody
	public Object createUserOrderPoint(HttpServletRequest request, CloudOrder order) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = orderService.createOrder(request, order);
		} catch (Exception e) {
			map.put("ErrCode", -104);
			map.put("Message", "建仓失败");
			map.put("status", "1");
			map.put("errorMsg", "建仓失败");
			return map;
		}
		return map;
	}

	@RequestMapping(value = "/getNewOrderAmount")
	@ResponseBody
	public Object getNewOrderAmount(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		String subject = request.getParameter("subject");
		CloudUser user = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);

		if (user == null) {
			map.put(Constant.RESPONSE_CODE, ErrorCode.SESSION_LOST);
			return map;
		}
		int orderAmount = orderService.getNewOrderAmount(user.getId(), subject);
		user = userService.selectUserById(user.getId());
		map.put(Constant.RESPONSE_CODE, ErrorCode.SUCCESS);
		map.put(Constant.RESPONSE_DATA, orderAmount);
		map.put("user", user);
		return map;
	}
}
