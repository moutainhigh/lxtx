package com.lxtx.settlement.handler;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtx.settlement.dbmodel.CarDialSettlementHistory;
import com.lxtx.settlement.dbmodel.DiceSettlementHistory;
import com.lxtx.settlement.utils.JdbcUtils;

public class CarDialSettlementHistoryHandler {

	public static List<CarDialSettlementHistory> querySettlementHistorys(int groupId) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from car_dial_settlement_history where group_id=?";
		List<CarDialSettlementHistory> result = qr.query(sql,
				new BeanListHandler<CarDialSettlementHistory>(CarDialSettlementHistory.class), groupId);
		return result;
	}

	public static int updateHistroySettlement(CarDialSettlementHistory history) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update car_dial_settlement_history set settlement_result=? where id=?";
		Object params[] = { history.getSettlement_result(), history.getId() };
		return qr.update(sql, params);
	}

	public static int updateLosedHistroySettlements(List<Integer> ids) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		StringBuilder sb = new StringBuilder();
		sb.append("update car_dial_settlement_history set settlement_result=0 where id in (");
		for (Integer id : ids) {
			sb.append(id).append(",");
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		String sql = sb.toString();
		return qr.update(sql);
	}
}
