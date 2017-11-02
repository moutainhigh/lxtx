package com.lxtech.game.plaza.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.game.plaza.db.model.SettlementSysConfig;

public class SystemConfigHandler {
	public static SettlementSysConfig getConfigByName(String key) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from settlement_sys_config where `key` = ?";
		Object params[] = { key };
		SettlementSysConfig result = qr.query(sql, new BeanHandler<SettlementSysConfig>(SettlementSysConfig.class), params);
		return result;
	}
	
	public static void main(String[] args) {
		SettlementSysConfig config;
		try {
			config = getConfigByName("dice_master_balance_limit");
			System.out.println(config.getValue());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
