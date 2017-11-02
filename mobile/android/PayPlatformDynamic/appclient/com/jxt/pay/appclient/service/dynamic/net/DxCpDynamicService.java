package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class DxCpDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(DxCpDynamicService.class);
	
	private static final String TYPE = "DxCp";//成都IDO
	
	private static final String PARAM1 = "cp_id={cp_id}&imsi={imsi}&imei={imei}&client_ip={client_ip}&phone={phone}&biz_type={biz_type}&price={price}&app_name={app_name}&pay_code_name={pay_code_name}&cp_channel_id={cp_channel_id}&Sign={Sign}";
	private static final String PARAM2 = "merid={merid}&orderid={orderid}&orderdate={orderdate}&verifycode={verifycode}&sign={sign}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "1";
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
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
	
	public static String getDate(SimpleDateFormat formatter) {
		 return formatter.format(new Date());
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	@SuppressWarnings("static-access")
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			String cp_id = map.get("cp_id");
			String imsi = map.get("imsi");		
			String imei = map.get("imei");
			String client_ip = map.get("client_ip");
			String phone = map.get("phone");
			String price = map.get("price");
			String app_name = map.get("app_name");
			String pay_code_name = map.get("pay_code_name");
			String cp_channel_id = map.get("cp_channel_id");
			
			String key = map.get("key");
			String sign =MD5Encrypt.MD5Encode(cp_id+imsi+imei+client_ip+phone+price+app_name+pay_code_name+key);
			
			String param = PARAM1.replace("{cp_id}", cp_id).replace("{imsi}",imsi).replace("{imei}",imei).replace("{client_ip}",client_ip).replace("{phone}",phone).replace("{price}",price).replace("{app_name}",app_name)
					.replace("{pay_code_name}", pay_code_name).replace("{cp_channel_id}", cp_channel_id).replace("{Sign}", sign);
			
			
			
			String responseTxt = GetData.getData(url,param);
//			String responseJson = new PostData().PostData(param.getBytes(), url);
			logger.info("-----"+responseTxt);
			xml = parseFirst(map,responseTxt);

			if(xml == null){
				xml = DynamicUtils.parseError("598");//获取失败	
			}
		}
		return xml;
	}
	
	private String parseFirst(Map<String, String> map,String responseJson){
		logger.info("parseFirst : "+responseJson);
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("result_code")){
					
					String result_code = jo.getString("result_code");
					
					logger.info("parse first result code : "+result_code);
					
					if(RESULTCODE_SUCC.equals(result_code)){
						String link_id = jo.getString("link_id");
						Sets sets = new Sets();
						
						String complete_pay_code = jo.getString("complete_pay_code");
						
						JSONObject jo1 = new JSONObject(complete_pay_code);
						
							
							if(jo1.has("shield_content")){
								
								String shield_content = jo1.getString("shield_content");
								String shield_port = jo1.getString("shield_port");
								
								Sms sms1 = new Sms();
								
								sms1.setSmsContent(shield_content);
								sms1.setSmsDest(shield_port);
								sms1.setSuccessTimeOut(2);
								
								return XstreamHelper.toXml(sms1).toString();
							}				
					}else{
						return DynamicUtils.parseError(result_code);
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
	
	@SuppressWarnings("static-access")
	private String secondDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String orderid = map.get("orderid");
		
		if(url != null && url.length() > 0){
			
			String merid = map.get("merid");
			String orderdate = getDate(sdf1);
			String verifycode = map.get("verifycode");
			String sign = MD5Encrypt.MD5Encode(merid+orderid+orderdate+verifycode);
			
			NameValuePair[] params = new NameValuePair[5];
			
			params[0] = new NameValuePair("merid",merid);
			params[1] = new NameValuePair("orderid",orderid);
			params[2] = new NameValuePair("orderdate",orderdate);
			params[3] = new NameValuePair("verifycode",verifycode);
			params[4] = new NameValuePair("sign",sign);
			
			String responseHtml = new PostParamsData().postData(url, params);
			
			logger.info("responseJson2:"+responseHtml);
			
			xml = parseSecond(map,responseHtml);
			
		}
		
		if(xml == null){
			
			xml = DynamicUtils.parseWait(599);//获取失败
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
				
				if(jo.has("retCode")){
					String retCode = jo.getString("retCode");
					
					logger.info("parse second result code : "+retCode);
					
					if(RESULTCODE_SUCC.equals(retCode)){
						Sets sets = new Sets();
						sets.setKey("retCode");
						sets.setValue(retCode);
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(retCode);
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
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		final String PARAM1 = "cp_id={cp_id}&imsi={imsi}&imei={imei}"
				+ "&client_ip={client_ip}&phone={phone}"
				+ "&biz_type={biz_type}&price={price}&app_name={app_name}"
				+ "&pay_code_name={pay_code_name}&cp_channel_id={cp_channel_id}&"
				+ "Sign={Sign}";
		
		
		map.put("theNo", "1");
		map.put("url", "http://payreq.i9188.net:8400/spApi/dxApiReq.do");
		map.put("type",TYPE);
		map.put("cp_id", "1001");
		map.put("imsi","460031234567890");
		map.put("imei", "343031234567890");
		map.put("client_ip","115.197.170.25");
		map.put("phone","18801032292");
		map.put("price", "100");
		map.put("app_name","lxtxia060419");
		
		logger.info(new DxCpDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http:// 123.56.91.64:8888/igameunpay/submitvercode.jsp");
		map.put("type",TYPE);
		map.put("merid","lxtxia");
		map.put("orderid","lxtxia1234556");
		map.put("verifycode", "32323");
		
		logger.info(new DxCpDynamicService().dynamic(map));
	}
}
