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
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class ZcDxAcyDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZcDxAcyDynamicService.class);
	
	private static final String TYPE = "zcDxAcy";//爱冲印
	
	private static final String PARAM1 = "pid={pid}&app_special={app_special}&money={money}&time={time}&tel={tel}&imsi={imsi}&imei={imei}&iccid={iccid}";
	private static final String PARAM2 = "pid={pid}&time={time}&orderid={orderid}";
	
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

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			String pid = map.get("pid");
			String money = map.get("money");
			String time = gettime();
			String tel = StringUtils.defaultString(map.get("dmobile"));
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String iccid = StringUtils.defaultString(map.get("iccid"));
			String app_special = map.get("app_special");
			if(app_special == null){
				app_special = "";
			}
			
			String param = PARAM1.replace("{pid}", pid).replace("{app_special}",app_special).
					replace("{money}",money).replace("{time}",time).replace("{tel}",tel)
					.replace("{imsi}",imsi).replace("{imei}",imei)
					.replace("{iccid}", iccid);
			
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
//						String orderid = jo.getString("orderid");
						
//						Sets sets = new Sets();
//						sets.setKey("orderid"); 
//						sets.setValue(orderid);
						
						
						String accessNo = jo.getString("accessNo");
						String smsContent = jo.getString("sms");
						
						Sms sms = new Sms();
						sms.setSmsDest(accessNo);
						sms.setSmsContent(smsContent);
						sms.setSuccessTimeOut(2);
						
						return XstreamHelper.toXml(sms).toString();
					}
						
						
						return DynamicUtils.parseError(resultCode);
					//}
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
			
			String pid = map.get("pid");
//			String app_special = map.get("app_special");
//			if(app_special == null){
//				app_special = "";
//			}
			String time = gettime();
			
			String param = PARAM2.replace("{pid}", pid).replace("{orderid}",orderid).replace("{time}",time);
			
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
					}
							return DynamicUtils.parseError(resultCode);
						
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
		map.put("app_special","ty_pay");
		map.put("money","20");
		map.put("imsi","460037751007929");
		map.put("imei", "A10000423B4E56");
		map.put("iccid", "89860316847912085580");
		map.put("dmobile", "18178915835");
		logger.info(new ZcDxAcyDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://182.92.149.179/open_gate/web_game_callback.php");
		map.put("type",TYPE);
		map.put("pid","BJLX");
//		map.put("app_special","fk_wow");
		map.put("orderid", "144740123282586304");
		
		logger.info(new ZcDxAcyDynamicService().dynamic(map));
	}
}
