package com.lxtech.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.model.CloudWeixinShellArticlePush;
import com.lxtech.model.CloudWxShellArticle;
import com.lxtech.util.JdbcUtils;

public class CloudWeixinArticleDao {
	public static List<CloudWeixinShellArticlePush> getArticlePushList() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `cloud_wx_shell_article_push` where day = ? and status = 0";
		String day = new SimpleDateFormat("yyyyMMdd").format(new Date());
		List<CloudWeixinShellArticlePush> pushList = qr.query(sql, new Object[]{day}, new BeanListHandler<CloudWeixinShellArticlePush>(CloudWeixinShellArticlePush.class));
		return pushList;
	}
	
	public static int updateArticlePushStatus(int pushId) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update `cloud_wx_shell_article_push` set status = 1 where id = ?";
		return qr.update(sql, new Object[]{pushId});
	}
	
	public static CloudWxShellArticle getWeixinPushArticle(int article_id) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_wx_shell_article where id = ? ";
		return qr.query(sql, new Object[]{article_id}, new BeanHandler<CloudWxShellArticle>(CloudWxShellArticle.class));
	}
	
	public static void main(String[] args) throws SQLException {
//		List<CloudWeixinShellArticlePush> pushList = getShellList();
//		System.out.println(pushList.size());
//		System.out.println(updateArticlePushStatus(1));
		CloudWxShellArticle article = getWeixinPushArticle(1);
		System.out.println(article.getTitle());
	}
}
