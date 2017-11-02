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
import com.jxt.pay.handler.MobileInfoHandler;
import com.jxt.pay.helper.BlackMobileHelper;
import com.jxt.pay.pojo.MobileInfo;

/**
 * 第一步是手机发送一个登录指令
BUB@T|@W NAA8uk87@hWg55tc80@66388999@2888468985@8@{random5}2588882356@40955@{random5}68922894
BUB@T|@W之后有2个半角空格
random5的值和步骤二中orderId值必须相等，必须为五位数字，尽量不重复

第二步是wap登录取动态的扣费指令；
http://cc.channel.3gshow.cn/common/req.ashx?imsi=460000000000000&imei=86000000000000&mb=13800000000&cid=151&orderId=1234&pid=1173&payCodeID=5465&responseType=xml
得到返回结果：
<?xml version="1.0"?>
<Response>
<Status>1103</Status>
<Pay>
<SMS>
<Num>1065889923</Num>
<Content>&lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;info&gt;&lt;linkid&gt;cmZ4hRbL4H2NulZ2jcZLRgBCtsCVivik&lt;/linkid&gt;&lt;infoCode&gt;200013&lt;/infoCode&gt;&lt;smsInfo&gt;&lt;/smsInfo&gt;&lt;mobile&gt;&lt;/mobile&gt;&lt;userid&gt;&lt;/userid&gt;&lt;retry&gt;5&lt;/retry&gt;&lt;/info&gt;</Content>
</SMS>
</Pay>
</Response>
 * @author leoliu
 *
 */
public class ZztFxDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(ZztFxDynamicService.class);
	
	private static final String TYPE = "zztFx";
											  	
	private static final String MSGCONTENT = "BUB@T|@W  NAA8uk87@hWg55tc80@66388999@2888468985@8@{d}2588882356@40955@{d}68922894";
	private static final int LENGTH = 5;
	private static final String SMSDEST0 = "10658422";
		
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String random = getRandom(LENGTH);
		
		Sms sms = new Sms();
		sms.setSmsDest(SMSDEST0);
		sms.setSmsContent(MSGCONTENT.replace("{d}", random));
		sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
		
		StringBuffer smsBuffer = XstreamHelper.toXml(sms);
		
		Sets sets = new Sets();
		sets.setKey("randomCode");
		sets.setValue(random);
		
		StringBuffer setsBuffer = XstreamHelper.toXml(sets);
		
		setsBuffer.append(smsBuffer);
		
		return setsBuffer.toString();
	}
	
	
	private static final String[] arr = new String[]{"0","1","2","3","4","5","6","7","8","9"};

	private static String getRandom(int length){
		String random = "";
		
		for(int i = 0 ; i < length ; i ++){
			random += arr[new Random().nextInt(arr.length)];
		}
		
		return random;
	}
	
	public static void main(String[] args){
		
		System.out.println(new ZztFxDynamicService().dynamic(new HashMap<String, String>()));
		
	}
}
