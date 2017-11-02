package com.lxtx.controller.coupon;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.model.CloudCoupon;
import com.lxtx.model.CloudUser;
import com.lxtx.service.coupon.CouponService;
import com.lxtx.util.Constant;
import com.lxtx.util.StringUtil;

@Controller
@RequestMapping(value = "/coupon")
public class CouponController {
	private static final Logger logger = LoggerFactory.getLogger(CouponController.class);

	@Autowired
	private CouponService couponService;

	/**
	 * 未使用优惠券
	 * 
	 * @return
	 */
	@RequestMapping("/coupon")
	public ModelAndView showLogin() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("coupon/coupon");
		return mv;
	}

	@RequestMapping("/getCoupon")
	@ResponseBody
	public Object getCoupon(HttpServletRequest request, HttpServletResponse response, int type) {
		Map<String, Object> map = new HashMap<>();
		CloudUser user = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);

		List<CloudCoupon> conpons = couponService.queryCloudCouponByStatus(user.getId(), type);
		if (type == Constant.COUPON_STAT_UNUSEED) {
			Date now = new Date();
			for (CloudCoupon cloudCoupon : conpons) {
				if (StringUtil.compDateCompare(cloudCoupon.getAddTime(), cloudCoupon.getOverdueTime(), now)) {
					cloudCoupon.setUsedFlag(true);
				} else {
					cloudCoupon.setUsedFlag(false);
				}
			}
		}
		map.put("List", conpons);
		// map.put("totals",
		// couponService.queryCountCloudCouponByStatus(user.getId(), type));
		return map;
	}

	@RequestMapping("/couponDetail")
	public ModelAndView couponDetail() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("coupon/couponDetail");
		return mv;
	}

	@RequestMapping("/couponRateDetail")
	public ModelAndView couponRateDetail() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("coupon/couponRateDetail");
		return mv;
	}

}
