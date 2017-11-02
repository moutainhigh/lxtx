package com.lxtech.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.model.CloudTargetIndexMinute;
import com.lxtech.util.JdbcUtils;

public class CloudTargetIndexMinuteDao {

	public static List<CloudTargetIndexMinute> queryIndexsByIndex(String subject, int beginIndex, int endIndex,
			String beginTm, String endTm) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataTestSource());
		String sql = "select idx from cloud_target_index_minute where subject=? and timeindex>=? and timeindex<? and dtime>=? and dtime<? order by timeindex";
		Object params[] = { subject, beginIndex, endIndex, beginTm, endTm };
		return qr.query(sql, new BeanListHandler<>(CloudTargetIndexMinute.class), params);
	}

	public static List<CloudTargetIndexMinute> queryIndexsByIndexGen(String subject, int beginIndex, int endIndex,
			String beginTm, String endTm) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataTestSource());
		String sql = "select idx from cloud_target_index_minute_gen where subject=? and timeindex>=? and timeindex<? and dtime>=? and dtime<? order by timeindex";
		Object params[] = { subject, beginIndex, endIndex, beginTm, endTm };
		return qr.query(sql, new BeanListHandler<>(CloudTargetIndexMinute.class), params);
	}

	public static List<CloudTargetIndexMinute> selectLastData() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataTestSource());
		String sql = "select a.* from cloud_target_index_minute a inner join (select Max(id) as id from cloud_target_index_minute group by subject) b on a.id=b.id ";
		Object params[] = {};
		return qr.query(sql, new BeanListHandler<>(CloudTargetIndexMinute.class), params);
	}

}
