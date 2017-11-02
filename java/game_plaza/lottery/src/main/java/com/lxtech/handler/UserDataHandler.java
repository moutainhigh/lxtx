package com.lxtech.handler;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.lxtech.inspect.utils.JdbcUtils;

public class UserDataHandler {

	public static int addCarryAmount(int userId, long addAmonut) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update g_user set carry_amount=carry_amount+? where id=?";
		Object params[] = { addAmonut, userId };
		return qr.update(sql, params);
	}
}
