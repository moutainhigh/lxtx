package com.lxtx.settlement.handler;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtx.settlement.dbmodel.SystemConfigData;
import com.lxtx.settlement.utils.JdbcUtils;

public class SystemConfigHandler {

	public static SystemConfigData queryByKey(String key) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "SELECT * from settlement_sys_config where `key`=?";
		Object params[] = { key };
		SystemConfigData result = qr.query(sql, new BeanHandler<SystemConfigData>(SystemConfigData.class), params);
		return result;
	}

	public static List<SystemConfigData> queryAll() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "SELECT * from settlement_sys_config";
		List<SystemConfigData> result = qr.query(sql, new BeanListHandler<SystemConfigData>(SystemConfigData.class));
		return result;
	}
}
