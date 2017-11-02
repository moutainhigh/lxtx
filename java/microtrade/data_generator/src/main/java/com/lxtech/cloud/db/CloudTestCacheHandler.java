package com.lxtech.cloud.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.google.common.base.Strings;
import com.lxtech.cloud.db.model.CloudTestCache;

public class CloudTestCacheHandler {
	public static CloudTestCache retrieveCache() throws SQLException {
		String sql = "select * from cloud_test_cache limit 1";

		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, new BeanHandler<CloudTestCache>(CloudTestCache.class));
	}
	
	
	public static int updateCache(String key, long value) throws SQLException {
		if (Strings.isNullOrEmpty(key) || (!key.equals("top") && !key.equals("bottom"))) {
			return -1;
		}
		String sql = "";
		if (key.equals("top")) {
			sql = "update cloud_test_cache set top = ?";
		} else {
			sql = "update cloud_test_cache set bottom = ?";
		}
		Object[] params = {value};
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.update(sql, params);
	}
	
	public static void main(String[] args) throws SQLException {
//		CloudTestCache cache = CloudTestCacheHandler.retrieveCache();
//		System.out.println(cache.getBottom());
		CloudTestCacheHandler.updateCache("top", 1000);
	}
}
