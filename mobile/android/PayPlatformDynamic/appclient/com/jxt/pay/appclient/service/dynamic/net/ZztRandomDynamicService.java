package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.handler.MobileInfoHandler;
import com.jxt.pay.helper.BlackMobileHelper;
import com.jxt.pay.pojo.MobileInfo;

/**
 * 第一步是手机发送一个登录指令
BUB@T|@W NAA8uk87@hWg55tc80@66388999@2888468985@8@{random5}2588882356@40955@{random5}68922894
BUB@T|@W之后有2个半角空格
random5的值和步骤二中orderId值必须相等，必须为五位数字，尽量不重复
 * @author leoliu
 *
 */
public class ZztRandomDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(ZztRandomDynamicService.class);
	
	private static final String TYPE = "zztRandom";
	
	private static final String SMSDEST0 = "10658422";
		
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String sLength = map.get("length");
		
		int length = Integer.parseInt(sLength);
		
		String channel = map.get("channel");
		
		String random = "";
		
		if(channel != null && channel.length() > length){
			random = channel.substring(channel.length() - length,channel.length());
		}else{
			random = StringUtils.getRandom(length);
		}
		
		String msgContent = map.get("msgContent");
		
		Sms sms = new Sms();
		sms.setSmsDest(SMSDEST0);
		sms.setSmsContent(msgContent.replace("{d}", random));
		sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
		
		StringBuffer smsBuffer = XstreamHelper.toXml(sms);
		
		Sets sets = new Sets();
		sets.setKey("randomCode");
		sets.setValue(random);
		
		StringBuffer setsBuffer = XstreamHelper.toXml(sets);
		
		setsBuffer.append(smsBuffer);
		
		return setsBuffer.toString();
	}
	
	public static void main(String[] args){
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("channel","13B201a012346603");
		map.put("msgContent","http://cc.channel.3gshow.cn/common/req.ashx");
		map.put("length","13");
		map.put("msgContent", "BUB@T|@W  NAA8uk87@hWg95tc43@76288259@8228988485@8@09{d}@40955@{d}");
		
		System.out.println(new ZztRandomDynamicService().dynamic(map));
		
	}
}
