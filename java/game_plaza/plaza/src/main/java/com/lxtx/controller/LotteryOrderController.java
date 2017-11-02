package com.lxtx.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;
import com.lxtx.model.CloudUser;
import com.lxtx.model.LotteryCqsscData;
import com.lxtx.model.LotteryOrder;
import com.lxtx.service.order.LotteryOrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.Constant;
import com.lxtx.util.TimeUtil;

@Controller
@RequestMapping("/lotteryorder")
public class LotteryOrderController {

	@Autowired
	private LotteryOrderService lotteryOrderService;
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/placeorderstate")
	@ResponseBody
	public Object queryPlaceOrderState(HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			map = lotteryOrderService.getPlaceOrderState(true);
		} catch (Exception e) {
			return map;
		}
		return map;
	}
	
	@RequestMapping(value = "/seelotteryresultlist")
	public ModelAndView seelotteryresultlist(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/lottery/lotterycodelist");
		return mv;
	}
	
	@RequestMapping(value = "/seerule")
	public ModelAndView seerule(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/lottery/seerule");
		return mv;
	}
	
	@RequestMapping(value = "/ajax_index_check")
	@ResponseBody
	public Object checkIndexCode(HttpServletRequest request) {
		int index = 0;
		try {
			index = Integer.valueOf(request.getParameter("index"));
		} catch (Exception e) {
		}
		if (index == 0) {
			return ImmutableMap.of("status", -1);
		}
		
		LotteryCqsscData latestData = lotteryOrderService.getLatestLotteryData();
		if (latestData.getSerialNumber() == index) {
			return  ImmutableMap.of("status", 1, "code", latestData.getOpenCode());
		} else {
			return  ImmutableMap.of("status", 0);
		}
	}
	
	@RequestMapping(value = "/ajax_index_up")
	@ResponseBody
	public Object checkIndexUpdate(HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		LotteryCqsscData data = lotteryOrderService.getLatestLotteryData();

		Date opentime = data.getOpenTime();
		DateTime dt = new DateTime();
		int hourofday = dt.getHourOfDay();
		int minutespan = 10;
		if (hourofday >= 2 && hourofday < 10) {
			map.put("status", -2);
			return map;
		} else if (hourofday >= 10 && hourofday < 22) {
			minutespan = 10;
		} else {
			minutespan = 5;
		}
		
		long diff = System.currentTimeMillis() - opentime.getTime();
		int minutes = (int)diff/1000/60;
		if (minutes > minutespan) {
			map.put("status", -1);
		} else {
			if (minutes < 5) {
				map.put("status", 1);
			} else {
				map.put("status", 0);
			}
		}
		 
		return map;
	}
	
	/**
	 * 
	 * @param request
	 * @param order
	 * @return
	 */
	@RequestMapping(value = "/createorder")
	@ResponseBody
	public Object createUserOrder(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		int period = Integer.valueOf(request.getParameter("period"));
		int number = Integer.valueOf(request.getParameter("number"));
		int partake_number = Integer.valueOf(request.getParameter("partake_number"));
		
		CloudUser sessionUser = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		if (sessionUser == null) {
			return ImmutableMap.of("data", 110);
		}
		
		LotteryOrder order = new LotteryOrder();
		order.setCode(partake_number);
		order.setDirectDate(TimeUtil.getDayByDate(new Date()));
		order.setDirectSn(period);
		order.setMoney(number * 100);
		order.setOrderTime(new Date());
		order.setUserId(sessionUser.getId());
		order.setState(0);
		
		try {
			map = lotteryOrderService.createOrder(request, order);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("ErrCode", -104);
			map.put("Message", "下单失败");
			map.put("status", "1");
			map.put("errorMsg", "下单失败");
			return map;
		}
		return map;
	}

	@RequestMapping(value = "/cqsscTodayDatas")
	@ResponseBody
	public Object queryCqsscTodayDatas(HttpServletRequest request){
		List<LotteryCqsscData> datas = lotteryOrderService.getCqsscTodayDatas();
		Map map = ImmutableMap.of("recordList", datas);
		return map;
	}
	
	@RequestMapping(value = "/userTodayOrders")
	@ResponseBody
	public Object queryTodayOrders(HttpServletRequest request){
		return lotteryOrderService.getTodayOrders(request);
	}

	@RequestMapping(value = "/notices")
	@ResponseBody
	public Object getCurrentNotices(HttpServletRequest request){
		List<LotteryOrder> orders = this.lotteryOrderService.getCurrentWinOrders();
		if(null != orders){
			Map<Integer, Integer> userWins = new HashMap<Integer, Integer>();
			List<String> notices = new ArrayList<String>();
			for(LotteryOrder order : orders){
				Integer last = userWins.get(order.getUserId());
				last = null == last ? 0 : last;
				userWins.put(order.getUserId(), last + order.getSettlementResult());
			}
			for(Entry<Integer, Integer> entry : userWins.entrySet()){
				CloudUser user = userService.selectUserById(entry.getKey());
				if(null != user){
					notices.add("恭喜"+user.getWxnm()+"成功抢走" + entry.getValue() + "夺宝币");
				}
			}
			return notices;
		}else{
			return null;
		}
	}
}
