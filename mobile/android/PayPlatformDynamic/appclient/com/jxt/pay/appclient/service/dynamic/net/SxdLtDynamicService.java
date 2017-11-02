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
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 赞成联通web
 * 1、http://vcc.geiliyou.com/web_gate_gly/fee.php?pid=BJLX&svcid=BJLXCX&paymentUser=18500973532&&imsi=460010970503904&imei=860806025781740sign=eab7745a880ea2ff956d8f2c5aa7705a
 * 2、http://vcc.geiliyou.com/web_gate_gly/callback.php?pid=BJLX&svcid=BJLXCX&paymentUser=18500973532&outTradeNo=BJLX120141107122932827963&paymentcodesms=651921&timeStamp=20130128023312&sign=ac3cd31fa903e1a8166f0d14319f6bb8
 * @author leoliu
 *
 */
public class SxdLtDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(SxdLtDynamicService.class);
	
	private static final String TYPE = "sxdLt";
	
	private static final String PARAM1 = "pid={pid}&svcid={svcid}&imsi={imsi}&imei={imei}&sign={sign}&paymentUser={paymentUser}";
	private static final String PARAM2 = "pid={pid}&svcid={svcid}&outTradeNo={outTradeNo}&paymentcodesms={paymentcodesms}&timeStamp={timeStamp}&sign={sign}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "0";
	
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

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String svcid = map.get("svcid");
			String pid = map.get("pid");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String key = map.get("key");
			String paymentUser = map.get("dmobile");
			
			if(paymentUser == null){
				paymentUser = "";
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append("pid=").append(pid).append("&svcid=").append(svcid).append("&key=").append(key);
			
			String src = sb.toString();
			logger.info("src : "+src);
			
			String sign = MD5Encrypt.MD5Encode(src).toLowerCase();
			logger.info("sign : "+sign);
			
			
			String param = PARAM1.replace("{pid}", pid).replace("{svcid}",svcid).replace("{imsi}",imsi).replace("{imei}",imei+"").replace("{sign}",sign).replace("{paymentUser}",paymentUser);
			
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
				
				if(jo.has("resultCode")){
					String resultCode = jo.getString("resultCode");
					
					logger.info("parse first result code : "+resultCode);
					
					if(RESULTCODE_SUCC.equals(resultCode)){
						String orderid = jo.getString("outTradeNo");
						
						Sets sets = new Sets();
						sets.setKey("orderid");
						sets.setValue(orderid);
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(resultCode);
					}
				}else{
					String errorCode = jo.getString("errorCode");
					
					return DynamicUtils.parseError(errorCode);
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
			String outTradeNo = map.get("orderid");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(outTradeNo);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(outTradeNo,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(outTradeNo);
					return DynamicUtils.parseError("599");
				}
			}
			
			
			String svcid = map.get("svcid");
			String pid = map.get("pid");
			String key = map.get("key");

			String paymentcodesms = map.get("verifycode");
			
			String timeStamp = sdf.format(new Date());
			
			StringBuffer sb = new StringBuffer();
			sb.append("outTradeNo=").append(outTradeNo).append("&paymentcodesms=").append(paymentcodesms);
			sb.append("&timeStamp=").append(timeStamp).append("&key=").append(key);
			
			String src = sb.toString();
			logger.info("src : "+src);
			
			String sign = MD5Encrypt.MD5Encode(src).toLowerCase();
			logger.info("sign : "+sign);
			
			String param = PARAM2.replace("{pid}", pid).replace("{svcid}",svcid).replace("{timeStamp}",timeStamp+"").replace("{sign}",sign);
			param = param.replace("{outTradeNo}", outTradeNo).replace("{paymentcodesms}",paymentcodesms);
			
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
				}else{
					return DynamicUtils.parseError(jo.getString("errorCode"));
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
		map.put("url", "http://119.254.35.173/web_gate/fee_req.php");
		map.put("key", "bce451618c93a32e69e7a884504d994c");
		map.put("svcid","LX0106");
		map.put("pid","BJLX");
		map.put("imsi","460014476218600");
		map.put("imei", "865977029750158");
		map.put("dmobile","13126634025");
		
		System.out.println(new SxdLtDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://119.254.35.173/web_gate/fee_confirm.php");
		map.put("key", "bce451618c93a32e69e7a884504d994c");
		map.put("svcid","LX0106");
		map.put("pid","BJLX");
		
		map.put("orderid", "BJLX20150128152800724147");
		map.put("verifycode","856771");
		
		System.out.println(new SxdLtDynamicService().dynamic(map));
	}
}
