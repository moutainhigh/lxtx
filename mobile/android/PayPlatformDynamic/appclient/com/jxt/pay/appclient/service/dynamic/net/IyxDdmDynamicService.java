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
public class IyxDdmDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(IyxDdmDynamicService.class);
	
	private static final String TYPE = "IyxDdm";//爱游戏短代码
	
	private static final String PARAM1 = "cp_order_id={cp_order_id}&game_code={game_code}&consume_code={consume_code}&fee={fee}&channel_code={channel_code}&phone={phone}&sign_msg={sign_msg}";
	private static final String PARAM2 = "merid={merid}&orderid={orderid}&orderdate={orderdate}&verifycode={verifycode}&sign={sign}";
	
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
	
	public static String getDate(SimpleDateFormat formatter) {
		 return formatter.format(new Date());
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	@SuppressWarnings("static-access")
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			String cp_order_id = map.get("cp_order_id");
			String game_code = map.get("game_code");		
			String consume_code = map.get("consume_code");
			String fee = map.get("fee");
			String channel_code = map.get("channel_code");
			String app_key = map.get("app_key");
			String phone = map.get("phone");
			String sign_msg =MD5Encrypt.MD5Encode(cp_order_id+game_code+consume_code+fee+phone+channel_code+app_key);
			
			String param = PARAM1.replace("{cp_order_id}", cp_order_id)
					.replace("{game_code}",game_code).replace("{consume_code}",consume_code).replace("{fee}",fee)
					.replace("{channel_code}",channel_code).replace("{phone}",phone).replace("{sign_msg}",sign_msg);
//					.replace("{returl}", returl).replace("{sign}", sign);
			
			
			String responseTxt = GetData.getData(url,param);
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
				
				if(jo.has("code")){
					String code = jo.getString("code");
					
					logger.info("parse first result code : "+code);
					
					if(RESULTCODE_SUCC.equals(code)){
						String text = jo.getString("text");
						Sets sets = new Sets();
							sets.setKey("text");
							sets.setValue(text);
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
		
		map.put("theNo", "1");
		map.put("url", "http:// 123.56.91.64:8888/igameunpay/req.jsp");
		map.put("type",TYPE);
		map.put("merid", "lxtxia");
		map.put("goodsid","010");
		map.put("mobileid", "18801032292");
		map.put("clientip","1");
		map.put("orderid","lxtxia123456");
		map.put("platType", "9");
		map.put("key","lxtxia060419");
		
		logger.info(new IyxDdmDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http:// 123.56.91.64:8888/igameunpay/submitvercode.jsp");
		map.put("type",TYPE);
		map.put("merid","lxtxia");
		map.put("orderid","lxtxia1234556");
		map.put("verifycode", "32323");
		
		logger.info(new IyxDdmDynamicService().dynamic(map));
	}
}
