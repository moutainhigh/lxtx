package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.qlzf.commons.helper.MD5Encrypt;

public class CdxmXezfDynamicService implements IDynamicService{

	private static final String TYPE="cdxmXezf";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	private static String RETURL = "http://www.baidu.com";
	
	private static Logger logger = Logger.getLogger(CdxmXezfDynamicService.class);
	
	static{
		sdf.applyPattern("yyyyMMddHHmmSS");
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else if("2".equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}
	
	private String firstDynamic(Map<String, String> map){
		
		String url = map.get("url");
		String merid = map.get("merid");
		String goodsid = map.get("goodsid");
		String mobileid = map.get("mobileid");
		String clientip = map.get(Constants.IPPARAM);
		String orderid = map.get("orderid");
		
		String orderdate = sdf.format(Calendar.getInstance().getTime());
		
		String platType = map.get("platType");
		String returl = RETURL;
		
		String key = map.get("key");
		String sign0 = merid+goodsid+mobileid+clientip+orderid+orderdate+platType+returl+key;
		String sign = MD5Encrypt.MD5Encode(sign0);
		
		NameValuePair[] pairs = new NameValuePair[9];
		pairs[0] = new NameValuePair("merid",merid);
		pairs[1] = new NameValuePair("goodsid",goodsid);
		pairs[2] = new NameValuePair("mobileid",mobileid);
		pairs[3] = new NameValuePair("clientip",clientip);
		pairs[4] = new NameValuePair("orderid",orderid);
		pairs[5] = new NameValuePair("orderdate",orderdate);
		pairs[6] = new NameValuePair("platType",platType);
		pairs[7] = new NameValuePair("returl",returl);
		pairs[8] = new NameValuePair("sign",sign);
		
		String responseJson = PostParamsData.postData(url, pairs);
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info("responseJson:"+responseJson);
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String retCode = jo.getString("retCode");
				
				if("0000".equals(retCode)){
					String paytype = jo.getString("paytype");
					
					if("YZM".equals(paytype)){
						Sets sets = new Sets();
						sets.setKey("YZM");
						sets.setValue("1");
						
						return XstreamHelper.toXml(sets).toString();
					}else if("SMS".equals(paytype)){
						StringBuffer sb = new StringBuffer();
						
						Sets sets = new Sets();
						sets.setKey("YZM");
						sets.setValue("0");
						
						sb.append(XstreamHelper.toXml(sets));
						
						String mo = jo.getString("mo");
						String called = jo.getString("called");
						
						Sms sms = new Sms();
						sms.setSmsDest(called);
						sms.setSmsContent(mo);
						sms.setSuccessTimeOut(2);
						
						sb.append(XstreamHelper.toXml(sms));
						
						if(jo.has("mo2")){
							String mo2 = jo.getString("mo2");
							String called2 = jo.getString("called2");
							
							if(mo2.length() > 0 && called2.length() > 0){
								sb.append("<wait>5</wait>");
								
								Sms sms1 = new Sms();
								sms1.setSmsDest(called2);
								sms1.setSmsContent(mo2);
								sms1.setSuccessTimeOut(2);
								
								sb.append(XstreamHelper.toXml(sms1));
							}
						}
						
						return sb.toString();
					}
					
				}else{
					return DynamicUtils.parseError(retCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	private String secondDynamic(Map<String, String> map){
		
		String url = map.get("url");
		
		String merid = map.get("merid");
		String orderid = map.get("orderid");
		String orderdate = sdf.format(Calendar.getInstance().getTime()).substring(0,8);
		String verifycode = map.get("verifycode");
		String key = map.get("key");
		
		String sign0 = merid+orderid+orderdate+verifycode+key;
		
		String sign = MD5Encrypt.MD5Encode(sign0);
		
		NameValuePair[] pairs = new NameValuePair[5];
		pairs[0] = new NameValuePair("merid",merid);
		pairs[1] = new NameValuePair("orderid",orderid);
		pairs[2] = new NameValuePair("orderdate",orderdate);
		pairs[3] = new NameValuePair("verifycode",verifycode);
		pairs[4] = new NameValuePair("sign",sign);
		
		String responseJson = PostParamsData.postData(url, pairs);
		
		logger.info("responseJson:"+responseJson);
		
		if(responseJson != null && responseJson.length() > 0){
			
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String retCode = jo.getString("retCode");
						
				if("0000".equals(retCode)){
					Sets sets = new Sets();
					sets.setKey("_succ");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(retCode);
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
		
		map.put("url","http://123.56.91.64:8888/igameunpay/req.jsp");
		map.put("type","cdxmXezf");
		map.put("theNo","1");
		map.put("merid","lxtxia");
		map.put("goodsid","100");
		map.put("mobileid","15271638188");
		map.put(Constants.IPPARAM,"114.244.131.225");
		map.put("orderid","lxtxia13B101a1234567892");
		map.put("platType","9");
		map.put("key","lxtxia060419");
		
		System.out.println(new CdxmXezfDynamicService().dynamic(map));
	}
	
	private static void test2(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://123.56.91.64:8888/igameunpay/submitvercode.jsp");
		map.put("type","cdxmXezf");
		map.put("theNo","2");
		map.put("merid","lxtxia");
		map.put("orderid","lxtxia13B101a1234567890");
		map.put("verifycode","620831");
		map.put("key","lxtxia060419");
		
		System.out.println(new CdxmXezfDynamicService().dynamic(map));
	}
	
}
