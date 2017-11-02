package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public class UpayZtsxDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(UpayZtsxDynamicService.class);
	
	private static final String TYPE = "upayZtsx";
	
	private static final String PARAM1 = "paykey={paykey}&imsi={imsi}&cp_param={cp_param}&cpid={cpid}";
		
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			String paykey = map.get("paykey");
			String imsi = map.get("imsi");
			String cp_param = map.get("cp_param");
			String cpid = map.get("cpid");
			
			String param = PARAM1.replace("{paykey}", paykey).replace("{imsi}",imsi).replace("{cpid}",cpid).replace("{cp_param}",cp_param);
			
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
				
				if(jo.has("serviceno")){
					String serviceno = jo.getString("serviceno");
					String msg = jo.getString("sms");
					
					if(serviceno != null && serviceno.length() > 0){
						
						if("2".equals(map.get("theNo"))){
							msg = CommonUtil.base64Decode(msg.replace(" ","\n"));
						}
						
						Sms sms = new Sms();
						sms.setSmsDest(serviceno);
						sms.setSmsContent(msg);
						sms.setSuccessTimeOut(2);
						
						return XstreamHelper.toXml(sms).toString();
					}else{
						return DynamicUtils.parseError("598");
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
//		test3();
	}
	
	private static void test3(){
		String s = "MDAwMDAwMDA4N1A4NTgvJjI2NDMzODI0ZjAwYTc0MzFNQzl5OTd3MH1GL0ZK aTVqMnluXUU2aFkgTWd3PT02bTtdNzU2fDdPNDY2Mm84MjApUm0wMTllMTUw NTZ3TTAwMHwwSDAwMDAwUTt1ckRWLGEzSHg0UGtkSypESCpRVmxkNTZfPg==";
		
		s = "MDAwMDAwMDA4MlQxMjEuIDMwNzQ1NTI0ZjAwYTAxMTRFRzNzNTl0a3FCOVMy bFRMWEVJMk9oRjd0a2F3PT03bDZSMjB4dTRIMTc1M2QxMjApUm0wMTllMTU4 MjF3TTAwMHwwSDAwMDAweVhMY3dBTnBZVks5eFdldzolaX0rNm55T0lpPg==";
		
		try {
			System.out.println(new String(Base64.decodeBase64(s),"utf-8"));
			System.out.println(CommonUtil.base64Decode(s.replace(" ","\n")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("url", "http://g.biedese.cn/vb/req_login_sms");
		map.put("type",TYPE);
		map.put("theNo","1");
		map.put("imsi","460022095595501");
		map.put("paykey","305800020030000-10664017-608125035");
		map.put("cp_param","LX13A301");
		map.put("cpid","894");
		
		logger.info(new UpayZtsxDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://g.biedese.cn/vb/req_sms_base64");
		map.put("type",TYPE);
		map.put("theNo","2");
		map.put("imsi","460022095595501");
		map.put("paykey","305800020030000-10664017-608125035");
		map.put("cp_param","LX13A301");
		map.put("cpid","894");
		logger.info(new UpayZtsxDynamicService().dynamic(map));
	}
}
