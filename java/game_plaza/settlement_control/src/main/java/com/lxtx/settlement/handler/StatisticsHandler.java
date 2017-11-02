package com.lxtx.settlement.handler;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.settlement.utils.JdbcUtils;

public class StatisticsHandler {
	private final static Logger logger = LoggerFactory.getLogger(StatisticsHandler.class);

	public static int updateTotal(int num, int groupId) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update statistics_add set last_index=? where number=?";
		Object params[] = { groupId, num };
		return qr.update(sql, params);
	}

	public static int updateDouble(int num, int groupId) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update statistics_double set last_index=? where number=?";
		Object params[] = { groupId, num };
		return qr.update(sql, params);
	}

	public static int updateTripple(int num, int groupId) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update statistics_tripple set last_index=? where number=?";
		Object params[] = { groupId, num };
		return qr.update(sql, params);
	}
}
