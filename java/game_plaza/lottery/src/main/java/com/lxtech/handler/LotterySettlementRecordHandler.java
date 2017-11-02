package com.lxtech.handler;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dbmodel.LotterySettlementRecord;
import com.lxtech.inspect.utils.JdbcUtils;

public class LotterySettlementRecordHandler {
	private final static Logger logger = LoggerFactory.getLogger(LotterySettlementRecordHandler.class);

	public static int insert(LotterySettlementRecord lsr) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into lottery_settlement_history (data_date, data_sn, data_open_code, operator_time, order_count, win_count, lose_count, order_money, settlement_money) values(?,?,?,?,?,?,?,?,?)";
		Object params[] = { lsr.getData_date(), lsr.getData_sn(), lsr.getData_open_code(), lsr.getOperator_time(), lsr.getOrder_count(), lsr.getWin_count(),
				lsr.getLose_count(), lsr.getOrder_money(), lsr.getSettlement_money() };
		return qr.update(sql, params);
	}
}
