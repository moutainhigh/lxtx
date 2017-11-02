package com.lxtech.cloud.db;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.db.model.CloudOrder;

public class CloudOrderHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(CloudOrderHandler.class);
	
	public static List<CloudOrder> getUnprocessedOrderList(String subject) throws SQLException {
		String sql = "select id, uid, subject, direction, money, order_index, status, clear_upper_limit, clear_down_limit"
				+ " from cloud_order where status = 0 and subject = ?";
		
		String[] params = {subject};
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		long time1 = System.currentTimeMillis();
		List<CloudOrder> orderList = qr.query(sql, params, new BeanListHandler<CloudOrder>(CloudOrder.class));
		logger.info("get unprocessed order list cost time:" + (System.currentTimeMillis() - time1)+" milli seconds.");
		return orderList;
	}
	
	public static void main(String[] args) {
		List<CloudOrder> orderList;
		try {
			orderList = CloudOrderHandler.getUnprocessedOrderList("BU");
			System.out.println(orderList.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
