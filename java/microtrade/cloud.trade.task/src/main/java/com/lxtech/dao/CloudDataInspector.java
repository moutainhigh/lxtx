package com.lxtech.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.lxtech.util.JdbcUtils;

public class CloudDataInspector {

	public static long getLatestDataTime(String sub) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataTestSource());
		String sql = "select dtime from `cloud_target_index_minute_gen` where `subject` = ? order by id desc limit 1";
		Object[] params = {sub};
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Date) map.get("dtime")).getTime();
	}
	
	public static void main(String[] args) throws SQLException {
		System.out.println("hey");
	}
	
}
