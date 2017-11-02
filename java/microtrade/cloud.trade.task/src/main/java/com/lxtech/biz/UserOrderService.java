package com.lxtech.biz;

import java.util.Date;

import com.lxtech.dao.CloudOrderDao;
import com.lxtech.mail.MailSender;
import com.lxtech.mail.SendMail;
import com.lxtech.util.TimeUtil;

public class UserOrderService {
	private static final int TIME_LIMIT = 5 * 60 * 1000;

	public static final String[] receiverList = new String[] {
			 "18310135821@163.com",
			"hecm123@126.com" };

	public static void userOrder() {
		try {
			String curr = TimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
			long beginTime = System.currentTimeMillis() - TIME_LIMIT;
			String begin = TimeUtil.formatDate(new Date(beginTime), "yyyy-MM-dd HH:mm:ss");
			int uid = 23245;
			int countOrder = CloudOrderDao.getOrderById(begin,curr,uid);
			if (countOrder > 0) {
				String title = "23245用户下单告警";
				String content = "23245用户下单了："
						+ TimeUtil.getTimeOfSpecifiedDate(new Date());
				SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		UserOrderService.userOrder();
	}
}
