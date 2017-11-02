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
public class CdIdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(CdIdoDynamicService.class);
	
	private static final String TYPE = "CdIdo";//成都IDO
	
//	private static final String PARAM1 = "merid={merid}&goodsid={goodsid}&mobileid={mobileid}&clientip={clientip}&orderid={orderid}&orderdate={orderdate}&platType={platType}&returl={returl}&sign={sign}";
//	private static final String PARAM2 = "merid={merid}&orderid={orderid}&orderdate={orderdate}&verifycode={verifycode}&sign={sign}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "0000";
	private static String returl = "http://www.baidu.com";
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
			
			String merid = map.get("merid");
			String goodsid = map.get("goodsid");		
			String mobileid = map.get("mobileid");
			String clientip = map.get("clientip");
			String orderid = map.get("orderid");
			String orderdate = getDate(sdf);
			String platType = map.get("platType");
			String key = map.get("key");
			String sign =MD5Encrypt.MD5Encode(merid+goodsid+mobileid+clientip+orderid+orderdate+platType+returl+key);
			
//			String param = PARAM1.replace("{merid}", merid).replace("{goodsid}",goodsid).replace("{mobileid}",mobileid).replace("{clientip}",clientip).replace("{orderid}",orderid).replace("{orderdate}",orderdate).replace("{platType}",platType)
//					.replace("{returl}", returl).replace("{sign}", sign);
			
			NameValuePair[] params = new NameValuePair[9];
			
			params[0] = new NameValuePair("merid",merid);
			params[1] = new NameValuePair("goodsid",goodsid);
			params[2] = new NameValuePair("mobileid",mobileid);
			params[3] = new NameValuePair("clientip",clientip);
			params[4] = new NameValuePair("orderid",orderid);
			params[5] = new NameValuePair("orderdate",orderdate);
			params[6] = new NameValuePair("platType",platType);
			params[7] = new NameValuePair("key",key);
			params[8] = new NameValuePair("sign",sign);
			
			String responseHtml = new PostParamsData().postData(url, params);
//			String responseJson = new PostData().PostData(param.getBytes(), url);
			logger.info("-----"+responseHtml);
			xml = parseFirst(map,responseHtml);

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
				
				if(jo.has("retCode")){
					String retCode = jo.getString("retCode");
					
					logger.info("parse first result code : "+retCode);
					
					if(RESULTCODE_SUCC.equals(retCode)){
						String paytype = jo.getString("paytype");
						Sets sets = new Sets();
						if("YZM".equals(paytype)){
							sets.setKey("paytype");
							sets.setValue("1");
							return XstreamHelper.toXml(sets).toString();
						}else if("SMS".equals(paytype)){
							
							String mo = jo.getString("mo");
							String mo2 = jo.getString("mo2");
							String Called = jo.getString("Called");
							String Called2 = jo.getString("Called2");
							
							List<Sms> smsList = new ArrayList<Sms>();
							
							Sms sms = new Sms();
							
							sms.setSmsContent(mo);
							sms.setSmsDest(Called);
							sms.setSuccessTimeOut(2);
							smsList.add(sms);
							
							Sms sms1 = new Sms();
								
							sms1.setSmsContent(mo2);
							sms1.setSmsDest(Called2);
							sms1.setSuccessTimeOut(2);	
							smsList.add(sms1);
							return XstreamHelper.toXml(smsList);
						}else{
							return DynamicUtils.parseWait(10,map);
						}								
						
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
		map.put("url", "http://123.206.52.71/sync/wojiareq");
		map.put("type",TYPE);
		map.put("merid", "lxtxia");
		map.put("goodsid","010");
		map.put("mobileid", "18801032292");
		map.put("clientip","1");
		map.put("orderid","lxtxia123456");
		map.put("platType", "9");
		map.put("key","lxtxia060419");
		
		logger.info(new CdIdoDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http:// 123.56.91.64:8888/igameunpay/submitvercode.jsp");
		map.put("type",TYPE);
		map.put("merid","lxtxia");
		map.put("orderid","lxtxia1234556");
		map.put("verifycode", "32323");
		
		logger.info(new CdIdoDynamicService().dynamic(map));
	}
}
