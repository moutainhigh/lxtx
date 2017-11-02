package com.lxtech.mail;

import java.sql.SQLException;

public class SendMail {

	private static int count = 0;

	// 163邮箱
	public static void send_163(String title, String content) {
		// String[] receiever = { "1186734332@qq.com", "18310135821@163.com",
		// "23377708@qq.com"};
		String[] receiever = { "136390747@qq.com" };
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("ancbj5@163.com"); // 实际发送者
		mailInfo.setPassword("anc123");// 您的邮箱密码
		mailInfo.setFromAddress("ancbj5@163.com"); // 设置发送人邮箱地址
		mailInfo.setSubject(title);
		mailInfo.setContent(content);
		// 这个类主要来发送邮件
		for (String s : receiever) {
			mailInfo.setToAddress(s); // 设置接受者邮箱地址
			SimpleMailSender sms = new SimpleMailSender();
			sms.sendTextMail(mailInfo); // 发送文体格式
			// sms.sendHtmlMail(mailInfo); // 发送html格式
		}
	}

	public static void send_163(MailSender sender, String title, String content, String[] receiever) {
		for (String s : receiever) {
			count++;
			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setMailServerHost("smtp.163.com");
			mailInfo.setMailServerPort("25");
			mailInfo.setValidate(true);
			String username = sender.getUsername();
			String password = sender.getPassword();
			mailInfo.setUserName(username); // 实际发送者
			// mailInfo.setUserName("ancbj1@163.com"); // 实际发送者
			mailInfo.setPassword(password);// 您的邮箱密码
			// mailInfo.setPassword("anc123");// 您的邮箱密码
			mailInfo.setFromAddress(username); // 设置发送人邮箱地址
			// mailInfo.setFromAddress("ancbj1@163.com"); // 设置发送人邮箱地址
			mailInfo.setSubject(title);
			mailInfo.setContent(content);
			mailInfo.setToAddress(s); // 设置接受者邮箱地址

			SimpleMailSender sms = new SimpleMailSender();
			sms.sendTextMail(mailInfo);
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		MailSender sender = MailSender.getMailSender();
		send_163(sender, "这是一条活动短信", "邀请您今天下午5点参加奥体的健步走活动", new String[]{"18310135821@163.com"});
	}
}
