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
public class ZztYyDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztYyDynamicService.class);
	
	private static final String TYPE = "zztYy";
	
	private static final String PARAM1 = "app={app}&tel={tel}&consumecode={consumecode}&time={time}&userid={userid}&price={price}";
	private static final String PARAM2 = "app={app}&tel={tel}&consumecode={consumecode}&verifycode={verifycode}&orderid={orderid}&time={time}&userid={userid}&price={price}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "200000";
	private static final String RESULTCODE_WAIT = "200002";
	
	private int timeOut = 60;
	
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
		String mobile = map.get("dmobile");
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String app = map.get("app");
			String consumecode = map.get("consumecode");
			String price = map.get("price");
			String userid = map.get("userid");
			
			long time = System.currentTimeMillis()/1000;
		
			String param = PARAM1.replace("{app}", app).replace("{consumecode}",consumecode).replace("{price}",price).replace("{time}",time+"").replace("{userid}",userid);
			param = param.replace("{tel}", mobile);
			
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
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				String resultCode = jo.getString("resultCode");
				
				logger.info("parse first result code : "+resultCode);
				
				if(RESULTCODE_SUCC.equals(resultCode)){
					String orderid = jo.getString("orderid");
					
					Sets sets = new Sets();
					sets.setKey("orderid");
					sets.setValue(orderid);
					
					return XstreamHelper.toXml(sets).toString();
				}else if(RESULTCODE_WAIT.equals(resultCode)){
					return null;
				}else{
					return DynamicUtils.parseError(resultCode);
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
			String orderid = map.get("orderid");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(orderid);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(orderid,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(orderid);
					return DynamicUtils.parseError("599");
				}
			}
			
			String app = map.get("app");
			String consumecode = map.get("consumecode");
			String price = map.get("price");
			String userid = map.get("userid");
			String mobile = map.get("dmobile");
			
			long time = System.currentTimeMillis()/1000;
			
			String verifycode = map.get("verifycode");
			
			String param = PARAM2.replace("{app}", app).replace("{consumecode}",consumecode).replace("{price}",price).replace("{time}",time+"").replace("{userid}",userid);
			param = param.replace("{tel}", mobile);
			param = param.replace("{orderid}", orderid).replace("{verifycode}",verifycode);
			
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
				
				String resultCode = jo.getString("resultCode");
				
				logger.info("parse second result code : "+resultCode);
				
				if(RESULTCODE_SUCC.equals(resultCode)){
					Sets sets = new Sets();
					sets.setKey("resultCode1");
					sets.setValue(resultCode);
					
					return XstreamHelper.toXml(sets).toString();
				}else if(RESULTCODE_WAIT.equals(resultCode)){
					return null;
				}else{
					return DynamicUtils.parseError(resultCode);
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
		map.put("type","zztYy");
		map.put("url", "http://api.datacomx.com/PCGameSms/FirDataImp");
		map.put("price", "800");
		map.put("consumecode", "000085309037");
		map.put("app","xrz");
		map.put("dmobile", "13843781637");
		map.put("userid", "1");
		map.put("channel","10B201a012345678");
		
		System.out.println(new ZztYyDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("type","zztYy");
		map.put("url", "http://api.datacomx.com/PCGameSms/SecDataImp");
		map.put("price", "800");
		map.put("consumecode", "000085309037");
		map.put("app","xrz");
		map.put("dmobile", "13843781637");
		map.put("userid", "1");
		
		map.put("orderid", "0700000f23d4424600363656");
		map.put("verifycode","674315");
		
		System.out.println(new ZztYyDynamicService().dynamic(map));
	}
}
