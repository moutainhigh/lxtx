package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class UpayVideoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(UpayVideoDynamicService.class);
	
	private static final String TYPE = "upayVideo";
	
	private static final String PARAM1 = "channel={channel}&flag={flag}&imsi={imsi}&cpparam={cpparam}";
	private static final String PARAM2 = "channel={channel}&flag={flag}&imsi={imsi}&billid={billid}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
		
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
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			String channelid = map.get("channelid");
			String flag = map.get("flag");
			String imsi = map.get("imsi");
			String cpparam = StringUtils.defaultString(map.get("cpparam"));
			
			String param = PARAM1.replace("{channel}", channelid).replace("{flag}",flag).replace("{imsi}",imsi).replace("{cpparam}",cpparam);
			
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
				
				if(jo.has("result")){
					int result = jo.getInt("result");
					
					if(0 == result){
						String billid = jo.getString("billid");
						
						Sets sets = new Sets();
						sets.setKey("billid");
						sets.setValue(billid);
						
						Sms sms = new Sms();
						sms.setSmsDest(jo.getString("port"));
						sms.setSmsContent(jo.getString("sms"));
						sms.setSuccessTimeOut(2);
						
						return XstreamHelper.toXml(sets).append(XstreamHelper.toXml(sms)).toString();
					}else{
						return DynamicUtils.parseError(result+"");
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
		
		if(url != null && url.length() > 0){
			String billid = map.get("billid");
			String channelid = map.get("channelid");
			String flag = map.get("flag");
			String imsi = map.get("imsi");
			
			String param = PARAM2.replace("{billid}", billid).replace("{channel}",channelid).replace("{flag}", flag).replace("{imsi}",imsi);
			
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
				
				if(jo.has("result")){
					int result = jo.getInt("result");
					
					logger.info("parse second result code : "+result);
					
					if(0 == result){
						Sms sms = new Sms();
						sms.setSmsDest(jo.getString("port"));
//						sms.setSmsContent(new String(Base64.decodeBase64(jo.getString("sms")),"utf-8"));
						
						sms.setSmsContent(CommonUtil.base64Decode(jo.getString("sms").replace(" ","\n")));
						
						sms.setSuccessTimeOut(2);
						
						return XstreamHelper.toXml(sms).toString();
					}else{
						return DynamicUtils.parseError(result+"");
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
//		test2();
//		test3();
	}

	private static void test3(){
		String s = "MDAwMDAwMDA4NlA1OTYnLzg4NTI5Njg0ZjAwYTk1NzBOQTd0NTB0a3FCOVMy bFRMWEVJMk9oRjd0a2F3PT0zbD5SNjh+fThCNzExOWo1MjApUm0wMTllMTE5 MTN3TTAwMHwwSDAwMDAwR1B5dkt1TXFCUDk9Ri9ueSpTVDZIeFZmVUppPg\u003d\u003d";
		
		try {
			System.out.println(new String(Base64.decodeBase64(s),"utf-8"));
			System.out.println(CommonUtil.base64Decode(s.replace(" ", "\n")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("theNo", "1");
		map.put("url", "http://vpp.wangbit.net/billPrehold");
		map.put("type",TYPE);
		map.put("imsi","460021283116601");
		map.put("channelid", "10009");
		map.put("flag","1000");
		map.put("cpparam","LX10B101a123456784");
		
		logger.info(new UpayVideoDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://vpp.wangbit.net/billSecondPrehold");
		map.put("type",TYPE);
		map.put("imsi","460021283116601");
		map.put("channelid", "10009");
		map.put("flag","1000");
		map.put("billid","20160519191917760723");
		
		logger.info(new UpayVideoDynamicService().dynamic(map));
	}
}
