package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.handler.BlackMobileHandler;
import com.jxt.pay.handler.MobileInfoHandler;
import com.jxt.pay.handler.PhoneNoRegionHandler;
import com.jxt.pay.helper.BlackMobileHelper;
import com.jxt.pay.pojo.MobileInfo;
import com.jxt.pay.pojo.PhoneNoRegion;

/**
 * 对接南京网游
 * @author leoliu
 *
 */
public class TwiceDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(TwiceDynamicService.class);
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String TYPE = "twice";
	
	private int timeOut = 60;
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
		}else if(THENO_2.equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}
	
	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			try {
				url = URLDecoder.decode(url, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			String responseXml = GetData.getData(url);
			
			xml = parseFirst(responseXml,map);
		
			String cpparam = map.get("channel");
			
			if(xml == null){
				Integer cnt = map1.get(cpparam);
				
				if(cnt == null){
					cnt = 0;
				}
				
				if(cnt >= 3){
					map1.remove(cpparam);
					xml = DynamicUtils.parseError("598");
				}else{
					cnt ++;
					map1.put(cpparam, cnt);
					
					xml = DynamicUtils.parseWait(10,map);//获取失败
				}
			}else{
				map1.remove(cpparam);
			}
		}
		
		return xml;
	}

	/**
	 * <p><status>0</status><error></error><errorInfo></errorInfo><taskId>1429886825768zVOCZRh</taskId><ip>123.57.45.100</ip><regist><SMS>QlVCQFR8VG4gIFdJQEhWTTlCOHZBQEFAOTI4MjlOOC1BVzY4QDVAajRfc3N5TTAxT0Q0OEA5Njk4NjY5OUA4Mjc3MDk2OTU4QDFANDk0NDE5MjI4MTEyOTI2QDIwNjQ0QDgzOTQ5Njg4OTI4MjQ=</SMS></regist></p>
	 * @param responseXml
	 * @return
	 */
	private String parseFirst(String responseXml,Map<String,String> map){
		logger.info("parseFirst : "+responseXml);
		if(responseXml != null && responseXml.length() > 0){
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if(status != null && status.equals("0")){
				try {
					String smsContent = CommonUtil.base64Decode(SingleXmlUtil.getNodeValue(responseXml, "SMS"));
					
					Sms sms = new Sms();
					sms.setSmsDest(map.get("dest"));
					sms.setSmsContent(smsContent);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					StringBuffer smsBuffer = XstreamHelper.toXml(sms);
					
					Sets sets = new Sets();
					sets.setKey("taskId");
					sets.setValue(SingleXmlUtil.getNodeValue(responseXml,"taskId"));
					
					StringBuffer setsBuffer = XstreamHelper.toXml(sets);
					
					Sets sets1 = new Sets();
					sets1.setKey("nextIp");
					sets1.setValue(SingleXmlUtil.getNodeValue(responseXml,"ip"));
					
					setsBuffer.append(XstreamHelper.toXml(sets1));
					
					//发两次，为了提高送达率
					setsBuffer.append(smsBuffer);
					
					return setsBuffer.toString();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String,String> map){
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){

			try {
				url = URLDecoder.decode(url, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			String responseXml =  GetData.getData(url);
		
			xml = parseSecond(map,responseXml);

			String channel = map.get("channel");
			
			if(xml == null){

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
				
				xml = DynamicUtils.parseWait(10,map);//获取失败
			
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
		
	private String parseSecond(Map<String,String> map,String responseXml){
		
		logger.info("parse second : "+responseXml);
		
		if(responseXml != null && responseXml.length() > 0){
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if("0".equals(status)){//等待
				try {
					String smsContent = CommonUtil.base64Decode(SingleXmlUtil.getNodeValue(responseXml, "SMS"));
					
					Sms sms = new Sms();
					sms.setSmsDest(map.get("dest"));
					sms.setSmsContent(smsContent);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					return XstreamHelper.toXml(sms).toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	public static void main(String[] args){
		test1();
	}
	
	private static void test1(){
		String url = "http://182.92.21.219:10789/cmcc/g/online/s2sAutoRegist?imei=862949029214504&imsi=460022101441340&chargeId=006047297005&pid=6821848435-8000667076&channelId=10218677&payId=0000000000000000&version=1.0.0.8";
		
		try {
			url = URLEncoder.encode(url,"utf-8");
			System.out.println(url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url",url);
		map.put("type","twice");
		map.put("theNo","1");
		map.put("channel","10B101a012345678");
		map.put("dest","10658422");
		
		System.out.println(new TwiceDynamicService().dynamic(map));
	}
	
	private static void test2(){
		String url = "http://123.57.68.225:10789/cmcc/g/online/s2sAutoChargeSMS?taskId=1429888428697TGn5XIB&pid=6821848435-8000667076&version=1.0.0.8";
		
		try {
			url = URLEncoder.encode(url,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url",url);
		map.put("type","twice");
		map.put("theNo","2");
		map.put("channel","10B101a012345678");
		map.put("dest","1065889923");
		
		System.out.println(new TwiceDynamicService().dynamic(map));
	}
}
