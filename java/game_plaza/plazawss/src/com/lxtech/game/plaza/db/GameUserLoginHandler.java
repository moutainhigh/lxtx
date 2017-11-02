package com.lxtech.game.plaza.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.db.model.GameUserLogin;
import com.lxtech.game.plaza.net.NetConstants;
import com.lxtech.game.plaza.protocol.impl.DicePacketHandler;
import com.lxtech.game.plaza.util.TimeUtil;

/**
 * get user information from g_user and g_user_login
 * @author wangwei
 */
public class GameUserLoginHandler {

	private static final Logger logger = LoggerFactory.getLogger(GameUserLoginHandler.class);
	
	public static GameUserLogin getUserLogin(String cookie) throws SQLException{
		String sql = "select * from g_user_login where cookie = ?";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, new Object[]{cookie}, new BeanHandler<GameUserLogin>(GameUserLogin.class));
	}
	
	public static GameUser getGameUser(long uid) throws SQLException{
		String sql = "select * from g_user where id = ?";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, new Object[]{uid}, new BeanHandler<GameUser>(GameUser.class));
	}
	
	public static int updateUserChips(long uid, long chipcnt) throws SQLException  {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update g_user set carry_amount = ? where id = ?";
		return qr.update(sql, new Object[]{chipcnt, uid});
	}
	
	public static GameUser saveGameUser(String name, double balance, String mobile, int identity, long carry_amount) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into g_user(wxnm, balance, mobile, crt_tm, identity, carry_amount) values(?,?,?,?,?,?)";
		Object[] params = new Object[]{name, balance, mobile, new Date(), identity, carry_amount};
		return qr.insert(sql, new BeanHandler<GameUser>(GameUser.class), params);
	}
	
	public static int saveReliefRecord(long uid, long chipCount) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into g_dice_relief(uid, time, count, day) values(?, ?, ?, ?)";
		Object[] params = new Object[]{uid, new Timestamp(System.currentTimeMillis()), chipCount, TimeUtil.getDayStr(new Date())};
		return qr.update(sql, params);
	}
	
	public static long getReliefCountByDay(long uid, Date d) throws SQLException {
		String day = TimeUtil.getDayStr(d);
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(*) from g_dice_relief where uid = ? and day = ?";
		Object[] params = new Object[]{uid, day};
		return qr.query(sql, params, new ScalarHandler<Long>());
	}
	
	public static long getReliefCount(long uid) throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select count(*) from g_dice_relief where uid = ?";
		return qr.query(sql, new Object[]{uid}, new ScalarHandler<Long>());
	}
	
	/**
	 * update user status
	 * @param status
	 * @param gameId
	 * @param uid
	 * @throws SQLException
	 */
	public static void updateUserStatus(int status, int gameId, long uid) {
		try {
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "update `g_user` set is_subscribe = ?, broker_id = ? where id = ?";
			qr.update(sql, new Object[]{status, gameId, uid});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void setUserAsMaster(int gameId, long uid){
		try {
			updateUserStatus(NetConstants.GAME_STATUS_MASTER, gameId, uid);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void setUserAsMasterInQueue(int gameId, long uid) {
		try {
			updateUserStatus(NetConstants.GAME_STATUS_IN_QUEUE, gameId, uid);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void setUserAsPlayer(long uid){
		try {
			updateUserStatus(NetConstants.GAME_STATUS_PLAYER, 0, uid);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		GameUserLoginHandler.updateUserStatus(NetConstants.GAME_STATUS_MASTER, NetConstants.GAME_ROOM_DICE, 2);
	}
}
