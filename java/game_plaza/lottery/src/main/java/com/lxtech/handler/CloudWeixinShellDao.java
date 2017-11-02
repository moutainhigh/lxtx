package com.lxtech.handler;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.dbmodel.CloudWeixinShell;
import com.lxtech.inspect.utils.JdbcUtils;


public class CloudWeixinShellDao {

	public static List<CloudWeixinShell> getShellList() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from `g_wx_shell` where id!=6";
		List<CloudWeixinShell> shellList = qr.query(sql, new BeanListHandler<CloudWeixinShell>(CloudWeixinShell.class));
		return shellList;
	}

	public static void main(String[] args) {
		try {
			List<CloudWeixinShell> shellList = getShellList();
			for (CloudWeixinShell shell : shellList) {
				System.out.println(shell.getChnno());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<CloudWeixinShell> getMajiangShellList() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getMjDataSource());
		String sql = "select a.*,b.app_id as core_app_id from `m_wx_shell` a left join m_wx_service_provider b on a.core_id=b.id where a.id<11";
		List<CloudWeixinShell> shellList = qr.query(sql, new BeanListHandler<CloudWeixinShell>(CloudWeixinShell.class));
		return shellList;
	}
	
}
