package com.lxtech.cloudtrade.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.cloudtrade.db.model.CloudTarget;
import com.lxtech.cloudtrade.net.KestrelConnector;


/**
 * Operations for updating the generated target table and query the original
 * target table
 * 
 * @author wangwei
 *
 */
public class CloudTargetHandler {

	public static CloudTarget retrieveOriginalTargetData(String name) throws SQLException {
		String sql = "select name, current_index from cloud_target where name = ?";

		String[] params = { name };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudTarget>(CloudTarget.class));
	}

	public static CloudTarget retrieveTargetData(String dataSource, String name) throws SQLException { 
		String tableName = "cloud_target";
		if (dataSource.equals(KestrelConnector.QUEUE_NAME_GEN)) {
			tableName = tableName + "_gen"; 
		}
		String sql = "select name, current_index from " + tableName +  " where name = ?";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String[] params = { name };
		
		return qr.query(sql, params, new BeanHandler<CloudTarget>(CloudTarget.class));
	}
	
	public static CloudTarget retrieveGeneratedTargetData(String name) throws SQLException {
		String sql = "select name, current_index from cloud_target_gen where name = ?";

		String[] params = { name };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudTarget>(CloudTarget.class));
	}

	public static int updateCloudTargetIndex(String name, int newIndex) throws SQLException {
		String sql = "update cloud_target_gen set current_index = ? where name = ?";
		Object[] params = { newIndex, name };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.update(sql, params);
	}

	public static void main(String[] args) throws SQLException {
		CloudTarget target = CloudTargetHandler.retrieveOriginalTargetData("BU");
		System.out.println(target.getName() + " " + target.getCurrent_index());

		CloudTargetHandler.updateCloudTargetIndex("BU", 2980);
		target = CloudTargetHandler.retrieveGeneratedTargetData("BU");
		System.out.println(target.getName() + " " + target.getCurrent_index());
	}

}
