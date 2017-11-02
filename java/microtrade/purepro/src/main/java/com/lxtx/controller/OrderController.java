package com.lxtx.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudUser;
import com.lxtx.model.vo.PersonalOrderStat;
import com.lxtx.service.cache.PersonalOrderCache;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;

@Controller
@RequestMapping("/order")
public class OrderController {
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
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
		if (!Strings.isNullOrEmpty(request.getParameter("test"))) {
			CloudUser user = userService.selectByWxid("oHttkwri9Xr4uGHyoM-_fhj4JQbA");
			request.getSession().setAttribute(Constant.SESSION_USER, user);
			order = new CloudOrder();
			order.setMoney(BigDecimal.valueOf(10));
			order.setAmount(1);
			order.setOrderIndex(18800);
			order.setSubject("BTC");
			order.setLimit(100);
			order.setChnno("6688");
			order.setDirection(1);
			order.setStatus(0);
		}
		
		Map<String, Object> map = new HashMap<>();
		try {
			if (order.getLimit() < 0 || order.getLimit() > 200) { 
				map.put("ErrCode", -102);
				map.put("Message", "参数不合法");
				map.put("status", "1");
				map.put("errorMsg", "参数不合法");
				return map;
			}
			logger.info("before creating the order.");
			
			map = orderService.createOrder(request, order);
			logger.info((String)map.get("status"));
		} catch (Exception e) {
			logger.info("create order fail here." + e.getMessage());
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
