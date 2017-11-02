package com.lxtech.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.lxtech.model.CloudTempMsg;
import com.lxtech.model.CloudUserDayTranSum;
import com.lxtech.util.JdbcUtils;

public class CloudOrderDao {
	public static long getLastOrderTime() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select order_time as oTime from `cloud_order` order by id desc limit 1";
		Object[] params = {};
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Date) map.get("oTime")).getTime();
	}

	public static void main(String[] args) throws SQLException {
		System.out.println(getBeyondOrderLimitData("2016-12-13 00:46:26", "2016-12-13 00:51:26"));
	}

	public static int getBeyondOrderLimitData(String begin, String curr) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as count from `cloud_order` where clear_time>=? and clear_time<? and f_profit <= -1000";
		Object[] params = { begin, curr };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}

	public static int getTranUCountByDate(String date) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as count from (select uid from cloud_order where DATE_FORMAT(clear_time,'%Y-%m-%d')=? group by uid) a";
		Object[] params = { date };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}

	public static int getTranUOldCountByDate(String date) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as count from (select uid from cloud_order a left join cloud_user b on a.uid= b.id where DATE_FORMAT(clear_time,'%Y-%m-%d')=? and DATE_FORMAT(b.crt_tm ,'%Y-%m-%d')<? group by uid) a ";
		Object[] params = { date, date };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}

	public static CloudUserDayTranSum getTranAmountByDate(String date) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select IFNULL(sum(ABS(f_profit)),0) as tran_amount,IFNULL(sum(commission-coupon_commission),0) as tran_commission,count(1) as tran_order_count from cloud_order where DATE_FORMAT(clear_time,'%Y-%m-%d')=? and (status =2 or status =3 )";
		Object[] params = { date };
		return qr.query(sql, new BeanHandler<CloudUserDayTranSum>(CloudUserDayTranSum.class), params);
	}

	public static CloudUserDayTranSum getOldUserTranAmountByDate(String date) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select IFNULL(sum(ABS(c.f_profit)),0) as tran_u_old_amount,IFNULL(sum(c.commission-c.coupon_commission),0) as tran_u_old_commission,count(1) as tran_u_old_order_count from (select b.crt_tm,a.* from cloud_order a left join cloud_user b on a.uid=b.id ) c "
				+ "where DATE_FORMAT(clear_time,'%Y-%m-%d')=? and (status =2 or status =3 ) and DATE_FORMAT(c.crt_tm ,'%Y-%m-%d')<?";
		Object[] params = { date, date };
		return qr.query(sql, new BeanHandler<CloudUserDayTranSum>(CloudUserDayTranSum.class), params);
	}

	public static int getOrderById(String begin, String curr, int uid) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as count from `cloud_order` where order_time>=? and order_time<? and uid = ? and human=0";
		Object[] params = { begin, curr, uid };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}
}
