package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

public class BjjsPcDynamicService implements IDynamicService{

	private static final String TYPE = "bjjsPc";
	private static Logger logger = Logger.getLogger(BjjsPcDynamicService.class);
	
	private static final String PARAM1 = "app={app}&source={source}&money={money}&tel={tel}&sign={sign}";
	private static final String PARAM2 = "app={app}&source={source}&verifycode={verifycode}&orderid={orderid}&sign={sign}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = StringUtils.defaultString(map.get("theNo"));
		
		if("1".equals(theNo)){
			return dynamic1(map);
		}else if("2".equals(theNo)){
			return dynamic2(map);
		}
		
		return null;
	}
	
	private static Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String dynamic1(Map<String,String> map){
		
		String channel = StringUtils.defaultString(map.get("channel"));
		String url = map.get("url");
		String app = map.get("app");
		String source = map.get("source");
		String money = map.get("money");
		String tel = map.get("dmobile");
		String key = map.get("key");
		
		String sign = CommonUtil.base64Encode(tel+money+source+app+key).replace("\r","").replace("\n","");
		
		String params = PARAM1.replace("{app}", app).replace("{source}", source).replace("{money}", money).replace("{tel}", tel).replace("{sign}", sign);
		
		String responseJson = GetData.getData(url,params);
		
		String xml = parse1(responseJson);
		logger.info("xml1:"+xml);
		if(xml != null && xml.length() > 0){
			map1.remove(channel);
		}else{
			Integer cnt = map1.get(channel);
			
			if(cnt == null){
				cnt = 0;
			}
			
			if(cnt <= 2){
				map1.put(channel, cnt + 1);
				xml = DynamicUtils.parseWait(5,map);
			}else{
				map1.remove(channel);
				xml = DynamicUtils.parseError("599");
			}
		}
		
		return xml;	
	}
	
	private String parse1(String responseJson){
		if(responseJson != null && responseJson.length() > 0){
			
			try{
				logger.info("responseJson1:"+responseJson);
				
				JSONObject jo = new JSONObject(responseJson);
				
				String resultCode = jo.getString("resultCode");
				
				if("200000".equals(resultCode)){
					String orderid = jo.getString("orderid");
					
					Sets sets = new Sets();
					sets.setKey("orderid");
					sets.setValue(orderid);
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(resultCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	private static Map<String, Integer> map2 = new HashMap<String, Integer>();
	
	private String dynamic2(Map<String,String> map){
		String url = map.get("url");
		String app = map.get("app");
		String source = map.get("source");
		String verifycode = map.get("verifycode");
		String orderid = map.get("orderid");
		String key = map.get("key");
		String sign = CommonUtil.base64Encode(orderid+source+app+key).replace("\r","").replace("\n",""); 
		
		String params = PARAM2.replace("{app}", app).replace("{source}", source).replace("{verifycode}", verifycode).replace("{orderid}", orderid).replace("{sign}", sign);
		
		String responseJson = GetData.getData(url,params);
		
		String xml = parse2(responseJson);
		logger.info("xml2:"+xml);
		if(xml != null && xml.length() > 0){
			map2.remove(orderid);
		}else{
			Integer cnt = map2.get(orderid);
			
			if(cnt == null){
				cnt = 0;
			}
			
			if(cnt <= 2){
				map2.put(orderid, cnt + 1);
				xml = DynamicUtils.parseWait(5,map);
			}else{
				map2.remove(orderid);
				xml = DynamicUtils.parseError("599");
			}
		}
		
		return xml;
	}
	
	private String parse2(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				logger.info("responseJson2:"+responseJson);
				
				String resultCode = jo.getString("resultCode");
				
				if("200000".equals(resultCode)){
					Sets sets = new Sets();
					sets.setKey("_succRet");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
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
		test1();
//		test2();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","bjjsPc");
		map.put("theNo","1");
		map.put("url","http://123.59.59.151/openthird/sendsms");
		map.put("app","xmywebkhd");
		map.put("source","xmjx");
		map.put("money","10");
		map.put("dmobile","13811155779");
		map.put("key","3c8989f78fab481d87f43ecedca55aa7");
		
		String xml = new BjjsPcDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","bjjsPc");
		map.put("theNo","2");
		map.put("url","http://123.59.59.151/openthird/confirm");
		map.put("app","xmywebkhd");
		map.put("source","xmjx");
		map.put("verifycode","851526");
		map.put("orderid","0700000f06bf00f1001b715b");
		map.put("key","3c8989f78fab481d87f43ecedca55aa7");
		
		String xml = new BjjsPcDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
	
	
}
