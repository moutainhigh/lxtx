package com.lxtx.util.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.util.Md5Util;
import com.lxtx.util.tool.HttpRequest;
import com.lxtx.util.tool.JsonUtil;

public class SMSender {
	private static final Logger logger = LoggerFactory.getLogger(SMSender.class);
	
	public static boolean sendValidationMsg(String mobile, String validCode) {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

		String url = "https://api.miaodiyun.com/20150822/industrySMS/sendSMS";
		String account_sid = "5b9b19ecfb40499081ed5c2de907bda8";
		String AUTH_TOKEN = "1249ba1d899946c5b70f0f93ced2c584";
		String ts = sdf2.format(new Date());
		String encodedString = Md5Util.MD5Encode(account_sid + AUTH_TOKEN + ts, "UTF-8");
		String param = "accountSid=%s&smsContent=【九州微云】您的验证码为{%s}，请尽快验证&to=%s&timestamp=%s&sig=%s&respDataType=JSON";
		String content = HttpRequest.sendPost(url,
				String.format(param, account_sid, validCode, mobile, ts, encodedString));

		Map<String, Object> map = (Map<String, Object>) JsonUtil.convertStringToObject(content);
		String code = (String) map.get("respCode");
		if (code.equals("00000")) {
			return true;
		} else {
			logger.info("send sms failed, error code is:" + code);
			logger.error("send sms failed, error code is:" + code);
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println(sendValidationMsg("18612421791", "123456"));
	}
}
