package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Map;

import com.jxt.pay.helper.ShuaConfigHelper;

public class ShuaDynamicService implements IDynamicService{

	private static final String TYPE = "shua";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String data = map.get("data");
		String payCode = map.get("payCode");
		
		String model = shuaConfigHelper.getModel(payCode);
		
		model = model.replace("{imsi}",imsi).replace("{imei}", imei).replace("{data}",data);
		
		return model;
	}
	
	//ioc
	private ShuaConfigHelper shuaConfigHelper;

	public void setShuaConfigHelper(ShuaConfigHelper shuaConfigHelper) {
		this.shuaConfigHelper = shuaConfigHelper;
	}
	
}
