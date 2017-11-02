package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;

public class SmsDynamicService implements IDynamicService{

	private static final String TYPE = "sms";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String dest = map.get("dest");
		String content = map.get("content");
		
		Sms sms = new Sms();
		sms.setSmsDest(dest);
		sms.setSmsContent(content);
		sms.setSuccessTimeOut(2);
		
		return XstreamHelper.toXml(sms).toString();
	}
	
	public static void main(String[] args){
		SmsDynamicService smsDynamicService = new SmsDynamicService();
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("dest", "106575554493");
		map.put("content", "2@oyddo@123456");
	
		System.out.println(smsDynamicService.dynamic(map));
	}

}
