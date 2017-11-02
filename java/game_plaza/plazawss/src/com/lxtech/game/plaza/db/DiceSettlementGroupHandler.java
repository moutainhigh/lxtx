package com.lxtech.game.plaza.db;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.lxtech.game.plaza.db.model.DiceSettlementGroup;
import com.lxtech.game.plaza.db.model.DiceSettlementHistory;

public class DiceSettlementGroupHandler {
	public static List<DiceSettlementGroup> queryPrevious() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from dice_settlement_group where state = 2 order by id desc limit 10";
		List<DiceSettlementGroup> result = qr.query(sql,
				new BeanListHandler<DiceSettlementGroup>(DiceSettlementGroup.class));
		return result;
	}

	public static int saveDiceSettleGroup(DiceSettlementGroup group) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into `dice_settlement_group`(banker_id, banker_carry_amount, start_time, state) values(?, ?, ?, ?)";
		Object[] params = new Object[] { group.getBanker_id(), group.getBanker_carry_amount(), group.getStart_time(),
				group.getState() };
		qr.update(sql, params);
		int id = (Integer) qr.query("SELECT max(id) from `dice_settlement_group`", new ScalarHandler(1));
		System.out.println(id);
		return id;
	}

	public static int saveDiceRecord(DiceSettlementHistory record) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into `dice_settlement_history`(user_id, group_id, amount, target, create_time) values(?,?,?,?,?)";
		return qr.update(sql, new Object[] { record.getUser_id(), record.getGroup_id(), record.getAmount(),
				record.getTarget(), new Date() });
	}
	
	public static DiceSettlementGroup getDiceSettlementGroup(long groupId) throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `dice_settlement_group` where id = ? ";
		return qr.query(sql, new Object[]{groupId}, new BeanHandler<DiceSettlementGroup>(DiceSettlementGroup.class));
	}
	
	public static DiceSettlementGroup getLatestSettledRound() throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `dice_settlement_group` where state = 2 order by id desc limit 1 ";
		return qr.query(sql, new BeanHandler<DiceSettlementGroup>(DiceSettlementGroup.class));
	}

	public static void main(String[] args) throws SQLException {
		/*
		 * DiceSettlementGroup group = new DiceSettlementGroup();
		 * group.setBanker_id(123); group.setStart_time(new Date());
		 * group.setBanker_carry_amount(19000000l); group.setState(0); int
		 * groupid = DiceSettlementGroupHandler.saveDiceSettleGroup(group);
		 * System.out.println(groupid);
		 */

		/*DiceSettlementHistory history = new DiceSettlementHistory();
		history.setAmount(5000);
		history.setGroup_id(12);
		history.setTarget(1);
		history.setUser_id(123);
		DiceSettlementGroupHandler.saveDiceRecord(history);*/
		DiceSettlementGroupHandler.getDiceSettlementGroup(36);
	}
}
