package com.lxtech.dao;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.lxtech.util.JdbcUtils;

public class CloudFundHistoryDao {

	public static void main(String[] args) throws SQLException {
		System.out.println(getFillLimitCount("2016-12-12 00:30:26", "2016-12-14 00:45:26"));
	}

	public static int getFillData(String begin, String curr) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as count from `cloud_fund_history` where time>=? and time<? and type = 1";
		Object[] params = { begin, curr };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}

	public static int getFillFailedData(String begin, String curr) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as count from `cloud_fund_history` where time>=? and time<? and type = 1 and status =0";
		Object[] params = { begin, curr };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}

	public static int getRepayLimitCount(String begin, String curr) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as count from `cloud_fund_history` where time>=? and time<? and type = 2 and status = 1 and amount>=1000";
		Object[] params = { begin, curr };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}

	public static int getFillLimitCount(String begin, String curr) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(1) as count from `cloud_fund_history` where time>=? and time<? and type = 1 and status = 1 and amount>=3000";
		Object[] params = { begin, curr };
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}
}
