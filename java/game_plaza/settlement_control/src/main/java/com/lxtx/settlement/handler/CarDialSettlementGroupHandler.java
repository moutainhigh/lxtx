package com.lxtx.settlement.handler;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtx.settlement.dbmodel.CarDialSettlementGroup;
import com.lxtx.settlement.dbmodel.SystemSettlementResult;
import com.lxtx.settlement.utils.JdbcUtils;

public class CarDialSettlementGroupHandler {

	public static CarDialSettlementGroup getSettlementGroup(int groupId) throws SQLException {
		String sql = "select * from car_dial_settlement_group where id = ?";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, new BeanHandler<CarDialSettlementGroup>(CarDialSettlementGroup.class), groupId);
	}

	public static int updateGroupSettlement(CarDialSettlementGroup group) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update car_dial_settlement_group set end_time=?, result=?, result_flag=?, state=?, banker_settlement_result=?, system_settlement_result=? where id=?";
		Object params[] = { group.getEnd_time(), group.getResult(), group.getResult_flag(), group.getState(),
				group.getBanker_settlerment_result(), group.getSystem_settlement_result(), group.getId() };
		return qr.update(sql, params);
	}

	public static long selectSumSystemSettlementResult(long beginTimestamp, long endTimestamp) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select sum(system_settlement_result) as system_settlement_result from car_dial_settlement_group where UNIX_TIMESTAMP(end_time)>=?	and UNIX_TIMESTAMP(end_time)<?";
		Object params[] = { beginTimestamp / 1000, endTimestamp / 1000 };

		SystemSettlementResult r = qr.query(sql, new BeanHandler<SystemSettlementResult>(SystemSettlementResult.class),
				params);
		return (null == r || r.getSystem_settlement_result() == null) ? 0 : r.getSystem_settlement_result();
	}

	public static long selectNormalBankerSumSystemSettlementResult(long beginTimestamp, long endTimestamp)
			throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());

		String sql = "select sum(a.system_settlement_result) as system_settlement_result from car_dial_settlement_group a left join g_user b on a.banker_id=b.id "
				+ "where b.identity=0 and UNIX_TIMESTAMP(a.end_time)>=? and UNIX_TIMESTAMP(a.end_time)<?";
		Object params[] = { beginTimestamp / 1000, endTimestamp / 1000 };

		SystemSettlementResult r = qr.query(sql, new BeanHandler<SystemSettlementResult>(SystemSettlementResult.class),
				params);
		return (null == r || r.getSystem_settlement_result() == null) ? 0 : r.getSystem_settlement_result();
	}
}
