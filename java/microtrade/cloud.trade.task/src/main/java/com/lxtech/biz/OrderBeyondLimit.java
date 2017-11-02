package com.lxtech.biz;

import java.util.Date;

import com.lxtech.dao.CloudOrderDao;
import com.lxtech.mail.MailSender;
import com.lxtech.mail.SendMail;
import com.lxtech.util.BizDBProperty;
import com.lxtech.util.TimeUtil;

public class OrderBeyondLimit {
	private static final int TIME_LIMIT = 5 * 60 * 1000;

	public static final String[] receiverList = new String[] {
			 "18310135821@163.com",
			 "277309442@qq.com" };

	public static void orderBeyondLimit() {
		try {
			String curr = TimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
			long beginTime = System.currentTimeMillis() - TIME_LIMIT;
			String begin = TimeUtil.formatDate(new Date(beginTime), "yyyy-MM-dd HH:mm:ss");
			int countOrder = CloudOrderDao.getBeyondOrderLimitData(begin,curr);
			if (countOrder > 0) {
				String title = "lose money warning: we've lost over 1000 within 5mins";
				String content = "please check the data(" + BizDBProperty.getProperty("host") + ")"
						+ TimeUtil.getTimeOfSpecifiedDate(new Date());
				SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		OrderBeyondLimit.orderBeyondLimit();
	}
}
