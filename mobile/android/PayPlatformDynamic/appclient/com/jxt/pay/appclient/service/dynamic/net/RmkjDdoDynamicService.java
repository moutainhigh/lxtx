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
public class RmkjDdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(RmkjDdoDynamicService.class);
	
	private static final String TYPE = "rmkjDdo";//融梦科技DDO
	
	private static final String PARAM1 = "client_app_key={client_app_key}&open_product_id={open_product_id}&user_mobile={user_mobile}&timestamp={timestamp}&version={version}&product_name={product_name}&out_trade_no={out_trade_no}&token={token}";
	private static final String PARAM2 = "client_app_key={client_app_key}&order_no={order_no}&auth_code={auth_code}&timestamp={timestamp}&token={token}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "0";
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
	
//	public static String getDate(SimpleDateFormat formatter) {
//		 return formatter.format(new Date());
//	}

	
	
	public static String gettime(){
		String time = ""+System.currentTimeMillis();
		return time;
	}
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			String client_app_key = "f45fd67983443c352c34da289d94c39c";
			String open_product_id = map.get("open_product_id");		
			String timestamp = gettime();
			String version = "3.0";
			String user_mobile = StringUtils.defaultString(map.get("user_mobile"));;
			String out_trade_no = map.get("out_trade_no");
			String product_name = "深圳融梦";
			String app_secret = "5215f153260bb50940109a10a31c41f3";
			logger.info("times:"+timestamp);
			String token =MD5Encrypt.MD5Encode(app_secret+timestamp).toLowerCase();
			
			
			logger.info("token:"+token);
			String param = PARAM1.replace("{client_app_key}", client_app_key).replace("{open_product_id}",open_product_id).replace("{timestamp}",timestamp)
					.replace("{version}",version).replace("{user_mobile}",user_mobile)
					.replace("{out_trade_no}",out_trade_no).replace("{product_name}",product_name).replace("{token}", token);
			
			
			
			String responseJson = GetData.getData(url,param);
//			String responseJson = new PostData().PostData(param.getBytes(), url);
			logger.info("-----"+responseJson);
			xml = parseFirst(map,responseJson);

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
				
				JSONObject jo1 = new JSONObject(responseJson);
				
				String response = jo1.getString("response");
				
				JSONObject jo = new JSONObject(response);
				
				if(jo.has("code")){
					
					String code = jo.getString("code");
					
					logger.info("parse first result code : "+code);
					
					if(RESULTCODE_SUCC.equals(code)){
						String order_no = jo.getString("order_no");
											
						Sets sets = new Sets();
						sets.setKey("order_no");
						sets.setValue(order_no);
									
						return XstreamHelper.toXml(sets).toString();
										
					}else{
						return DynamicUtils.parseError(code);
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
			
			String client_app_key = "f45fd67983443c352c34da289d94c39c";
			String order_no = map.get("order_no");		
			String timestamp = gettime();
			String auth_code=map.get("auth_code");
			String app_secret = "5215f153260bb50940109a10a31c41f3";
			logger.info("times:"+timestamp);
			String token =MD5Encrypt.MD5Encode(app_secret+timestamp).toLowerCase();
			
			
			logger.info("token:"+token);
			String param = PARAM2.replace("{client_app_key}", client_app_key).replace("{order_no}",order_no).replace("{timestamp}",timestamp)
					.replace("{auth_code}",auth_code).replace("{token}", token);
			
			
			
			String responseJson = GetData.getData(url,param);
//			String responseJson = new PostData().PostData(param.getBytes(), url);
			logger.info("-----"+responseJson);
			
			xml = parseSecond(map,responseJson);
			
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
				
				JSONObject jo1 = new JSONObject(responseJson);
				String response = jo1.getString("response");
				JSONObject jo = new JSONObject(response);
				
				if(jo.has("code")){
					String code = jo.getString("code");
					
					logger.info("parse second result code : "+code);
					
					if(RESULTCODE_SUCC.equals(code)){
						Sets sets = new Sets();
						sets.setKey("code");
						sets.setValue(code);
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(code);
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
		test2();
		
		
	}

	public static String exChange(String str){  
	    StringBuffer sb = new StringBuffer();  
	    if(str!=null){  
	        for(int i=0;i<str.length();i++){  
	            char c = str.charAt(i);  
	            if(Character.isUpperCase(c)){  
	                sb.append(Character.toLowerCase(c));  
	            }else if(Character.isLowerCase(c)){  
	                sb.append(Character.toUpperCase(c));   
	            }  
	        }  
	    }  
	      
	    return sb.toString();  
	}  
	
	
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("theNo", "1");
		map.put("url", "http://112.124.106.40:8080/smspay/generate_order.json");
		map.put("type",TYPE);
		map.put("open_product_id","1000");
		map.put("out_trade_no","0313A301");
		map.put("user_mobile","18801032292");
		
		
		logger.info(new RmkjDdoDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://112.124.106.40:8080/smspay/pay_confirm.json");
		map.put("type",TYPE);
		map.put("order_no","2S3ZEA14PS6b4Bpc");
		map.put("auth_code","551978");
		
		logger.info(new RmkjDdoDynamicService().dynamic(map));
	}
}
