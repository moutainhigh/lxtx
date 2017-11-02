package com.lxtech.game.plaza.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.game.plaza.db.model.GameUser;

public class GUserHandler {

	public static GameUser queryById(long id) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from g_user where id = ?";
		Object params[] = { id };
		GameUser result = qr.query(sql, new BeanHandler<GameUser>(GameUser.class), params);
		return result;
	}

	
	public static void updatejBalanceById(GameUser user) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update g_user set balance = ?,carry_amount = ? where id = ?";
		Object params[] = {user.getBalance(),user.getCarry_amount(),user.getId()};
		qr.update(sql, params);
		
	}
}
