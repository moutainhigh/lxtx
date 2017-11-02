package com.lxtech.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.lxtech.model.CloudKPeriod;
import com.lxtech.util.JdbcUtils;

public class CloudKPeriodDao {

	public static void saveData(CloudKPeriod cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataTestSource());
		String sql = "insert into cloud_k_period (subject, `time`,`open`, high, low, `close`,`type`)values (?,?,?,?,?,?,?)";
		Object params[] = { cc.getSubject(), cc.getTime(), cc.getOpen(), cc.getHigh(), cc.getLow(), cc.getClose(),
				cc.getType() };
		qr.update(sql, params);
	}

	public static void saveDataGen(CloudKPeriod cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataTestSource());
		String sql = "insert into cloud_k_period_gen (subject, `time`,`open`, high, low, `close`,`type`)values (?,?,?,?,?,?,?)";
		Object params[] = { cc.getSubject(), cc.getTime(), cc.getOpen(), cc.getHigh(), cc.getLow(), cc.getClose(),
				cc.getType() };
		qr.update(sql, params);
	}

}
