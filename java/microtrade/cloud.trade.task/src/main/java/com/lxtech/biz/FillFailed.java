package com.lxtech.biz;

import java.util.Date;

import com.lxtech.dao.CloudFundHistoryDao;
import com.lxtech.mail.MailSender;
import com.lxtech.mail.SendMail;
import com.lxtech.util.BizDBProperty;
import com.lxtech.util.TimeUtil;

public class FillFailed {

	public static final String[] receiverList = new String[] {
			 "18310135821@163.com",
			"277309442@qq.com"};

	public static void fillFailed() {
		int TIME_LIMIT = 15 * 60 * 1000;
		try {
			String curr = TimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
			long beginTime = System.currentTimeMillis() - TIME_LIMIT;
			String begin = TimeUtil.formatDate(new Date(beginTime), "yyyy-MM-dd HH:mm:ss");
			int countFill = CloudFundHistoryDao.getFillData(begin, curr);
			if (countFill == 0) {
			}else{
				int countFillFailed = CloudFundHistoryDao.getFillFailedData(begin, curr);
				if (countFill == countFillFailed) {
					String title = "recharge has failed：all recharge actions within 15 mins failed";
					String content = "Please check the recharge service(" + BizDBProperty.getProperty("host") + ")"
							+ TimeUtil.getTimeOfSpecifiedDate(new Date());
					SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void repayBeyondLimit() {
		int TIME_LIMIT = 5 * 60 * 1000;
		try {
			String curr = TimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
			long beginTime = System.currentTimeMillis() - TIME_LIMIT;
			String begin = TimeUtil.formatDate(new Date(beginTime), "yyyy-MM-dd HH:mm:ss");
			int countRepay = CloudFundHistoryDao.getRepayLimitCount(begin, curr);
			if (countRepay > 0) {
				String title = "big money redraw warning: someone withdrawed thousand yuan within 5 mins";
				String content = "please check withdraw records(" + BizDBProperty.getProperty("host") + ")"
						+ TimeUtil.getTimeOfSpecifiedDate(new Date());
				SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		fillFailed();
	}

	public static void FillBeyondLimit() {
		int TIME_LIMIT = 5 * 60 * 1000;
		try {
			String curr = TimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
			long beginTime = System.currentTimeMillis() - TIME_LIMIT;
			String begin = TimeUtil.formatDate(new Date(beginTime), "yyyy-MM-dd HH:mm:ss");
			int countFill = CloudFundHistoryDao.getFillLimitCount(begin, curr);
			if (countFill > 0) {
				String title = "big money recharge warning: someone recharged over 3000rmb within 5mins";
				String content = "please check recharge data(" + BizDBProperty.getProperty("host") + ")"
						+ TimeUtil.getTimeOfSpecifiedDate(new Date());
				SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
