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
public class ZztTtxcDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztTtxcDynamicService.class);
	
	private static final String TYPE = "ZztTtxc";
	
	private static final String PARAM1 = "goodsid={goodsid}&mobileid={mobileid}&orderid={orderid}&amount={amount}";
	private static final String PARAM2 = "orderid={orderid}&verifycode={verifycode}";	
	
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
			String goodsid = map.get("goodsid");
			String mobileid = map.get("mobileid");
			String orderid = map.get("orderid");
			String amount = map.get("amount");
			
			String param =PARAM1.replace("{goodsid}", goodsid).replace("{mobileid}",mobileid).replace("{orderid}",orderid).replace("{amount}",amount);
			
			String responseXml = GetData.getData(url+"?"+param);
			logger.info("-----"+responseXml);

			if(responseXml != null && responseXml.length() > 0){
				xml = parseFirst(map,responseXml);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
			
		}
		
		return xml;
	}
	
	private String parseFirst(Map<String,String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				
				if(jo.has("msg")){
					String resultCode = jo.getString("msg");
					
					logger.info("parse first result code : "+resultCode);
											
						Sets sets = new Sets();
						sets.setKey("_succ");
						sets.setValue("1");
						
						return XstreamHelper.toXml(sets).toString();
					
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
			
			String verifycode = map.get("verifycode");
			String orderid = map.get("orderid");
			
			String param = url+"?"+PARAM2.replace("{verifycode}", verifycode).replace("{orderid}",orderid);
			
			String responseJson = GetData.getData(param);
			
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
				
				
				
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("msg")){
					String resultCode = jo.getString("msg");
					
					logger.info("parse second result code : "+resultCode);
					
					
						Sets sets = new Sets();
						sets.setKey("_succ");
						sets.setValue("1");
						
						return XstreamHelper.toXml(sets).toString();
					
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
		map.put("url", "http://cc.spinterface.3gshow.cn/huayalefu/getorder.ashx");
		map.put("type",TYPE);
		map.put("goodsid", "020");
		map.put("mobileid","18801032292");
		map.put("orderid","099_test879484222");
		map.put("amount","200");
		
		logger.info(new ZztTtxcDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://cc.spinterface.3gshow.cn/huayalefu/confirmpay.ashx");
		map.put("type",TYPE);
		map.put("orderid", "099_test879484222");
		map.put("verifycode","015999");
		
		logger.info(new ZztTtxcDynamicService().dynamic(map));
	}
}
