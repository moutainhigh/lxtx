package com.lxtx.settlement.handler;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtx.settlement.dbmodel.StatisticsSettlement;
import com.lxtx.settlement.dbmodel.SystemSettlementResult;
import com.lxtx.settlement.utils.JdbcUtils;

public class StatisticsSettlementHandler {

	public static int insertDiceStatisticsSettlement(StatisticsSettlement statisticsSettlement) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "INSERT into dice_statistics_settlement (group_id, user_id, result, create_time) values (?, ?, ?, ?)";
		Object[] params = new Object[] { statisticsSettlement.getGroup_id(), statisticsSettlement.getUser_id(),
				statisticsSettlement.getResult(), statisticsSettlement.getCreate_time() };
		return qr.update(sql, params);
	}

	public static int insertAnimalDialStatisticsSettlement(StatisticsSettlement statisticsSettlement)
			throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "INSERT into animal_dial_statistics_settlement (group_id, user_id, result, create_time) values (?, ?, ?, ?)";
		Object[] params = new Object[] { statisticsSettlement.getGroup_id(), statisticsSettlement.getUser_id(),
				statisticsSettlement.getResult(), statisticsSettlement.getCreate_time() };
		return qr.update(sql, params);
	}

	public static int insertCarDialStatisticsSettlement(StatisticsSettlement statisticsSettlement) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "INSERT into car_dial_statistics_settlement (group_id, user_id, result, create_time) values (?, ?, ?, ?)";
		Object[] params = new Object[] { statisticsSettlement.getGroup_id(), statisticsSettlement.getUser_id(),
				statisticsSettlement.getResult(), statisticsSettlement.getCreate_time() };
		return qr.update(sql, params);
	}

}
