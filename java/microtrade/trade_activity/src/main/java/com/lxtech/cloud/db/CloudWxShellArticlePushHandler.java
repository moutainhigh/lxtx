package com.lxtech.cloud.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.cloud.db.model.CloudWxShellArticlePush;

public class CloudWxShellArticlePushHandler {
	public static CloudWxShellArticlePush selectByWxid(String wxid) throws SQLException {
		String sql = "select * from cloud_wx_shell_article_push where wxid=?";
		Object[] params = { wxid };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudWxShellArticlePush>(CloudWxShellArticlePush.class));
	}

	public static int updateById(CloudWxShellArticlePush push) throws SQLException {
		String sql = "update cloud_wx_shell_article_push set last_send_time=?, current_index = ? where id=?";
		Object params[] = { push.getLast_send_time(), push.getCurrent_index(), push.getId() };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.update(sql, params);
	}

	public static int insert(CloudWxShellArticlePush push) throws SQLException {
		String sql = "insert into cloud_wx_shell_article_push(wxid, last_send_time, current_index, chnno) values(?,?,?,?)";
		Object[] params = { push.getWxid(), push.getLast_send_time(), push.getCurrent_index(), push.getChnno() };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		int result = qr.update(sql, params);
		return result;
	}
}
