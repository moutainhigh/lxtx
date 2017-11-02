package com.lxtech.game.plaza.db;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.game.plaza.db.model.AnimalDialSettlementHistory;
import com.lxtech.game.plaza.db.model.DiceSettlementResult;
import com.lxtech.game.plaza.db.model.PersonalChipsetStat;

public class AnimalDialSettlementHistoryHandler {

	public static List<AnimalDialSettlementHistory> querySettlementHistorys(int groupId) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from animal_dial_settlement_history where group_id=?";
		List<AnimalDialSettlementHistory> result = qr.query(sql,
				new BeanListHandler<AnimalDialSettlementHistory>(AnimalDialSettlementHistory.class), groupId);
		return result;
	}

	public static int updateHistroySettlement(AnimalDialSettlementHistory history) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update animal_dial_settlement_history set settlement_result=? where id=?";
		Object params[] = { history.getSettlement_result(), history.getId() };
		return qr.update(sql, params);
	}
	
	public static List<DiceSettlementResult> filterHistoryByRoundId(long roundId) throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select a.*, b.identity from (select user_id, sum(amount) as setted_num, sum(settlement_result) as win_num from `animal_dial_settlement_history` where group_id = ? group by user_id) a left join `g_user` b on a.user_id = b.id";
		return qr.query(sql, new Object[]{roundId}, new BeanListHandler<DiceSettlementResult>(DiceSettlementResult.class));
	}

	public static Map getChipStat(long roundNo, long uid) throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select sum(amount) as sum, target from `animal_dial_settlement_history` where group_id = ? and user_id = ? group by target";
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		List<PersonalChipsetStat> statList = qr.query(sql, new Object[] { roundNo, uid },
				new BeanListHandler<PersonalChipsetStat>(PersonalChipsetStat.class));

		for (int i = 0; i < 21; i++) {
			map.put(i, 0l);
		}
		
		for (PersonalChipsetStat stat : statList) {
			map.put(stat.getTarget() - 1, stat.getSum());
		}
		return map;
	}

	public static void insert(AnimalDialSettlementHistory ash) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into animal_dial_settlement_history (user_id,group_id,amount,target,create_time) values (?,?,?,?,?)";
		Object params[] = { ash.getUser_id(), ash.getGroup_id(), ash.getAmount(), ash.getTarget(),
				ash.getCreate_time() };
		qr.update(sql, params);
	}
	
	public static void main(String[] args) {
		AnimalDialSettlementHistory ash = new AnimalDialSettlementHistory();
		ash.setAmount(1000);
		ash.setCreate_time(new Date());
		ash.setGroup_id(3);
		ash.setTarget(12);
		ash.setUser_id(2);
		try {
			insert(ash);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
