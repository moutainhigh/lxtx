package com.lxtech.game.plaza.db;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.game.plaza.db.model.ResultStatistics;

public class ResultStatisticsHandler {
public static List<ResultStatistics> queryForAdd() throws SQLException{
	QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
	String sql = "select * from statistics_add order by number asc";
	List<ResultStatistics> result = qr.query(sql, new BeanListHandler<ResultStatistics>(ResultStatistics.class));
	return result;
}

public static List<ResultStatistics> queryForDouble() throws SQLException{
	QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
	String sql = "select * from statistics_double order by number asc";
	List<ResultStatistics> result = qr.query(sql, new BeanListHandler<ResultStatistics>(ResultStatistics.class));
	return result;
}

public static List<ResultStatistics> queryForTripple() throws SQLException{
	QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
	String sql = "select * from statistics_tripple order by number asc";
	List<ResultStatistics> result = qr.query(sql, new BeanListHandler<ResultStatistics>(ResultStatistics.class));
	return result;
}
}
