package com.lxtx.util.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/**
 * Operations for updating the generated target table and query the original
 * target table
 * 
 * @author wangwei
 *
 */
public class RedPacketUserHandler {

	public static Integer getGameUIdByWxid(String wxid) throws SQLException {
		try {
			String sql = "select gameUserId from pub_user where wxOpenId = ?";
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			return qr.query(sql, new ScalarHandler<Integer>(), new Object[]{wxid});					
		} catch (NullPointerException e) {
			return 0;
		}

	}
	
	public static int updateCloudTargetIndex(Double moneyAdd, long userId) throws SQLException {
		String sql = "update pub_user set money = money + ? where gameUserId = ?";
		Object[] params = { moneyAdd, userId };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.update(sql, params);
	}
	
	public static int reduceUserBalance(Double moneyToReduce, String openId) throws SQLException {
		String sql = "update pub_user set money = money - ? where wxOpenId = ?";
		Object[] params = { moneyToReduce, openId};
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.update(sql, params);
	}
	
	public static double queryBalance(String openId) throws SQLException {
		try {
			String sql = "select money from pub_user where wxOpenId = ?";
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			return qr.query(sql, new ScalarHandler<Double>(), new Object[]{openId});			
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public static void main(String[] args) throws SQLException {
//		RedPacketUserHandler.updateCloudTargetIndex(10d, 2);
//		System.out.println(RedPacketUserHandler.queryBalance("oDfCIw6s8okTYfeYLVGDwyY9rC58aa"));
//		System.out.println(RedPacketUserHandler.reduceUserBalance(20.0d, "oDfCIw6s8okTYfeYLVGDwyY9rC58"));
		System.out.println(RedPacketUserHandler.getGameUIdByWxid("oDfCIw6s8okTYfeYLVGDwyY9rC58s"));
	}
}
