package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 
 * @author leoliu
 *
 */
public class FhSdkDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(FhSdkDynamicService.class);
	
	private static final String FIRSTREQUESTMODEL = "imei={imei}&imsi={imsi}&price={price}&chapterId={chapterId}&channel={channelId}&msgCallback=true";
	private static final String SECONDREQUESTMODEL = "msgId={msgId}";
	
	private static final String TYPE = "fhSdk";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else if("2".equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}
	
	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String xml = null;
		
		
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String price = map.get("price");
		String channelId = map.get("channelId");
		String chapterId = map.get("chapterId");
		String cpparam = map.get("channel");
		
		String script = FIRSTREQUESTMODEL.replace("{imsi}", imsi).replace("{imei}", imei).replace("{price}", price+"").replace("{chapterId}", chapterId).replace("{channelId}", channelId);
	
		logger.info("firstDynamic : "+script);
		
		String responseJson = GetData.getData(url, script);
		
		if(responseJson != null && responseJson.length() > 0){
			xml = parseFirst(responseJson);
			
			map1.remove(cpparam);
		}else{
			Integer cnt = map1.get(cpparam);
			
			if(cnt == null){
				cnt = 0;
			}
			
			if(cnt >= 2){
				map1.remove(cpparam);
				xml = DynamicUtils.parseError("599");
			}else{
				cnt ++;
				map1.put(cpparam, cnt);
				
				xml = DynamicUtils.parseWait(10,map);//获取失败
			}
		}
					
		return xml;
	}

	private String parseFirst(String responseJson){
		logger.info("parseFirst : "+responseJson);
		if(responseJson != null && responseJson.length() > 0){
			try {
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("status");
				
				if("0".equals(status)){
					String receiver = jo.getString("receiver");
					String message = jo.getString("message");
					
					String msgId = jo.getString("msgId");
					
					Sets sets = new Sets();
					sets.setKey("msgId");
					sets.setValue(msgId);
					
					Sms sms = new Sms();
					sms.setSmsDest(receiver);
					sms.setSmsContent(message);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					return XstreamHelper.toXml(sets).append(XstreamHelper.toXml(sms)).toString();
				}else{
					return DynamicUtils.parseError(status);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return null;
	}

	//记录重复请求
	private Map<String, Integer> tryMap = new HashMap<String, Integer>();
	
	private String secondDynamic(Map<String,String> map){
		String xml = null;
		String url = map.get("url");
		
		String msgId = map.get("msgId");
		
		String script = SECONDREQUESTMODEL.replace("{msgId}", msgId);
		
		logger.info("secondDynamic : "+script);
		
		String responseJson = GetData.getData(url, script);
		
		if(responseJson == null || responseJson.length() == 0){
			Integer tryCnt = tryMap.get(msgId);
			
			if(tryCnt == null){
				tryCnt = 0;
			}
			
			if(tryCnt >= 2){
				tryMap.remove(msgId);
				return DynamicUtils.parseError("599");
			}else{
				tryMap.put(msgId,tryCnt+1);
				xml = DynamicUtils.parseWait(10,map);//获取失败
			}
		}else{
			tryMap.remove(msgId);
			xml = parseSecond(responseJson);
		}
		
		return xml;
	}
		
	private String parseSecond(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("status");
				
				if("0".equals(status)){
					Sets sets = new Sets();
					sets.setKey("succ1");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(status);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
		
	@Override
	public String getType() {
		return TYPE;
	}

		
	public static void main(String[] args){
		
		test2();
	
	}
	
	
	private static void test1(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","fhSdk");
		map.put("theNo","1");
		map.put("url","http://vpay.jf55555.com:9999/client/api/v0/pay.api");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		map.put("price","800");
		map.put("chapterId","00000013");
		map.put("channelId","00000001");
		map.put("channel","10A201a101234567");
		
		String result = new FhSdkDynamicService().dynamic(map);
		
		System.out.println(result);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","fhSdk");
		map.put("theNo","2");
		map.put("url","http://vpay.jf55555.com:9999/client/api/v0/sms_send_callback.api");
		map.put("msgId","e050418b-548c-447f-a96a-4020f24a487a");
	
		String result = new FhSdkDynamicService().dynamic(map);
		
		System.out.println(result);
	}
}
