package com.lxtech.game.plaza.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.game.plaza.db.model.DiceUserData;

public class DiceDataHandler {
	public static DiceUserData getUserDiceData(long uid) throws SQLException{
		String sql = "select * from g_user_dice_data where uid = ?";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, new Object[]{uid}, new BeanHandler<DiceUserData>(DiceUserData.class));
	}
	
	public static void main(String[] args) throws SQLException {
		DiceUserData diceData = getUserDiceData(2);
		System.out.println(diceData.getRelief_count());
	}
}
