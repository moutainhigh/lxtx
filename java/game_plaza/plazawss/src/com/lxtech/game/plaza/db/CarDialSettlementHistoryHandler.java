package com.lxtech.game.plaza.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.game.plaza.db.model.CarDialSettlementHistory;
import com.lxtech.game.plaza.db.model.DiceSettlementResult;
import com.lxtech.game.plaza.db.model.PersonalChipsetStat;

public class CarDialSettlementHistoryHandler {
	public static List<CarDialSettlementHistory> querySettlementHistorys(int groupId) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from car_dial_settlement_history where group_id=?";
		List<CarDialSettlementHistory> result = qr.query(sql,
				new BeanListHandler<CarDialSettlementHistory>(CarDialSettlementHistory.class), groupId);
		return result;
	}

	public static void insert(CarDialSettlementHistory dsh) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into car_dial_settlement_history (user_id,group_id,amount,target,create_time) values (?,?,?,?,?)";
		Object params[] = { dsh.getUser_id(), dsh.getGroup_id(), dsh.getAmount(), dsh.getTarget(),
				dsh.getCreate_time() };
		qr.update(sql, params);
	}
	
	public static List<DiceSettlementResult> filterHistoryByRoundId(long roundId) throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select a.*, b.identity from (select user_id, sum(amount) as setted_num, sum(settlement_result) as win_num from `car_dial_settlement_history` where group_id = ? group by user_id) a left join `g_user` b on a.user_id = b.id";
		return qr.query(sql, new Object[]{roundId}, new BeanListHandler<DiceSettlementResult>(DiceSettlementResult.class));
	}
	
	public static Map<Integer, Long> getChipStat(long roundId, long uid) throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select sum(amount) as sum, target from `car_dial_settlement_history` where group_id = ? and user_id = ? group by target";
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		List<PersonalChipsetStat> statList = qr.query(sql, new Object[] { roundId, uid },
				new BeanListHandler<PersonalChipsetStat>(PersonalChipsetStat.class));

		for (int i = 0; i < 8; i++) {
			map.put(i, 0l);
		}
		
		for (PersonalChipsetStat stat : statList) {
			map.put(stat.getTarget() - 1, stat.getSum());
		}
		return map;
	}	
}
