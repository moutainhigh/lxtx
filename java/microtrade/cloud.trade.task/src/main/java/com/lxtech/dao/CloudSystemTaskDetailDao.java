package com.lxtech.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.lxtech.model.CloudSystemTaskDetail;
import com.lxtech.util.JdbcUtils;

public class CloudSystemTaskDetailDao {
	public static int updateByDayAndTaskNm(CloudSystemTaskDetail cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update cloud_system_task_detail set exec_time = ?,detail =? where exec_day = ? and task_nm = ?";
		Object params[] = { cc.getExecTime(), cc.getDetail(), cc.getExecDay(), cc.getTaskNm() };
		return qr.update(sql, params);
	}

	public static void insertTaskDetail(CloudSystemTaskDetail cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_system_task_detail (exec_day, exec_time,detail, task_nm)values (?,?,?,?)";
		Object params[] = { cc.getExecDay(), cc.getExecTime(), cc.getDetail(), cc.getTaskNm() };
		qr.update(sql, params);
	}
}
