package com.lxtech.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.model.CloudTarget;
import com.lxtech.util.JdbcUtils;

public class CloudTargetDao {
	public static List<CloudTarget> queryTarget() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_target";
		Object params[] = {};
		return qr.query(sql, new BeanListHandler<CloudTarget>(CloudTarget.class), params);
	}

	public static void main(String[] args) {
		try {
			System.out.println(queryTarget().size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
