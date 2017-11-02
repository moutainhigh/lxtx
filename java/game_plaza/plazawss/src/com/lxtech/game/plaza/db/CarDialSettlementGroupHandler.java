package com.lxtech.game.plaza.db;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.lxtech.game.plaza.db.model.CarDialSettlementGroup;

public class CarDialSettlementGroupHandler {

	public static CarDialSettlementGroup getSettlementGroup(int groupId) throws SQLException {
		String sql = "select * from car_dial_settlement_group where id = ?";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, new BeanHandler<CarDialSettlementGroup>(CarDialSettlementGroup.class), groupId);
	}

	public static List<CarDialSettlementGroup> queryPrevious() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from car_dial_settlement_group where state = 2 order by id desc limit 10";
		List<CarDialSettlementGroup> result = qr.query(sql,
				new BeanListHandler<CarDialSettlementGroup>(CarDialSettlementGroup.class));
		return result;
	}
	
	public static CarDialSettlementGroup getLatestSettledRound() throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `car_dial_settlement_group` where state = 2 order by id desc limit 1 ";
		return qr.query(sql, new BeanHandler<CarDialSettlementGroup>(CarDialSettlementGroup.class));
	}
	
	public static int saveCarSettleGroup(CarDialSettlementGroup group) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into `car_dial_settlement_group`(banker_id, banker_carry_amount, start_time, state) values(?, ?, ?, ?)";
		Object[] params = new Object[] { group.getBanker_id(), group.getBanker_carry_amount(), group.getStart_time(),
				group.getState() };
		qr.update(sql, params);
		int id = (Integer) qr.query("SELECT max(id) from `car_dial_settlement_group`", new ScalarHandler(1));
		return id;
	}	
	
	public static void main(String[] args) {
		try {
			CarDialSettlementGroup group = getLatestSettledRound();
			System.out.println(group.getBanker_settlement_result());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
