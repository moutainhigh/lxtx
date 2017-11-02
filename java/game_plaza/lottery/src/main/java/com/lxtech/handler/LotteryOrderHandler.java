package com.lxtech.handler;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dbmodel.LotteryOrder;
import com.lxtech.dbmodel.LotteryOrder.LotteryOrderState;
import com.lxtech.inspect.utils.JdbcUtils;

public class LotteryOrderHandler {
	private final static Logger logger = LoggerFactory.getLogger(LotteryOrderHandler.class);

	public static int updateSettlementResult(LotteryOrder lo) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update lottery_order set state=?, settlement_time=?, settlement_result=?, announcement = ? where id=?";
		Object params[] = { lo.getState(), lo.getSettlement_time(), lo.getSettlement_result(), lo.getAnnouncement(), lo.getId()};
		return qr.update(sql, params);
	}

	public static List<LotteryOrder> query(int date, int sn, LotteryOrderState state) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from lottery_order where direct_date=? and direct_sn=? and state=?";
		Object params[] = { date, sn, state.ordinal() };
		List<LotteryOrder> result = qr.query(sql, new BeanListHandler<LotteryOrder>(LotteryOrder.class), params);
		return result;
	}

}
