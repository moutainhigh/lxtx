package com.lxtech.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.lxtech.model.CloudUserDayTranSum;
import com.lxtech.util.JdbcUtils;

public class CloudUserDayTranSumDao {
	public static void saveData(CloudUserDayTranSum cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_user_day_tran_sum (tran_u_count, tran_amount,tran_commission, tran_order_count, tran_u_mean_count, tran_mean_amount,single_tran_amount,tran_u_old_count,tran_u_old_order_count,tran_u_old_amount,tran_u_old_commission,tran_u_old_count_prop,tran_u_old_amount_prop,tran_u_old_commission_prop,`date`)values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object params[] = { cc.getTran_u_count(), cc.getTran_amount(), cc.getTran_commission(),
				cc.getTran_order_count(), cc.getTran_u_mean_count(), cc.getTran_mean_amount(),
				cc.getSingle_tran_amount(), cc.getTran_u_old_count(), cc.getTran_u_old_order_count(),
				cc.getTran_u_old_amount(), cc.getTran_u_old_commission(), cc.getTran_u_old_commission_prop(),
				cc.getTran_u_old_amount_prop(), cc.getTran_u_old_commission_prop(), cc.getDate() };
		qr.update(sql, params);
	}

	public static int updateData(CloudUserDayTranSum cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update cloud_user_day_tran_sum set tran_u_count = ?, tran_amount = ?, tran_commission =?, tran_order_count = ?,tran_u_mean_count=?,tran_mean_amount=?,single_tran_amount=?,tran_u_old_count=?,tran_u_old_order_count=?,tran_u_old_amount=?,tran_u_old_commission=?,tran_u_old_count_prop=?,tran_u_old_amount_prop=?,tran_u_old_commission_prop=? where `date` = ?";
		Object params[] = { cc.getTran_u_count(), cc.getTran_amount(), cc.getTran_commission(),
				cc.getTran_order_count(), cc.getTran_u_mean_count(), cc.getTran_mean_amount(),
				cc.getSingle_tran_amount(), cc.getTran_u_old_count(), cc.getTran_u_old_order_count(),
				cc.getTran_u_old_amount(), cc.getTran_u_old_commission(), cc.getTran_u_old_commission_prop(),
				cc.getTran_u_old_amount_prop(), cc.getTran_u_old_commission_prop(), cc.getDate() };
		return qr.update(sql, params);
	}
}
