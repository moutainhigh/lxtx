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
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 赞成联通vcc2
 * 1、http://182.92.149.179/open_gate/web_game_fee.php?pid=1001&money=1&time=1234567&imsi=46002xxxxxxxx&imei=86xxxxxxx&tel=**
{"resultCode":"200000","resultMsg":"成功","orderid":"0100000f30e2b04c0007c160",”appname”:”神仙道”,”apptype”:”vcc2.0”,”app”,”sxd”，” accessNo”:” 10655454”,”sms”:” wo&88149731e0ed75daa03a6e1e30427cddfa4ch3ci& 900001&8938057637279851”}

 * 2、 http://182.92.149.179/open_gate/web_game_callback.php?time=1234567&pid=1001&orderid=0200000f30e2a7910007b15d&verifycode=653262
{"resultCode":"201015","resultMsg":"XXXX"}
 * @author leoliu
 *
 */
public class ZcVcc2DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(SxdLtDynamicService.class);
	
	private static final String TYPE = "zcVcc2";
	
	private static final String PARAM1 = "pid={pid}&money={money}&time={time}&imsi={imsi}&imei={imei}&tel={tel}";
	private static final String PARAM2 = "time={time}&pid={pid}&orderid={orderid}&verifycode={verifycode}";
	
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
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String pid = map.get("pid");
			String money = map.get("money");
			String time = gettime();
			String tel = map.get("dmobile");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			
			String param = PARAM1.replace("{pid}", pid).replace("{money}",money).replace("{time}",time).replace("{tel}",tel).replace("{imsi}",imsi).replace("{imei}",imei);
			
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
				
				if(jo.has("resultCode")){
					String resultCode = jo.getString("resultCode");
					
					logger.info("parse first result code : "+resultCode);
					
					if(RESULTCODE_SUCC.equals(resultCode)){
						String orderid = jo.getString("orderid");
						
						Sets sets = new Sets();
						sets.setKey("orderid");
						sets.setValue(orderid);
						
						StringBuffer sb = XstreamHelper.toXml(sets);
						
						if(jo.has("accessNo")){
							String accessNo = jo.getString("accessNo");
							String smsContent = jo.getString("sms");
							
							Sms sms = new Sms();
							sms.setSmsDest(accessNo);
							sms.setSmsContent(smsContent);
							sms.setSuccessTimeOut(timeOut);
							
							sb.append(XstreamHelper.toXml(sms));
						}
						
						return sb.toString();
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
			String pid = map.get("pid");
			String time = gettime();
			
			String param = PARAM2.replace("{pid}", pid).replace("{verifycode}",verifycode).replace("{orderid}",orderid).replace("{time}",time);
			
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
		test2();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("theNo", "1");
		map.put("url", "http://182.92.149.179/open_gate/web_game_fee.php");
		map.put("type",TYPE);
		map.put("pid", "BJLX");
		map.put("money","2");
		map.put("dmobile","15590865736");
		map.put("imsi","460018994202331");
		map.put("imei", "865977029750158");
		
		logger.info(new ZcVcc2DynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://182.92.149.179/open_gate/web_game_callback.php");
		map.put("type",TYPE);
		map.put("pid","BJLX");
		map.put("orderid", "BJLX20150609160056867219");
		map.put("verifycode","266824");
		
		logger.info(new ZcVcc2DynamicService().dynamic(map));
	}
}
