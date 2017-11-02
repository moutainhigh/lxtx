package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 凤凰动漫
 * 	http://tv.003tv.com:8080/crackvideo/test/getsms?imsi=46000000000000&paykey=0000000&q=123123123
 * @author leoliu
 *
 */
public class FhSpjdDynamicService implements IDynamicService{
	
	private static Logger logger = Logger.getLogger(FhSpjdDynamicService.class);

	private static final String TYPE = "fhSpjd";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imsi={imsi}&paykey={paykey}&q={q}";
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			String imsi = map.get("imsi");
			String paykey = map.get("paykey");
			
			String param = REQUESTMODEL.replace("{imsi}",imsi).replace("{paykey}",paykey).replace("{q}",channel);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parse(responseJson);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 10){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("msg");
				
				if("getsmsok".equals(status)){
					Sms sms = new Sms();
					
					sms.setSmsContent(jo.getString("sms"));
					sms.setSmsDest(jo.getString("serviceno"));
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					sms.setSendType(Sms.SENDTYPE_DATA);
						
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError(status);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://tv.003tv.com:8080/crackvideo/test/getsms";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("imsi", "460001123143655");
		map.put("paykey", "d589020371fc489c9b59101ea70f5490");
		
		System.out.println(new FhSpjdDynamicService().dynamic(map));
	}

}
