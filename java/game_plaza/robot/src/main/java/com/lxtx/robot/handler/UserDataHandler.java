package com.lxtx.robot.handler;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.robot.dbmodel.UserData;
import com.lxtx.robot.utils.JdbcUtils;

public class UserDataHandler {
	private final static Logger logger = LoggerFactory.getLogger(UserDataHandler.class);

	/*
	 * public static int insert(LotteryCqsscData lcd) throws SQLException {
	 * QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource()); String sql =
	 * "insert into lottery_cqssc_data (id,date,serial_number,open_code,open_time) values(?,?,?,?,?)"
	 * ; Object params[] = { lcd.getId(), lcd.getDate(), lcd.getSerial_number(),
	 * lcd.getOpen_code(), lcd.getOpen_time() }; return qr.update(sql, params);
	 * }
	 */
	public static List<UserData> queryUserData(Collection<Integer> userIds) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		StringBuilder sql = new StringBuilder("select id,wxnm,balance,identity,carry_amount from g_user where id in(");
		for (Integer id : userIds) {
			sql.append(id).append(",");
		}
		sql.setLength(sql.length() - 1);
		sql.append(")");

		return qr.query(sql.toString(), new BeanListHandler<UserData>(UserData.class));
	}

	public static List<UserData> queryRobots() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select id,wxnm,balance,identity,carry_amount from g_user where identity = 1";
		return qr.query(sql, new BeanListHandler<UserData>(UserData.class));
	}

	public static int updateCarryAmount(UserData ud) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update g_user set carry_amount=? where id=?";
		Object params[] = { ud.getCarry_amount(), ud.getId() };
		return qr.update(sql, params);
	}
}
