package com.lxtech.mail;

import java.util.ArrayList;
import java.util.List;

public class MailSender {
	
	private static List<MailSender> senderList;
	
	static {
		senderList = new ArrayList<MailSender>();
		senderList.add(new MailSender("ancbj6@163.com", "anc123"));
		senderList.add(new MailSender("ancbj7@163.com", "anc123"));
		senderList.add(new MailSender("ancbj8@163.com", "wangyi123"));
	}
	
	private String username;
	private String password;

	public MailSender(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static MailSender getMailSender() {
		int index = (int)(Math.random()*((double)senderList.size()));
		return senderList.get(index);
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			MailSender sender = MailSender.getMailSender();
			System.out.println(sender.getUsername());
		}
	}
	
}
