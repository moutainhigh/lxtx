package com.lxtech.cloud.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.cache.GlobalCacheUtil;
import com.lxtech.cloud.db.CloudCouponHandler;
import com.lxtech.cloud.db.model.CloudCoupon;
import com.lxtech.cloud.db.model.CloudUser;
import com.lxtech.cloud.util.ActivityConfig;
import com.lxtech.cloud.util.TimeUtil;

public class BetterLoginActivity extends AbstractActivity{

	private final static Logger logger = LoggerFactory.getLogger(BetterLoginActivity.class);
	
	public BetterLoginActivity(String user_id) {
		super(user_id);
	}

	@Override
	public void handleActivity() {
		ActivityConfig config = new ActivityConfig("/coupon.properties"); 
		//firstly, query the user information
		CloudUser user = GlobalCacheUtil.getUserCache().get(this.user_id);
		if (user != null) {
			try {
				CloudCoupon coupon = CloudCouponHandler.getMostRecentCoupon(user.getId());
				if (coupon == null) {
					//没有送过券
					Map<Date, List<Integer>> dayMap = TimeUtil.getDateList(5, 5);
					if (user.getChnno().equals("1112") || user.getChnno().equals("1113")
							|| user.getChnno().equals("1115")) {
						List<Integer> singleAmount = new ArrayList<>();
						singleAmount.add(10);
						CloudCouponHandler.saveCoupon(user.getId(), 1, new Date(), singleAmount);
					}
					for (Date d : dayMap.keySet()) {
						List<Integer> amountList = dayMap.get(d);
						if (!user.getChnno().equals("1112") && !user.getChnno().equals("1113")
								&& !user.getChnno().equals("1115")) {
							CloudCouponHandler.saveCoupon(user.getId(), 1, d, amountList);
						}
						CloudCouponHandler.saveDiscountCoupon(user.getId(), 2, d);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws SQLException {
		List<Date> discountDays = TimeUtil.listDateForDiscount();
		for (Date d : discountDays) {
			CloudCouponHandler.saveDiscountCoupon(52, 2, d);
		}
	}
}
