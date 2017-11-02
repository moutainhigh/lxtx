package com.lxtech.cloud.activity;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.cache.GlobalCacheUtil;
import com.lxtech.cloud.db.CloudCouponHandler;
import com.lxtech.cloud.db.model.CloudCoupon;
import com.lxtech.cloud.db.model.CloudUser;
import com.lxtech.cloud.util.ActivityConfig;
import com.lxtech.cloud.util.TimeUtil;

public class LoginActivity extends AbstractActivity{
	
	private final static Logger logger = LoggerFactory.getLogger(LoginActivity.class);
	
	public LoginActivity(String user_id) {
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
					CloudCouponHandler.sendCoupon(user, Integer.valueOf(config.get("coupon.level1")), 1);
				} else {
					Date date = coupon.getAdd_time();
					Calendar c = Calendar.getInstance();
					DateTime dt = new  DateTime(c);
					int dayOfWeek = dt.getDayOfWeek();
					
					if (!TimeUtil.getDay(date).equals(TimeUtil.getDay(new Date())) && dayOfWeek <= 5) {
						//今天没送过
						int count = CloudCouponHandler.getSentCouponCount(user.getId());
						if (count < Integer.valueOf(config.get("coupon.level1.day"))) {//coupon.level1.day
							CloudCouponHandler.sendCoupon(user, Integer.valueOf(config.get("coupon.level1")).intValue(), count+1);
						} else if(count < Integer.valueOf(config.get("coupon.level2.day"))) {
							CloudCouponHandler.sendCoupon(user, Integer.valueOf(config.get("coupon.level2")), (count+1));
						} else {
							//已经送完
							logger.info("用户现金券已经送完["+user.getId()+"]");
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
}
