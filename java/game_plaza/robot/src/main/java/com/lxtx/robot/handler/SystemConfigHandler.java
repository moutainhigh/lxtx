package com.lxtx.robot.handler;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtx.robot.dbmodel.SystemConfigData;
import com.lxtx.robot.utils.JdbcUtils;


public class SystemConfigHandler {

	public static SystemConfigData queryByKey(String key) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "SELECT * from robot_sys_config where `key`=?";
		Object params[] = { key };
		SystemConfigData result = qr.query(sql, new BeanHandler<SystemConfigData>(SystemConfigData.class), params);
		return result;
	}
}
