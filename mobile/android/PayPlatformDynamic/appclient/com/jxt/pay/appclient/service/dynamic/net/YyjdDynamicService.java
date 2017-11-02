package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

public class YyjdDynamicService implements IDynamicService{

	private static final String TYPE = "yyjd";
	private static Logger logger = Logger.getLogger(YyjdDynamicService.class);
	private static final String UA = "Dalvik/1.6.0%20(Linux;%20U;%20Android%204.4.4;%20C6502%20Build/14.4.A.0.108)";
	
	private static final String PARAM1 = "imei={imei}&imsi={imsi}&ua={ua}&chid={chid}&type=0";
	private static final String PARAM2 = "imei={imei}&imsi={imsi}&ua={ua}&chid={chid}&type=1";
	private static final String PARAM3 = "imei={imei}&imsi={imsi}&ua={ua}&chid={chid}&type=2&rsbc={rsbc}&ps={ps}";
	
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
		}else if("3".equals(theNo)){
			return dynamic3(map);
		}
		
		return null;
	}
	
	private static Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String dynamic1(Map<String,String> map){
		
		String channel = StringUtils.defaultString(map.get("channel"));
		String url = map.get("url");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String chid = map.get("chid");
		String ua = map.get("ua");
		
		if(ua == null || ua.length() == 0){
			ua = UA;
		}
		
		String params = PARAM1.replace("{imsi}", imsi).replace("{imei}", imei).replace("{ua}", ua).replace("{chid}", chid);
		
		String responseXml = GetData.getData(url,params);
		logger.info("responseXml1:"+responseXml);
		String xml = parse1(responseXml);
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
	
	private String parse1(String responseXml){
		if(responseXml != null && responseXml.length() > 0){
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if(status != null && status.length() > 0){
				if(status.equals("100")){
					String msg = SingleXmlUtil.getNodeValue(responseXml, "msg");
					String port = SingleXmlUtil.getNodeValue(responseXml, "port");
					
					if(msg.length() > 1 && port.length() > 1){
						Sms sms = new Sms();
						sms.setSmsContent(msg);
						sms.setSmsDest(port);
						sms.setSuccessTimeOut(2);
						
						return XstreamHelper.toXml(sms).append("<wait>10</wait>").toString();
					}
				}else if(status.equals("1")){
					Sets sets = new Sets();
					sets.setKey("parse1");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
				}else if(status.equals("333333")){
					return DynamicUtils.parseError(status);
				}
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
		String chid = map.get("chid");
		String ua = map.get("ua");
		
		if(ua == null || ua.length() == 0){
			ua = UA;
		}
		
		String params = PARAM2.replace("{imsi}", imsi).replace("{imei}", imei).replace("{ua}", ua).replace("{chid}", chid);
		
		String responseXml = GetData.getData(url,params);
		logger.info("responseXml2:"+responseXml);
		String xml = parse2(responseXml);
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
	
	private String parse2(String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if(status != null && status.length() > 0){
				if(status.equals("100")){
					String msg = SingleXmlUtil.getNodeValue(responseXml, "msg");
					String port = SingleXmlUtil.getNodeValue(responseXml, "port");
					
					Sms sms = new Sms();
					sms.setSmsContent(msg);
					sms.setSmsDest(port);
					sms.setSuccessTimeOut(2);
					
					Sets sets = new Sets();
					sets.setKey("rsbc");
					sets.setValue(SingleXmlUtil.getNodeValue(responseXml, "rsbc"));
					
					return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).append("<wait>10</wait>").toString();
				}else if(status.equals("1010")){
					return DynamicUtils.parseError(status);
				}
			}
			
		}
		
		return null;
	}
	
	private static Map<String, Integer> map3 = new HashMap<String, Integer>();
	
	private String dynamic3(Map<String,String> map){
		String channel = StringUtils.defaultString(map.get("channel"));
		String url = map.get("url");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String chid = map.get("chid");
		String ua = map.get("ua");
		String rsbc = map.get("rsbc");
		String ps = map.get("ps");
		
		if(ua == null || ua.length() == 0){
			ua = UA;
		}
		
		String params = PARAM3.replace("{imsi}", imsi).replace("{imei}", imei).replace("{ua}", ua).replace("{chid}", chid).replace("{rsbc}", rsbc).replace("{ps}",ps);
		
		String responseXml = GetData.getData(url,params);
		logger.info("responseXml3:"+responseXml);
		String xml = parse3(responseXml);
		logger.info("xml3:"+xml);
		if(xml != null && xml.length() > 0){
			map3.remove(channel);
		}else{
			Integer cnt = map3.get(channel);
			
			if(cnt == null){
				cnt = 0; 
			}
			
			if(cnt <= 2){
				map3.put(channel, cnt + 1);
				xml = DynamicUtils.parseWait(5,map);
			}else{
				map3.remove(channel);
				xml = DynamicUtils.parseError("599");
			}
		}
		
		return xml;
	}
	
	private String parse3(String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if(status != null && status.length() > 0){
				if(status.equals("0")){
					
					Sets sets = new Sets();
					sets.setKey("parse3");
					sets.setValue("0");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					if(Integer.parseInt(status) < 0){
						
					}else{
						return DynamicUtils.parseError(status);
					}
				}
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		test1();
//		test2();
//		test3();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","yyjd");
		map.put("theNo","1");
		map.put("url","http://111.4.114.148:1001/Services.s1");
		map.put("imsi","460078740873624");
		map.put("imei","861158024090312");
		map.put("chid","W1130A");
		
		String xml = new YyjdDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","yyjd");
		map.put("theNo","2");
		map.put("url","http://111.4.114.148:1001/Services.s1");
		map.put("imsi","460078740873624");
		map.put("imei","861158024090312");
		map.put("chid","W1130A");
		
		String xml = new YyjdDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
	
	private static void test3(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","yyjd");
		map.put("theNo","3");
		map.put("url","http://111.4.114.148:1001/Services.s1");
		map.put("imsi","460078740873624");
		map.put("imei","861158024090312");
		map.put("chid","W1130A");
		map.put("rsbc", "f8614d56360496e520151216134717");
		map.put("ps", "D1008");
		
		String xml = new YyjdDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
}
