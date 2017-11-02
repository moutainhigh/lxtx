package com.lxtech.biz;

import java.util.Date;

import com.lxtech.dao.CloudOrderDao;
import com.lxtech.mail.MailSender;
import com.lxtech.mail.SendMail;
import com.lxtech.util.BizDBProperty;
import com.lxtech.util.TimeUtil;

public class OrderEmptyByHalfHour {
	private static final int TIME_LIMIT = 30 * 60 * 1000;

	public static final String[] receiverList = new String[] {
			 "18310135821@163.com",
			 "277309442@qq.com" };

	public static void halfHourOrdered() {
		try {
			long buTime = CloudOrderDao.getLastOrderTime();
			long curtime = System.currentTimeMillis();
			if (curtime - buTime > TIME_LIMIT) {
				String title = "order warning: there's no order within 30 mins";
				String content = "please check the web server(" + BizDBProperty.getProperty("host") + ")"
						+ TimeUtil.getTimeOfSpecifiedDate(new Date());
				SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		OrderEmptyByHalfHour.halfHourOrdered();
	}
}
