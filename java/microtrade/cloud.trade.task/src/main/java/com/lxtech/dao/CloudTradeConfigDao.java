package com.lxtech.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.model.CloudSystemConfig;
import com.lxtech.util.JdbcUtils;


public class CloudTradeConfigDao {
	public static String readSystemConfig(String name) throws SQLException {
		String sql = "select * from cloud_system_config where property = ?";
		Object[] params = { name };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		//return qr.update(sql, params);
		CloudSystemConfig config = qr.query(sql, params, new BeanHandler<CloudSystemConfig>(CloudSystemConfig.class));
		if (config != null) {
			return config.getValue();
		} else {
			return "";
		}
	}
	
	public static int updateSystemConfig(String property, String value) throws SQLException {
		String sql = "update cloud_system_config set `value` = ? where `property` = ? ";
		Object[] params = { value, property };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.update(sql, params);
	}
	
	public static void main(String[] args) throws SQLException {
		//CloudSystemConfigDao.updateSystemConfig("test", "value2");
		System.out.println(CloudTradeConfigDao.readSystemConfig("human.channels"));
	}
}
