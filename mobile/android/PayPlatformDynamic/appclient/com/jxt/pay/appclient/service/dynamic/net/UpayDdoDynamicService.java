package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class UpayDdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(UpayDdoDynamicService.class);
	
	private static final String TYPE = "upayDdo";
	
	private static final String PARAM1 = "app_key={app_key}&mobile={mobile}&order_id={order_id}&amount={amount}&imsi={imsi}&imei={imei}&iccid={iccid}&ch_key={ch_key}";
	private static final String PARAM2 = "order_id={order_id}&code={code}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "200";
	
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
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String app_key = map.get("app_key");
			String amount = map.get("amount");
			String order_id = channel;
			String mobile = StringUtils.defaultString(map.get("dmobile"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String iccid = StringUtils.defaultString(map.get("iccid"));
			String chan = StringUtils.defaultString(map.get("chan"));
			
			String param = PARAM1.replace("{app_key}", app_key).replace("{mobile}",mobile).replace("{order_id}",order_id).replace("{amount}",amount).replace("{imsi}",imsi).replace("{imei}",imei).replace("{iccid}",iccid).replace("{ch_key}",chan);
			
			String responseJson = GetData.getData(url, param);
			logger.info("-----"+responseJson);
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
				
				if(jo.has("result")){
					String resultCode = jo.getString("result");
					
					logger.info("parse first result code : "+resultCode);
					
					if(RESULTCODE_SUCC.equals(resultCode)){
						String orderid = jo.getString("order_id");
						
						Sets sets = new Sets();
						sets.setKey("orderid");
						sets.setValue(orderid);
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(resultCode);
					}
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
		String orderid = map.get("orderid");
		
		if(url != null && url.length() > 0){
			
			String verifycode = map.get("verifycode");
			
			String param = PARAM2.replace("{order_id}", orderid).replace("{code}",verifycode);
			
			String responseJson = GetData.getData(url, param);
			
			logger.info("responseJson2:"+responseJson);
			
			xml = parseSecond(map,responseJson);
			
		}
		
		if(xml == null){

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
			
			xml = DynamicUtils.parseWait(10,map);//获取失败
		}else{
			tryMap.remove(orderid);
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
				
				if(jo.has("result")){
					String resultCode = jo.getString("result");
					
					logger.info("parse second result code : "+resultCode);
					
					if(RESULTCODE_SUCC.equals(resultCode)){
						Sets sets = new Sets();
						sets.setKey("resultCode1");
						sets.setValue(resultCode);
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(resultCode);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void main(String[] args){
//		test1();
		test2();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("theNo", "1");
		map.put("url", "http://api.upay360.cn/api/plan/exec_pay");
		map.put("type",TYPE);
		map.put("app_key", "10001304");
		map.put("dmobile","13811155779");
		map.put("amount","20");
		map.put("imsi","460001123143655");
		map.put("imei", "867376023133651");
		map.put("chan","10B101");
		map.put("channel","10B101a123456784");
		
		logger.info(new UpayDdoDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://api.upay360.cn/api/plan/simple_conf");
		map.put("type",TYPE);
		map.put("orderid", "10B101a123456784");
		map.put("verifycode","424716");
		
		logger.info(new UpayDdoDynamicService().dynamic(map));
	}
}
