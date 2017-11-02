package com.lxtech.cloud.db;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.google.common.collect.ImmutableMap;
import com.lxtech.cloud.db.model.CloudCoupon;
import com.lxtech.cloud.db.model.CloudUser;
import com.lxtech.cloud.net.CloudNetPackage;
import com.lxtech.cloud.net.CloudPackageHeader;
import com.lxtech.cloud.net.CloudPackageType;
import com.lxtech.cloud.net.KestrelConnector;
import com.lxtech.cloud.util.ActivityConfig;
import com.lxtech.cloud.util.JsonUtil;
import com.lxtech.cloud.util.TimeUtil;


public class CloudCouponHandler {
	public static final String DEFAULT_NAME = "现金券";
	public static final String DISCOUNT_COUPON = "折扣券";
	
	public static CloudCoupon getMostRecentCoupon(long uid) throws SQLException {
		String sql = "select * from cloud_coupon where uid = ? order by id desc limit 1";

		Object[] params = { uid };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudCoupon>(CloudCoupon.class));
	}
	
	public static int getSentCouponCount(long uid) throws SQLException  {
		String sql = "select count(*) from cloud_coupon where uid = ?";
		Object[] params = { uid };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return (int)qr.query(sql, params, new ScalarHandler<>());
	}
	
	public static int sendCoupon(CloudUser user , int amount, int day) throws SQLException {
		String sql = "insert into cloud_coupon(uid, coupon_type, status, add_time, overdue_time, coupon_name, coupon_amount) values(?,?,?,?,?,?,?)";
		Object[] params = {user.getId(), 1, 0, new Date(), new Date(), DEFAULT_NAME, amount};
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		int result = qr.update(sql, params);
		
		//发送消息到队列
		//String body = "{\"ConnectionId\":11, \"wxid\":\"opPADwBmRX-6Uz9RsGCDs44Bb6Do\", \"title\":\"welcome\", \"message\":\"welcome\"}";
	/*	ActivityConfig config = new ActivityConfig("/coupon.properties");
		String title = String.format(config.get("coupon.title"), amount);
		String couponMsg = String.format(config.get("coupon.message"), day);
		Map map = ImmutableMap.of("wxid", user.getWxid(), "title", title, "message", couponMsg, "type", "coupon");
		String body = JsonUtil.convertObjToStr(map);
		
		String header = CloudPackageHeader.generateResponseHeader(body.length(), CloudPackageType.MSG_TYPE_News, "1");
		String message = header + CloudNetPackage.PACKAGE_SPLITTER + body;
		String queueName = config.get("coupon.message.queue");
		KestrelConnector.enqueue(queueName, message);*/
		
		return result;
	}
	
	public static int saveCoupon(int uid, int coupon_type, Date day, List<Integer> amountList) throws SQLException {
		String sql = "insert into cloud_coupon(uid, coupon_type, status, add_time, overdue_time, coupon_name, coupon_amount) values(?,?,?,?,?,?,?)";
		for (int amount : amountList) {
			Date targetDay = TimeUtil.getPlusDay(day, 7);
			Object[] params = {uid, coupon_type, 0, day, targetDay, DEFAULT_NAME, amount};
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			qr.update(sql, params);
		}

		return 1;
	}
	
	public static void main(String[] args) {
		/*try {
			CloudCoupon coupon = CloudCouponHandler.getMostRecentCoupon(1);
			System.out.println("sent coupon count:" + CloudCouponHandler.getSentCouponCount(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		
//		CloudCouponHandler.sendCoupon(user, amount, day)
	}

	public static void saveDiscountCoupon(Integer id, int coupon_type, Date d) throws SQLException {
		String sql = "insert into cloud_coupon(uid, coupon_type, status, add_time, overdue_time, coupon_name, use_amount, rate) values(?,?,?,?,?,?,?,?)";
		Date targetDay = TimeUtil.getPlusDay(d, 7);
		Object[] params = {id, 2, 0, d, targetDay, DISCOUNT_COUPON, 100, 9};
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		qr.update(sql, params);
	}
}
