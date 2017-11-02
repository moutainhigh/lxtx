package com.lxtech.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.lxtech.model.CloudDayCensus;
import com.lxtech.util.JdbcUtils;

public class CloudDayCensusDao {
	public static int saveDayCensusData(CloudDayCensus cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into channel_cost(channelid, day, income) values(?,?,?)";
		Object params[] = {};
		return qr.update(sql, params);
	}
}
