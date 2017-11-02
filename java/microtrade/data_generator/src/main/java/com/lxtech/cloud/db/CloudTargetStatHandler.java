package com.lxtech.cloud.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.cloud.db.model.CloudTargetIndexStat;

public class CloudTargetStatHandler {
	public static CloudTargetIndexStat retrieveTargetStat(String subject) throws SQLException {
		String sql = "select * from cloud_open_close where subject = ?";

		String[] params = { subject };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudTargetIndexStat>(CloudTargetIndexStat.class));
	}
	
	public static int updateTargetTopIndex(String subject, double newIndex) throws SQLException {
		return updateTargetIndex(subject, "high", newIndex);
	}
	
	public static int updateTargetLowIndex(String subject, double newIndex) throws SQLException {
		return updateTargetIndex(subject, "low", newIndex);
	}
	
	public static int updateTargetOpenIndex(String subject, double newIndex) throws SQLException {
		return updateTargetIndex(subject, "open", newIndex);
	}	
	
	public static int updateTargetIndex(String subject, String field, double newIndex) throws SQLException {
		String sql = "update cloud_open_close set " + field + " = ? where subject = ?";
		Object[] params = {newIndex, subject};
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.update(sql, params);
	}

	public static void main(String[] args) throws SQLException {
		CloudTargetIndexStat stat = CloudTargetStatHandler.retrieveTargetStat("BU");
		System.out.println(stat.getLast_close());
	}
}
