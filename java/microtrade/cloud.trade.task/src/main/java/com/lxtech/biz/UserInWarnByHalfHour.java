package com.lxtech.biz;

import java.util.Date;

import com.lxtech.dao.CloudUserDao;
import com.lxtech.mail.MailSender;
import com.lxtech.mail.SendMail;
import com.lxtech.util.BizDBProperty;
import com.lxtech.util.TimeUtil;

public class UserInWarnByHalfHour {
	private static final int TIME_LIMIT = 30 * 60 * 1000;

	public static final String[] receiverList = new String[] {
			 "18310135821@163.com",
			 "277309442@qq.com" };

	public static void userInWarn() {
		try {
			long buTime = CloudUserDao.getLastUserInTime();
			long curtime = System.currentTimeMillis();
			if (curtime - buTime > TIME_LIMIT) {
				String title = "用户注册告警:已有半小时无新用户注册";
				String content = "请核实主服务(" + BizDBProperty.getProperty("host") + ")是否正常"
						+ TimeUtil.getTimeOfSpecifiedDate(new Date());
				SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		UserInWarnByHalfHour.userInWarn();
	}

}
