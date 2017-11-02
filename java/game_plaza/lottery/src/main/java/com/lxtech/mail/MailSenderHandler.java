package com.lxtech.mail;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.inspect.utils.JdbcUtils;

public class MailSenderHandler {
	public static MailSender query(int id) throws SQLException {
		// 将数据源传递给QueryRunner，QueryRunner内部通过数据源获取数据库连接
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from mail_sender where id = ?";
		Object params[] = { id };
		MailSender mr = (MailSender) qr.query(sql, new BeanHandler<MailSender>(MailSender.class),params);
		return mr;
	}
	
	public static void main(String[] args){
		try {
			MailSender ms = query(1);
			ms.getId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
