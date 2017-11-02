package com.lxtech.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.lxtech.model.CloudOpenClose;
import com.lxtech.util.JdbcUtils;

public class CloudOpenCloseDao {

	public static int updateBySubject(CloudOpenClose cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataTestSource());
		String sql = "update cloud_open_close set open = ?, high = ?, low =?, last_close = ? where subject = ?";
		Object params[] = { cc.getOpen(), cc.getHigh(), cc.getLow(), cc.getLastClose(), cc.getSubject() };
		return qr.update(sql, params);
	}

	public static void insertSelective(CloudOpenClose cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataTestSource());
		String sql = "insert into cloud_open_close(subject, open, high, low, last_close) values(?,?,?,?,?)";
		Object params[] = { cc.getSubject(), cc.getOpen(), cc.getHigh(), cc.getLow(), cc.getLastClose() };
		qr.update(sql, params);
	}
}
