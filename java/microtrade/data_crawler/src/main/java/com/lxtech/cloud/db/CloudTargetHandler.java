package com.lxtech.cloud.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.cloud.db.model.CloudTarget;

/**
 * Operations for updating the generated target table and query the original
 * target table
 * 
 * @author wangwei
 *
 */
public class CloudTargetHandler {

	public static int updateCloudTargetIndex(String name, double newIndex) throws SQLException {
		String sql = "update cloud_target set current_index = ? where name = ?";
		Object[] params = { newIndex, name };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.update(sql, params);
	}
	
	public static int retrieveTargetIndex(String name) throws SQLException {
		String sql = "select * from cloud_target where name = ?";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		CloudTarget target = qr.query(sql, new Object[]{name}, new BeanHandler<CloudTarget>(CloudTarget.class));
		return target.getCurrent_index();
	}

	public static void main(String[] args) throws SQLException {
		System.out.println(retrieveTargetIndex("BU"));
	}

}
