package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class CdblDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(CdblDynamicService.class);
	
	private static final String TYPE = "Cdbl";
	
	private static final String PARAM1 = "action={action}&cpid={cpid}&imsi={imsi}&imei={imei}&serviceid={serviceid}&mode={mode}";
	private static final String PARAM2 = "action={action}&cpid={cpid}&imsi={imsi}&imei={imei}&serviceid={serviceid}&mode={mode}";
	private static final String PARAM3 = "action={action}&cpid={cpid}&imsi={imsi}&imei={imei}&serviceid={serviceid}&mode={mode}&time={time}&random_key={random_key}&channeldata={channeldata}";
	
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	private static final String THENO_3 = "3";
	
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	private static String time ="";
	private static String key = "";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
		}else if(THENO_2.equals(theNo)){
			return secondDynamic(map);
		}else if(THENO_3.equals(theNo)){
			return thirdDynamic(map);
		}
		
		return null;
	}
	
	public static String gettime(){
		String time = ""+System.currentTimeMillis()/1000;
		return time;
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
//		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String cpid = map.get("cpid");
			String serviceid = map.get("serviceid");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String mode = map.get("mode");
			String action = map.get("action");
			
			String param = url+"?"+PARAM1.replace("{action}", action).replace("{cpid}", cpid).replace("{serviceid}",serviceid).replace("{mode}",mode).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseXml = GetData.getData(url, param);
			logger.info("-----"+responseXml);
			xml = parseFirst(map,responseXml);

			if(xml == null){
				
				return xml = DynamicUtils.parseError("598");
				
			}
			
		}
		
		return xml;
	}
	
	private String parseFirst(Map<String,String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			try{
				
				logger.info(responseXml);
				
				String msgcode = SingleXmlUtil.getNodeValue(responseXml, "return_code");
				
				
					if ("0".equals(msgcode)) {
						
						Sets sets = new Sets();
						sets.setKey("_succ");
						sets.setValue("1");
						return XstreamHelper.toXml(sets).toString();
						
					}
						String init_add = SingleXmlUtil.getNodeValue(responseXml, "init_add");
						String init_msg = SingleXmlUtil.getNodeValue(responseXml, "init_msg");
						
						String smsContent = CommonUtil.base64Decode(init_msg);
						Sms sms = new Sms();
						sms.setSmsContent(smsContent);
						sms.setSmsDest(init_add);
						sms.setSuccessTimeOut(8);
//						sms.setWaitGuard("8");
						StringBuffer smsBuffer = XstreamHelper.toXml(sms);
						
						Sets sets = new Sets();
						sets.setKey("msgcode");
						sets.setValue(msgcode);
						
						StringBuffer setsBuffer = XstreamHelper.toXml(sets);
						//发两次，为了提高送达率
						setsBuffer.append(smsBuffer);
						setsBuffer.append(smsBuffer);
						
						return setsBuffer.toString();
					

				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
//		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String cpid = map.get("cpid");
			String serviceid = map.get("serviceid");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String mode = map.get("mode");
			String action = map.get("action");
			
			
			String param = url+"?"+PARAM2.replace("{action}", action).replace("{cpid}", cpid).replace("{serviceid}",serviceid).replace("{mode}",mode).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseXml = GetData.getData(url, param);
			logger.info("-----"+responseXml);
			xml = parseSecond(map,responseXml);

			if(xml == null){
				
				return xml = DynamicUtils.parseError("598");
				
			}
			
		}
		
		return xml;
	}

	
	private String parseSecond(Map<String, String> map, String responseXml) {
		
		if(responseXml != null && responseXml.length() > 0){
			
			logger.info(responseXml);
			
			String return_code = SingleXmlUtil.getNodeValue(responseXml, "return_code");
				
			if(return_code != null && return_code.equals("7009-1")){
				
				
				
				
			}else if (return_code != null && return_code.equals("0")) {
				String sms_msg = SingleXmlUtil.getNodeValue(responseXml, "sms_msg");
				String sms_add = SingleXmlUtil.getNodeValue(responseXml, "sms_add");
				time = SingleXmlUtil.getNodeValue(responseXml, "time");
				key = SingleXmlUtil.getNodeValue(responseXml, "random_key");
				String smsContent;
				try {
					smsContent = CommonUtil.base64Decode(sms_msg);
					Sms sms = new Sms();
					sms.setSmsContent(smsContent);
					sms.setSmsDest(sms_add);
					sms.setSuccessTimeOut(8);
//					sms.setWaitGuard("8");
					StringBuffer smsBuffer = XstreamHelper.toXml(sms);
					
					Sets sets = new Sets();
					sets.setKey("return_code");
					sets.setValue(return_code);
					
					StringBuffer setsBuffer = XstreamHelper.toXml(sets);
					//发两次，为了提高送达率
					setsBuffer.append(smsBuffer);
					setsBuffer.append(smsBuffer);
					
					return setsBuffer.toString();
					
				} catch (UnsupportedEncodingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
							
				
			}
			
			return DynamicUtils.parseError("597");
		}
		
		return null;
	}
	
private String thirdDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
//		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String cpid = map.get("cpid");
			String serviceid = map.get("serviceid");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String mode = map.get("mode");
			String channeldata = map.get("channeldata");
			String action  = map.get("action");
			
			String param = url+"?"+PARAM3.replace("{action}",action).replace("{cpid}", cpid).replace("{serviceid}",serviceid).replace("{mode}",mode).replace("{imsi}",imsi).replace("{imei}",imei).replace("{time}", time).replace("random_key", key).replace("channeldata", channeldata);
			
			String responseXml = GetData.getData(url, param);
			logger.info("-----"+responseXml);
			xml = parseThird(map,responseXml);
			if(xml == null){
				
				return xml = DynamicUtils.parseError("598");
				
			}
			
		}
		
		return xml;
	}
	
private String parseThird(Map<String, String> map, String responseXml) {
	if(responseXml != null && responseXml.length() > 0){
		
		logger.info(responseXml);
		
		String return_code = SingleXmlUtil.getNodeValue(responseXml, "return_code");
			
		if (return_code != null && return_code.equals("0")) {
			Sets sets = new Sets();
			sets.setKey("_succ");
			sets.setValue("1");
			
			return XstreamHelper.toXml(sets).toString();
		}
		
		return DynamicUtils.parseError("597");
	}
	
	return null;
}
	public static void main(String[] args){
//		test1();
		test2();
		test3();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("theNo", "1");
		map.put("url", "http://182.92.149.179/open_gate/web_game_fee.php");
		map.put("type",TYPE);
		map.put("action", "init");
		map.put("imsi","460027223516113");
		map.put("imei","864668020179965");
		map.put("cpid","1234");
		map.put("serviceid", "4");
		map.put("mode","1");
		
		logger.info(new CdblDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://182.92.149.179/open_gate/web_game_callback.php");
		map.put("type",TYPE);
		map.put("action","sms");
		map.put("imsi","460027223516113");
		map.put("imei", "864668020179965");
		map.put("cpid","1234");
		map.put("serviceid", "4");
		map.put("mode", "1");
		
		
		logger.info(new CdblDynamicService().dynamic(map));
	}
	
	private static void test3(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "3");
		map.put("url", "http://182.92.149.179/open_gate/web_game_callback.php");
		map.put("type",TYPE);
		map.put("action","confirm");
		map.put("imsi","460027223516113");
		map.put("imei", "864668020179965");
		map.put("cpid","1234");
		map.put("serviceid", "4");
		map.put("mode","1");
		
		logger.info(new CdblDynamicService().dynamic(map));
	}
}
