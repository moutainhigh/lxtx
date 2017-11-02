package com.lxtech.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.model.CloudWeixinShell;
import com.lxtech.util.JdbcUtils;

public class CloudWeixinShellDao {

	public static List<CloudWeixinShell> getShellList() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `cloud_wx_shell`";
		List<CloudWeixinShell> shellList = qr.query(sql, new BeanListHandler<CloudWeixinShell>(CloudWeixinShell.class));
		return shellList;
	}
	
	public static CloudWeixinShell getShellById(int shellId) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `cloud_wx_shell` where id = ?";
		
		return qr.query(sql, new Object[]{shellId}, new BeanHandler<CloudWeixinShell>(CloudWeixinShell.class));
	}
	
	public static CloudWeixinShell getShellByChnno(String chnno) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `cloud_wx_shell` where chnno = ?";
		
		return qr.query(sql, new Object[]{chnno}, new BeanHandler<CloudWeixinShell>(CloudWeixinShell.class));
	}

	public static void main(String[] args) {
/*		try {
			List<CloudWeixinShell> shellList = getShellList();
			for (CloudWeixinShell shell : shellList) {
				System.out.println(shell.getApp_name());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		
		try {
			CloudWeixinShell shell = CloudWeixinShellDao.getShellById(1);
			System.out.println(shell.getApp_name());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
