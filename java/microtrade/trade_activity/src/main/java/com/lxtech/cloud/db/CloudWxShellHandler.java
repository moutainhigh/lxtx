package com.lxtech.cloud.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.cloud.db.model.CloudWxShell;

public class CloudWxShellHandler {

	public static CloudWxShell getShellByChnno(String chnno) throws SQLException {
		String sql = "select * from cloud_wx_shell where chnno=?";

		Object[] params = { chnno };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		return qr.query(sql, params, new BeanHandler<CloudWxShell>(CloudWxShell.class));
	}
}
