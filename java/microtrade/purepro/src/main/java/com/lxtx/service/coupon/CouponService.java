package com.lxtx.service.coupon;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudCouponMapper;
import com.lxtx.model.CloudCoupon;

@Service
public class CouponService {
	private static final Logger logger = LoggerFactory.getLogger(CouponService.class);
	@Autowired
	private CloudCouponMapper cloudCouponMapper;

	public List<CloudCoupon> queryCloudCouponByStatus(Integer uid, int status) {
		return cloudCouponMapper.queryCloudCouponByStatus(uid, status);
	}

	public int queryCountCloudCouponByStatus(Integer uid, int status) {
		return cloudCouponMapper.queryCountCloudCouponByStatus(uid, status);
	}

	/**
	 * 返回某天未使用且未通知的券
	 * @param uid
	 * @param date
	 * @return
	 */
	public CloudCoupon queryCloudCouponByUid(Integer uid, String date) {
		return cloudCouponMapper.queryCloudCouponByUid(uid, date);
	}
	
	public int queryCloudCouponCount(Integer uid) {
		return cloudCouponMapper.queryCloudCouponCount(uid);
	}
	
	public int updateCloudCoupon(CloudCoupon coupon) {
		return cloudCouponMapper.updateByPrimaryKey(coupon);
	}
}
