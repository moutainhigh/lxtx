package com.lxtech.cloudtrade.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.cloudtrade.db.model.CloudTargetIndexStat;


public class CloudTargetStatHandler {
	public static Map<String, CloudTargetIndexStat> retrieveTargetStats() throws SQLException {
		String sql = "select * from cloud_open_close";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<CloudTargetIndexStat> statList = qr.query(sql, new BeanListHandler<CloudTargetIndexStat>(CloudTargetIndexStat.class));
		Map<String, CloudTargetIndexStat> statMap = new HashMap<String, CloudTargetIndexStat>();
		for (CloudTargetIndexStat stat : statList) {
			statMap.put(stat.getSubject(), stat);
		}
		return statMap;
	}

	public static void main(String[] args) throws SQLException {
		Map<String, CloudTargetIndexStat> statMap = CloudTargetStatHandler.retrieveTargetStats();
		Set<String> keySet = statMap.keySet();
		for (String key: keySet) {
			System.out.println(key);	
		}
	}
}
