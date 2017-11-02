package com.lxtech.cloud.db;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.lxtech.cloud.db.model.CloudWxShellArticle;

public class CloudWxShellArticleHandler {

	public static int getCount() throws SQLException {
		String sql = "select count(1) as count from cloud_wx_shell_article";
		Object[] params = {};
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Long) map.get("count")).intValue();
	}

	public static CloudWxShellArticle selectById(int index) throws SQLException {
		String sql = "select * from cloud_wx_shell_article where id=?";

		Object[] params = { index };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudWxShellArticle>(CloudWxShellArticle.class));
	}
}
