package com.lxtx.util.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.lxtx.model.SendFlowInfo;

public class SendFlowInfoHandler {

	public static int saveSendFlowInfo(SendFlowInfo cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getStatsDataSource());
		String sql = "insert into send_flow_info (mobile, `time`, `status`) values(?,?,?)";
		Object params[] = { cc.getMobile(), cc.getTime(), cc.getStatus() };
		return qr.update(sql, params);
	}
}
