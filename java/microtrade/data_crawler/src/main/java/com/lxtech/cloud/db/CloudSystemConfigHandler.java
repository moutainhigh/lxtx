package com.lxtech.cloud.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.cloud.db.model.CloudSystemConfig;

public class CloudSystemConfigHandler {
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

	public static void main(String[] args) throws SQLException {
		System.out.println(readSystemConfig("ws.addr"));
	}

}
