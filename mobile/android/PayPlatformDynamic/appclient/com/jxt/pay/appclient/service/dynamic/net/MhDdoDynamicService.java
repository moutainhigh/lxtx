package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class MhDdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(MhDdoDynamicService.class);
	
	private static final String TYPE = "mhDdo";
	
	private static final String URL = "http://api.170ds.com/wlappserv/f/api/feeInfo/smsReq2out?ci=aaaa&appid={appid}&imei={imei}&imsi={imsi}&ip={ip}&price={price}&mobile={mobile}&opid=1";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String channel = map.get("channel");
		
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String appid = map.get("appid");
		String price = map.get("price");
		String mobile = map.get("dmobile");
		
		String url = URL.replace("{imsi}", imsi).replace("{imei}", imei).replace("{appid}", appid).replace("{price}", price).replace("{ip}", "115.28.52.43").replace("{mobile}",mobile);
		
		String responseJson = GetData.getData(url);
		
		logger.info("responseJson:"+responseJson);
		
		if(responseJson != null && responseJson.length() > 0){
			String xml = parse(responseJson);
			logger.info("xml:"+xml);
			return xml;
		}
		
		return null;
	}
	
	private String parse(String responseJson){
		try{
			JSONObject jo = new JSONObject(responseJson);
			
			String resultCode = jo.getString("resultCode");
			
			if("0".equals(resultCode)){
				String execurl = jo.getString("execurl");
				
				Sets sets = new Sets();
				sets.setKey("execurl");
				sets.setValue(execurl);
				
				return XstreamHelper.toXml(sets).toString();
			}else{
				return DynamicUtils.parseError(resultCode);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

}