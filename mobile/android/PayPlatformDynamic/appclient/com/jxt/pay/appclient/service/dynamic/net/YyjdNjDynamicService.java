package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

public class YyjdNjDynamicService implements IDynamicService{

	private static final String TYPE = "yyjdNj";
	private static Logger logger = Logger.getLogger(YyjdNjDynamicService.class);
	
	private static final String PARAM1 = "{\"bsid\":\"{bsid}\",\"imei\":\"{imei}\",\"imsi\":\"{imsi}\",\"cmd\":\"init\"}";
	private static final String PARAM2 = "{\"bsid\":\"{bsid}\",\"imei\":\"{imei}\",\"imsi\":\"{imsi}\",\"cmd\":\"charge\",\"itemid\":\"{itemid}\",\"itemprice\":\"{itemprice}\"}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = StringUtils.defaultString(map.get("theNo"));
		
		if("1".equals(theNo)){
			return dynamic1(map);
		}else if("2".equals(theNo)){
			return dynamic2(map);
		}
		
		return null;
	}
	
	private static Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String dynamic1(Map<String,String> map){
		
		String channel = StringUtils.defaultString(map.get("channel"));
		String url = map.get("url");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String bsid = map.get("bsid");
		
		String params = PARAM1.replace("{imsi}", imsi).replace("{imei}", imei).replace("{bsid}", bsid);
		
		String responseJson = new PostData().PostData(CommonUtil.base64Encode(params).getBytes(), url);
		
		String xml = parse1(responseJson);
		logger.info("xml1:"+xml);
		if(xml != null && xml.length() > 0){
			map1.remove(channel);
		}else{
			Integer cnt = map1.get(channel);
			
			if(cnt == null){
				cnt = 0;
			}
			
			if(cnt <= 2){
				map1.put(channel, cnt + 1);
				xml = DynamicUtils.parseWait(5,map);
			}else{
				map1.remove(channel);
				xml = DynamicUtils.parseError("599");
			}
		}
		
		return xml;	
	}
	
	private String parse1(String responseJson){
		if(responseJson != null && responseJson.length() > 0){
			
			try{
				responseJson = CommonUtil.base64Decode(responseJson);
				logger.info("responseJson1:"+responseJson);
				
				String type = getStr(responseJson,"\"type\":\"","\",");
				
				if(type != null && type.length() > 0){
					String msg = getStr(responseJson,"\"sms\":\"","\",");
					
					msg = CommonUtil.base64Decode(msg);//.replaceAll(" ","+"));
					
					String port = getStr(responseJson,"\"port\":\"","\"}");
					
					if(type.equals("login")){//需要发登录短信
						
						if(msg.length() > 1 && port.length() > 1){
							Sms sms = new Sms();
							sms.setSmsContent(msg);
							sms.setSmsDest(port);
							sms.setSuccessTimeOut(2);
							
							Sets sets = new Sets();
							sets.setKey("loginRet");
							sets.setValue("login");
							
							return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).append("<wait>"+(5+new Random().nextInt(6))+"</wait>").toString();
						}
					}else if(type.equals("docharge")){
						
						if(msg.length() > 1 && port.length() > 1){
							Sms sms = new Sms();
							sms.setSmsContent(msg);
							sms.setSmsDest(port);
							sms.setSuccessTimeOut(2);
							
							Sets sets = new Sets();
							sets.setKey("loginRet");
							sets.setValue("fee");
							
							return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).append("<wait>"+(5+new Random().nextInt(3))+"</wait>").toString();
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	private static Map<String, Integer> map2 = new HashMap<String, Integer>();
	
	private String dynamic2(Map<String,String> map){
		String channel = StringUtils.defaultString(map.get("channel"));
		String url = map.get("url");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String bsid = map.get("bsid");
		String itemid = map.get("itemid");
		String itemprice = map.get("itemprice");
		
		String params = PARAM2.replace("{imsi}", imsi).replace("{imei}", imei).replace("{itemid}", itemid).replace("{bsid}", bsid).replace("{itemprice}", itemprice);
		
		String responseJson = new PostData().PostData(CommonUtil.base64Encode(params).getBytes(),url);
		
		String xml = parse2(responseJson);
		logger.info("xml2:"+xml);
		if(xml != null && xml.length() > 0){
			map2.remove(channel);
		}else{
			Integer cnt = map2.get(channel);
			
			if(cnt == null){
				cnt = 0;
			}
			
			if(cnt <= 2){
				map2.put(channel, cnt + 1);
				xml = DynamicUtils.parseWait(5,map);
			}else{
				map2.remove(channel);
				xml = DynamicUtils.parseError("599");
			}
		}
		
		return xml;
	}
	
	private String parse2(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				responseJson = CommonUtil.base64Decode(responseJson);
				logger.info("responseJson2:"+responseJson);
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("status");
				
				if(status != null && status.length() > 0){
					if(status.equals("100")){
						Sets sets = new Sets();
						sets.setKey("execRet");
						sets.setValue("1");
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(status);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	public static String getStr(String ss,String start,String end){
		int pos0 = ss.indexOf(start);
		int pos1 = ss.indexOf(end,pos0+1);
		
		if(pos0 >= 0 && pos1 >= 0){
			return ss.substring(pos0+start.length(),pos1);
		}
		
		return "";
	}
	
	
	public static void main(String[] args){
		test1();
//		test2();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","yyjdNj");
		map.put("theNo","1");
		map.put("url","http://y211.mziku.info:7777/vms/billing");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		map.put("bsid","CH010CC");
		
		String xml = new YyjdNjDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","yyjd");
		map.put("theNo","2");
		map.put("url","http://y211.mziku.info:7777/vms/billing");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		map.put("bsid","CH010CC");
		map.put("itemid","600966020000006064");
		map.put("itemprice","1000");
		
		String xml = new YyjdNjDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
	
	
}
