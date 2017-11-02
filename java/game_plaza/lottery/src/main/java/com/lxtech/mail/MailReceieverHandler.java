package com.lxtech.mail;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.lxtech.inspect.utils.JdbcUtils;
import com.lxtech.mail.SendMail;



public class  MailReceieverHandler {
	public static MailReceiever query(String job) throws SQLException {
		// 将数据源传递给QueryRunner，QueryRunner内部通过数据源获取数据库连接
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from mail_receiever where job = ?";
		Object params[] = { job };
		MailReceiever mr = (MailReceiever) qr.query(sql, new BeanHandler<MailReceiever>(MailReceiever.class),params);
		return mr;
	}
	
	
	
	public static void main(String[] args){
//		String[] receiever = CheckUrl.getReceiver("checkapk");
		 String[] r =  { "136390747@qq.com"};
		SendMail.send_163("测试1", "测试1",r);
		System.out.println("测试1");
		SendMail.send_163("测试2", "测试2",r);
		System.out.println("测试2");
		SendMail.send_163("测试3", "测试3",r);
		System.out.println("测试3");
	}
}
