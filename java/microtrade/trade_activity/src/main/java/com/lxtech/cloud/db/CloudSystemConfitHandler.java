package com.lxtech.cloud.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.cloud.db.model.CloudSystemConfig;

public class CloudSystemConfitHandler {
	public static CloudSystemConfig selectByProperty(String property) throws SQLException {
		String sql = "select * from cloud_system_config where property=? limit 1";

		Object[] params = { property };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudSystemConfig>(CloudSystemConfig.class));
	}
}
