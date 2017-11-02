package com.lxtech.cloud.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.cloud.db.model.CloudUser;

public class CloudUserHandler {
	public static CloudUser getCloudUser(String user_id) throws SQLException {
		String sql = "select * from cloud_user where wxid = ?";

		Object[] params = { user_id };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudUser>(CloudUser.class));
	}
	public static CloudUser getCloudShellUser(String user_id) throws SQLException {
		String sql = "select * from cloud_wx_shell_user where wxid = ?";
		
		Object[] params = { user_id };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudUser>(CloudUser.class));
	}
	
	public static void main(String[] args) {
		try {
			CloudUser user = CloudUserHandler.getCloudUser("opPADwBmRX-6Uz9RsGCDs44Bb6Do");
			System.out.println(user.getHeadimgurl());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
