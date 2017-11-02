package com.lxtech.cloud.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.google.common.base.Splitter;
import com.lxtech.cloud.db.model.CloudTradeException;

public class CloudTradeExceptionHandler {
	public static CloudTradeException retrieveTradeExceptionByDay(long day) throws SQLException {
		String sql = "select * from cloud_trade_exception_day where `day` = ?";

		Object[] params = { day };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		CloudTradeException exc = qr.query(sql, params,
				new BeanHandler<CloudTradeException>(CloudTradeException.class));
		if (exc != null) {
			String hours = exc.getHours();
			Iterable<String> iter = Splitter.on(",").split(hours);
			List<Integer> list = new ArrayList();
			for (String hour : iter) {
				list.add(Integer.valueOf(hour));
			}
			exc.setHourList(list);
			return exc;
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		try {
			CloudTradeException exc = CloudTradeExceptionHandler.retrieveTradeExceptionByDay(20161125);
			List<Integer> hourList = exc.getHourList();
			for (Integer hour:hourList) {
				System.out.println(hour);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
