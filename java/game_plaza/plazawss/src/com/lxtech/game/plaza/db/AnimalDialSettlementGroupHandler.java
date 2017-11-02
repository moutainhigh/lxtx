package com.lxtech.game.plaza.db;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.lxtech.game.plaza.db.model.AnimalDialSettlementGroup;
import com.lxtech.game.plaza.db.model.DiceSettlementGroup;

public class AnimalDialSettlementGroupHandler {

	public static AnimalDialSettlementGroup getSettlementGroup(int groupId) throws SQLException {
		String sql = "select * from animal_dial_settlement_group where id = ?";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, new BeanHandler<AnimalDialSettlementGroup>(AnimalDialSettlementGroup.class), groupId);
	}

	public static int updateGroupSettlement(AnimalDialSettlementGroup group) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update animal_dial_settlement_group set end_time=?, result_tiandi=?, result_wuxing=?, result_animal=?, state=?, banker_settlement_result=? where id=?";
		Object params[] = { group.getEnd_time(), group.getResult_tiandi(), group.getResult_wuxing(),
				group.getResult_animal(), group.getState(), group.getBanker_settlement_result(), group.getId() };
		return qr.update(sql, params);
	}

	public static int saveAnimalSettleGroup(AnimalDialSettlementGroup group) throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into `animal_dial_settlement_group`(banker_id, banker_carry_amount, start_time, state, `combined_two_tiandi`, `combined_two_animal`, `combined_three_tiandi`,"
				+ "`combined_three_wuxing`, `combined_three_animal`) values(?, ?, ?, ?, ?,?,?,?,?)";
		Object[] params = new Object[] { group.getBanker_id(), group.getBanker_carry_amount(), group.getStart_time(),
				group.getState(), group.getCombined_two_tiandi(), group.getCombined_two_animal(), group.getCombined_three_tiandi(),
				group.getCombined_three_wuxing(), group.getCombined_three_animal()};
		qr.update(sql, params);
		int id = (Integer) qr.query("SELECT max(id) from `animal_dial_settlement_group`", new ScalarHandler(1));
		return id;
	}

	public static AnimalDialSettlementGroup getLatestSettledRound() throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `animal_dial_settlement_group` where state = 2 order by id desc limit 1 ";
		return qr.query(sql, new BeanHandler<AnimalDialSettlementGroup>(AnimalDialSettlementGroup.class));
	}
	
	public static int getLastestRoundIdForAnimal(int animalIndex) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select id from animal_dial_settlement_group where result_animal = ? order by id desc limit 1";
		try {
			return qr.query(sql, new Object[]{animalIndex}, new ScalarHandler<Integer>());
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static int getLatestRoundIdForTdSx(int td, int sx) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select id from animal_dial_settlement_group where result_animal = ? and result_tiandi = ?";
		try {
			return qr.query(sql, new Object[]{sx, td}, new ScalarHandler<Integer>());
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static int getLatestRoundIdForTdWxSx(int td, int wx, int sx) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select id from animal_dial_settlement_group where result_tiandi = ? and result_wuxing = ? and result_animal = ?";
		try {
			return qr.query(sql, new Object[]{td, wx, sx}, new ScalarHandler<Integer>());
		} catch (Exception e) {
			return 0;
		}
	} 
	
	public static void main(String[] args) throws SQLException {
		/*AnimalDialSettlementGroup group = new AnimalDialSettlementGroup();
		group.setBanker_carry_amount(12000000l);
		group.setBanker_id(1);
		group.setCombined_three_animal(1);
		group.setCombined_three_tiandi(18);
		group.setCombined_three_wuxing(13);
		group.setCombined_two_animal(2);
		group.setState(0);
		group.setCombined_two_tiandi(19);
		
		System.out.println(saveAnimalSettleGroup(group));*/
//		System.out.println(AnimalDialSettlementGroupHandler.getLastestRoundIdForAnimal(11));
//		System.out.println(AnimalDialSettlementGroupHandler.getLastestRoundIdForAnimal(1));
//		System.out.println(AnimalDialSettlementGroupHandler.getLatestRoundIdForTdWxSx(18, 14, 10));
		System.out.println(queryPrevious().size());
	}

	public static List<AnimalDialSettlementGroup> queryPrevious() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `animal_dial_settlement_group` where state = 2 order by id desc limit 10";
		List<AnimalDialSettlementGroup> result = qr.query(sql,
				new BeanListHandler<AnimalDialSettlementGroup>(AnimalDialSettlementGroup.class));
		return result;
	}
	
	

}
