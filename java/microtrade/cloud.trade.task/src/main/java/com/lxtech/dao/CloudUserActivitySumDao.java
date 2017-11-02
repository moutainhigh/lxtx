package com.lxtech.dao;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.lxtech.model.CloudTempMsg;
import com.lxtech.model.CloudUserActivitySum;
import com.lxtech.util.JdbcUtils;

public class CloudUserActivitySumDao {
	public static void saveData(CloudUserActivitySum cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_user_activity_sum (uid,wxnm,tran_day_count,tran_order_count,tran_amount,tran_commission,tran_profit,`date`)values (?,?,?,?,?,?,?,?)";
		Object params[] = { cc.getUid(), cc.getWxnm(), cc.getTran_day_count(), cc.getTran_order_count(),
				cc.getTran_amount(), cc.getTran_commission(), cc.getTran_profit(), cc.getDate() };
		qr.update(sql, params);
	}

	public static int updateData(CloudUserActivitySum cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update cloud_user_activity_sum set wxnm=?, tran_day_count =?, tran_order_count = ?, tran_amount = ?, tran_commission = ?, tran_profit = ?, `date` = ? where uid = ?";
		Object params[] = {cc.getWxnm(), cc.getTran_day_count(), cc.getTran_order_count(), cc.getTran_amount(),
				cc.getTran_commission(), cc.getTran_profit(), cc.getDate(), cc.getUid() };
		return qr.update(sql, params);
	}

	public static CloudUserActivitySum queryUserActivitySum(Integer id) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as tran_order_count,IFNULL(sum(ABS(f_profit)),0) as tran_amount,IFNULL(sum(f_profit),0) as tran_profit,IFNULL(sum(commission-coupon_commission),0) as tran_commission from cloud_order where uid=?";
		Object params[] = { id };
		return qr.query(sql, new BeanHandler<CloudUserActivitySum>(CloudUserActivitySum.class), params);
	}

	public static int queryUserTranOrderSum(Integer id) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(a.count) as count from (select DISTINCT(DATE_FORMAT(order_time,'%Y-%m-%d')) as count from cloud_order where uid=? group by DATE_FORMAT(order_time,'%Y-%m-%d')) a";
		Object params[] = { id };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}
}
