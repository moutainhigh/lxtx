package com.lxtech.cloud.db;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.db.model.CloudOrder;

public class CloudOrderHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(CloudOrderHandler.class);
	
	public static long getUnprocessedOrderCount(String subject, int minCnt) throws SQLException {
		Date d = new Date(System.currentTimeMillis() - minCnt * 60 * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String sql = "select count(*) from cloud_order where status = 0 and subject = ? and order_time < ?";
		String[] params = {subject, sdf.format(d)}; 
		QueryRunner qr = new QueryRunner(JdbcUtils.getTradeDataSource());
		Long cnt = qr.query(sql, params, new ScalarHandler<Long>());
		logger.info("list orders before " + sdf.format(d) + " count :" + cnt);
		return cnt.longValue();
	}
	
	public static List<CloudOrder> getUnprocessedOrderList(String subject) throws SQLException {
		String sql = "select id, uid, subject, direction, contract_money, order_index, status, clear_upper_limit, clear_down_limit, commission, `limit`"
				+ " from cloud_order where status = 4 and subject = ?";
		
		String[] params = {subject};
		QueryRunner qr = new QueryRunner(JdbcUtils.getTradeDataSource());
		long time1 = System.currentTimeMillis();
		List<CloudOrder> orderList = qr.query(sql, params, new BeanListHandler<CloudOrder>(CloudOrder.class));
		logger.info("get unprocessed order list cost time:" + (System.currentTimeMillis() - time1)+" milli seconds.");
		return orderList;
	}
	
	public static int markOrderProcessed(String subject, double indexValue) throws SQLException {
		String sql = "update cloud_order set status = 1, clear_index = ? where (clear_upper_limit <= ? or clear_down_limit >= ?) and `subject` = ? and `status` = 0 ";
		Object[] params = {indexValue, indexValue, indexValue, subject};
		QueryRunner qr = new QueryRunner(JdbcUtils.getTradeDataSource());
		return qr.update(sql, params);
	}
	
	public static void main(String[] args) throws SQLException{
		/*List<CloudOrder> orderList;
		try {
			orderList = CloudOrderHandler.getUnprocessedOrderList("BU");
			System.out.println(orderList.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		long time1 = System.currentTimeMillis();
		System.out.println(CloudOrderHandler.getUnprocessedOrderCount("BTC", 10));
		System.out.println(System.currentTimeMillis() - time1);
		System.out.println(CloudOrderHandler.getUnprocessedOrderCount("BTC", 10));
		System.out.println(System.currentTimeMillis() - time1);
	}
}
