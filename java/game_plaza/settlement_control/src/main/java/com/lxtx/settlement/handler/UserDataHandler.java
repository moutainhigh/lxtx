package com.lxtx.settlement.handler;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtx.settlement.dbmodel.UserData;
import com.lxtx.settlement.utils.JdbcUtils;

public class UserDataHandler {

	public static int addCarryAmount(int userId, long addAmonut) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update g_user set carry_amount=carry_amount+? where id=?";
		Object params[] = { addAmonut, userId };
		return qr.update(sql, params);
	}
	public static List<UserData> queryRobots() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select id,wxnm,balance,identity,carry_amount from g_user where identity = 1";
		return qr.query(sql, new BeanListHandler<UserData>(UserData.class));
	}
}
