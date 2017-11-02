package com.lxtech.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.model.CloudTempMsg;
import com.lxtech.util.JdbcUtils;

public class TempMsgDao {

	public static CloudTempMsg queryTempMsg(Integer proId, String date, int type, int status) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_temp_msg where wx_provider_id = ? and date=? and msg_type=? and status = ? limit 1";
		Object params[] = { proId, date, type, status };
		return qr.query(sql, new BeanHandler<CloudTempMsg>(CloudTempMsg.class), params);
	}

	public static int updateTempMsg(Integer id, int status) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update cloud_temp_msg set status = ? where id = ?";
		Object params[] = { status, id };
		return qr.update(sql, params);
	}

	public static void main(String[] args) {
		try {
			CloudTempMsg d = TempMsgDao.queryTempMsg(1, "2016-12-03", 0, 0);
			System.out.println(d.getUid());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
