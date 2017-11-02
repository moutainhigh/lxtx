package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 掌智通页游
 * @author leoliu
 *1、
 *请求 http://api.datacomx.com/PCGameSms/FirDataImp?app=abc&tel=13866668888&consumecode=000082639001&time=1398496458&userid=1&price=1
 *响应 {"resultCode":"200000","resultMsg":"Success","orderid":"1500000f311eb055004bcf0a"}

  2、
      请求 http://api.datacomx.com/PCGameSms/SecDataImp?app=abc&tel=13866668888&consumecode=000082639001&verifycode=127023&orderid=0100000f19668e01000d011d&time=1398496603&userid=1&price=1
      响应 {"resultCode":"200000","resultMsg":"success"}
 *
 */
public class HhkjYyDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjYyDynamicService.class);
	
	private static final String TYPE = "hhkjYy";
	
	private static final String PARAM1 = "pid={pid}&imsi={imsi}&imei={imei}";
	private static final String PARAM2 = "imsi={imsi}&smscontent={smscontent}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
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
		}		
		
		return null;
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String pid = map.get("pid");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			
			String param = PARAM1.replace("{pid}", pid).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parseFirst(map,responseJson);

			if(xml == null){
				Integer cnt = map1.get(channel);
				
				if(cnt == null){
					cnt = 0;
				}
				
				if(cnt >= 3){
					map1.remove(channel);
					xml = DynamicUtils.parseError("598");
				}else{
					cnt ++;
					map1.put(channel, cnt);
					
					xml = DynamicUtils.parseWait(10,map);//获取失败
				}
			}else{
				map1.remove(channel);
			}
			
		}
		
		return xml;
	}
	
	private String parseFirst(Map<String,String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info("parseFirst : "+responseJson);
			
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				String result = jo.getString("result");
				
				logger.info("parse first result code : "+result);
				
				if("1".equals(result)){
					Sms sms = new Sms();
					sms.setSmsDest(jo.getString("smsnumber"));
					sms.setSmsContent(jo.getString("smscontent"));
					sms.setSuccessTimeOut(2);
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError(result);
				}
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
		
		if(url != null && url.length() > 0){
			String imsi = map.get("imsi");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(imsi);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(imsi,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * 60){
					tryMap.remove(imsi);
					return DynamicUtils.parseError("599");
				}
			}
			
			String verifycode = map.get("verifycode");
			
			String param = PARAM2.replace("{imsi}", imsi).replace("{smscontent}",verifycode);
			
			String responseJson = GetData.getData(url, param);
			
			logger.info("responseJson2:"+responseJson);
			
			xml = parseSecond(map,responseJson);
			
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(10,map);//获取失败
		}
		
		return xml;
	}

	
	private String parseSecond(Map<String, String> map, String responseJson) {
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
				
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				String result = jo.getString("result");
				
				logger.info("parse second result code : "+result);
				
				if("1".equals(result)){
					Sets sets = new Sets();
					sets.setKey("resultCode1");
					sets.setValue(result);
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(result);
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
		
		map.put("theNo", "1");
		map.put("type","hhkjYy");
		map.put("url", "http://220.194.56.98/kyip/jsps/sms/lz/pccharge.jsp");
		map.put("pid", "PC1000kyA5");
		map.put("imsi", "460001123143655");
		map.put("imei","867376023133651");
		map.put("channel","10B201a012345678");
		
		System.out.println(new HhkjYyDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("type","hhkjYy");
		map.put("url", "http://220.194.56.98/kyip/jsps/sms/lz/pcverify.jsp");
		
		map.put("imsi", "460001123143655");
		map.put("verifycode","686403");
		
		System.out.println(new HhkjYyDynamicService().dynamic(map));
	}
}
