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
public class ZcDdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZcDdoDynamicService.class);
	
	private static final String TYPE = "zcDdo";
	
	private static final String PARAM1 = "pid={pid}&app_special={app_special}&money={money}&time={time}&tel={tel}&imsi={imsi}&imei={imei}";
	private static final String PARAM2 = "pid={pid}&app_special={app_special}&time={time}&orderid={orderid}&verifycode={verifycode}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "200000";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
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
	
	public static String gettime(){
		String time = ""+System.currentTimeMillis()/1000;
		return time;
	}

	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String pid = map.get("pid");
			String money = map.get("money");
			String time = gettime();
			String tel = StringUtils.defaultString(map.get("dmobile"));
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String app_special = map.get("app_special");
			if(app_special == null){
				app_special = "";
			}
			
			String param = PARAM1.replace("{pid}", pid).replace("{app_special}",app_special).replace("{money}",money).replace("{time}",time).replace("{tel}",tel).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseJson = GetData.getData(url, param);
			logger.info("-----"+responseJson);
			xml = parseFirst(map,responseJson);			
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
				
				if(jo.has("resultCode")){
					String resultCode = jo.getString("resultCode");
					
					logger.info("parse first result code : "+resultCode);
					
					if(RESULTCODE_SUCC.equals(resultCode)){
						String orderid = jo.getString("orderid");
						
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
	
	private String secondDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String orderid = map.get("orderid");
		
		if(url != null && url.length() > 0){
			
			String verifycode = map.get("verifycode");
			String pid = map.get("pid");
			String app_special = map.get("app_special");
			if(app_special == null){
				app_special = "";
			}
			String time = gettime();
			
			String param = PARAM2.replace("{pid}", pid).replace("{app_special}",app_special).replace("{verifycode}",verifycode).replace("{orderid}",orderid).replace("{time}",time);
			
			String responseJson = GetData.getData(url, param);
			
			logger.info("responseJson2:"+responseJson);
			
			xml = parseSecond(map,responseJson);
			
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
				
				if(jo.has("resultCode")){
					String resultCode = jo.getString("resultCode");
					
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
		test1();
//		test2();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("theNo", "1");
		map.put("url", "http://182.92.149.179/open_gate/web_game_fee.php");
		map.put("type",TYPE);
		map.put("pid", "BJLX");
		map.put("app_special","3761");
		map.put("money","10");
		map.put("imsi","460014867444190");
		map.put("imei", "358574041772411");
		map.put("channel","13X265a123844860");
		map.put("dmobile", "15575871567");
		
		logger.info(new ZcDdoDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://182.92.149.179/open_gate/web_game_callback.php");
		map.put("type",TYPE);
		map.put("pid","BJLX");
		map.put("app_special","3001");
		map.put("orderid", "144740123282586304");
		map.put("verifycode","015999");
		
		logger.info(new ZcDdoDynamicService().dynamic(map));
	}
}
