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
public class HyzxWtjDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HyzxWtjDynamicService.class);
	
	private static final String TYPE = "hyzxWtj";//沃淘金
	
	private static final String PARAM1 = "channelId={channelId}&province={province}&fee={fee}&imsi={imsi}&imei={imei}&mac={mac}";
	private static final String PARAM2 = "tradeid={tradeid}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "0";
	
	
	
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
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String channelId = map.get("channelId");
			String fee = map.get("fee");
//			String tel = StringUtils.defaultString(map.get("dmobile"));
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String mac = "98:6c:f5:2c:82:10";
			String province = map.get("province");
			
			String param = PARAM1.replace("{channelId}", channelId).replace("{province}",province).replace("{fee}",fee).replace("{imsi}",imsi).replace("{imei}",imei).replace("{mac}",mac);
			
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
				
				if(jo.has("rc")){
					String rc = jo.getString("rc");
					
					logger.info("parse first result code : "+rc);
					
					if(RESULTCODE_SUCC.equals(rc)){
						String tradeid = jo.getString("tradeid");
						
						Sets sets = new Sets();
						sets.setKey("tradeid");
						sets.setValue(tradeid);
						
						
						String num = jo.getString("num");
						String smsContent = jo.getString("sms");
						
						Sms sms = new Sms();
						sms.setSmsDest(num);
						sms.setSmsContent(smsContent);
						//sms.setSendType("2");
						
						return XstreamHelper.toXml(sets).append(XstreamHelper.toXml(sms)).toString();
					}
						
						
						return DynamicUtils.parseError(rc);
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
		
		
		if(url != null && url.length() > 0){
			
			
			String tradeid = map.get("tradeid");
			
			String param = PARAM2.replace("{tradeid}",tradeid);
			
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
				
				if(jo.has("rc")){
					String rc = jo.getString("rc");
					
					logger.info("parse second result code : "+rc);
					
					if(RESULTCODE_SUCC.equals(rc)){
						Sets sets = new Sets();
						sets.setKey("rc");
						sets.setValue(rc);
						
						return XstreamHelper.toXml(sets).toString();
					}
							return DynamicUtils.parseError(rc);
						
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
		map.put("url", "http://www.wosdk.cn/wosdk/wotaojinpay");
		map.put("type",TYPE);
		map.put("channelId", "3779");
		map.put("province","9");
		map.put("fee","600");
		map.put("imsi","460078010952058");
		map.put("imei", "867451025555753");
		map.put("channel","13X265a123844860");
		
		logger.info(new HyzxWtjDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://www.wosdk.cn/wosdk/wotaojinpay2");
		map.put("type",TYPE);
//		map.put("app_special","fk_wow");
		map.put("tradeid", "144740123282586304");
		
		logger.info(new HyzxWtjDynamicService().dynamic(map));
	}
}
