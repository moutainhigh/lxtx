package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.jxt.pay.appclient.utils.StringUtils;

/**

请求计费地址：
 
http://115.159.63.63:8090/SMS/RequestParam	 
请求方式：Http/POST
请求参数：
{"IMSI":"460020237985977","Chn":"CL01","type":"2","IMEI":"358489031512489","key":0,"exdata":"DFY1423540939526"}

返回 
{"spno":"10658424","msg":"MM#WLAN#70hy+iYOXtlwMa8lGr5qVA==#908029226#399900002400","status":"1","tid":"195021"}	 


 * @author leoliu
 *
 */
public class BjjsSmm2DynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(BjjsSmm2DynamicService.class);
	
	private static final String TYPE = "bjjsSmm2";
	
	private static final String PARAMS = "{\"IMSI\":\"{imsi}\",\"Chn\":\"{chn}\",\"type\":\"2\",\"IMEI\":\"{imei}\",\"key\":0,\"exdata\":\"{exdata}\"}";
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	private Map<String, Integer> tryMap = new HashMap<String, Integer>();

	@Override
	public String dynamic(Map<String, String> map) {
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			Integer tryCount = tryMap.get(channel);
			
			if(tryCount == null){
				tryCount = 1;
				tryMap.put(channel,tryCount);
			}else{
				if(tryCount >= 3){
					tryMap.remove(channel);
					return DynamicUtils.parseError("599");
				}

				tryMap.put(channel,tryCount+1);
			}
			
			String responseJson = "";
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String chn = StringUtils.defaultString(map.get("chn"));
			
//			NameValuePair[] params = new NameValuePair[6];
//			params[0] = new NameValuePair("IMSI",imsi);
//			params[1] = new NameValuePair("IMEI",imei);
//			params[2] = new NameValuePair("Chn",chn);
//			params[3] = new NameValuePair("type","2");
//			params[4] = new NameValuePair("key", "0");
//			params[5] = new NameValuePair("exdata", channel);
//			
//			responseJson = PostParamsData.postData(url,params);
			
			String params = PARAMS.replace("{imsi}", imsi).replace("{imei}", imei).replace("{chn}", chn).replace("{exdata}", channel);
	
			responseJson = new PostData().PostData(params.getBytes(), url);
			
			xml = parseXml(map,responseJson);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				if(tryCount >= 3){
					xml = DynamicUtils.parseError("597");
					tryMap.remove(channel);
				}else{
					xml = DynamicUtils.parseWait(10,map);
				}
			}
		}
		
		return xml;
	}
	
	private String parseXml(Map<String, String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info("bjjsSmm2 responseJson : "+responseJson);
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("status");
				
				if("1".equals(status)){
					Sms sms = new Sms();
					
					sms.setSmsContent(jo.getString("msg"));
					sms.setSmsDest(jo.getString("spno"));
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
					Sms guardSms = new Sms();
								
					List<Guard> guardList = new ArrayList<Guard>();
					
					guardList.add(guard1);
					guardList.add(guard2);
							
					guardList.add(guard3);
					guardList.add(guard4);
					
					guardSms.setGuardList(guardList);
					
					
					return XstreamHelper.toXml(sms).toString()+XstreamHelper.toXml(guardSms).toString();
				}
				
			}catch(Exception e){
				
			}
		}

		return null;
	}
	
	
	public static void main(String[] args){
		
		test2();
		
//		System.out.println(fetchSid("MM#WLAN#KjGDL0HmLvO27awoEZxqQw==#1022273555#399900003000"));
	}
	
	private static void test2(){
//		http://124.172.237.77/MMSQLWMessage/getqlmocontent?appid=3038489&imei=862949029214504&imsi=460022101441340&paycode=30000873430704&cpid=36&chid=3003954714
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://115.159.63.63:8090/SMS/RequestParam");
		map.put("imei","434805963967915");
		map.put("imsi","460005852233945");
		map.put("channel","10B201a087654321");
		map.put("chn", "CL10");
		map.put("type","bjjsSmm2");
		
		System.out.println(new BjjsSmm2DynamicService().dynamic(map));
	}
	
}
