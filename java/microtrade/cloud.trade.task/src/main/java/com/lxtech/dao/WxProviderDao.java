package com.lxtech.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import com.lxtech.model.CloudWxServiceProvider;
import com.lxtech.util.JdbcUtils;

public class WxProviderDao {
	public static CloudWxServiceProvider queryActiveProvider() throws SQLException {
		
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_wx_service_provider where is_active = 1 limit 1";
		return qr.query(sql, new BeanHandler<CloudWxServiceProvider>(CloudWxServiceProvider.class));
	}

	public static void main(String[] args) {
		try {
			CloudWxServiceProvider d = WxProviderDao.queryActiveProvider();
			System.out.println(d.getApp_id());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
