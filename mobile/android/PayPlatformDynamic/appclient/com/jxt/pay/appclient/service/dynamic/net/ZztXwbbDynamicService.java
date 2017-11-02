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
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class ZztXwbbDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztXwbbDynamicService.class);
	
	private static final String TYPE = "zztXwbb";//掌智通炫舞宝贝
	
	private static final String PARAM1 = "orderid={orderid}&mobileid={mobile}&amount={amount}&goodsid={goodsid}";
	private static final String PARAM2 = "orderid={orderid}&smscode={smscode}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "200";
	
	
	
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
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String orderid = map.get("orderid");
			String mobile = StringUtils.defaultString(map.get("dmobile"));
			String amount = map.get("amount");
			String goodsid = map.get("goodsid");
			
			
			String param = PARAM1.replace("{amount}", amount).replace("{orderid}",orderid)
					.replace("{mobileid}",mobile).replace("{goodsid}",goodsid);
			
			String responseJson = GetData.getData(url, param);
			logger.info("-----"+responseJson);
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
				
				if(jo.has("msg")){
					
						String msg = jo.getString("msg");
						
						Sets sets = new Sets();
						sets.setKey("msg");
						sets.setValue(msg);
				
						
						return XstreamHelper.toXml(sets).toString();
					}
						
						
						
					//}
				
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
			String nid = map.get("nid");
			String orderId = map.get("orderId");
			String mobile = StringUtils.defaultString(map.get("dmobile"));
			String cId = map.get("cId");
			String verifyCode = map.get("verifyCode");
			
			String msg = map.get("msg");
			
			String param = PARAM2.replace("{nid}", nid).replace("{orderId}",orderId)
					.replace("{mobile}",mobile).replace("{msg}",msg).replace("{cId}",cId).replace("{verifyCode}",verifyCode);
			
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
				
				if(jo.has("code")){
					String code = jo.getString("code");
					
					logger.info("parse second result code : "+code);
					
					if(RESULTCODE_SUCC.equals(code)){
						Sets sets = new Sets();
						sets.setKey("code");
						sets.setValue(code);
						
						return XstreamHelper.toXml(sets).toString();
					}
							return DynamicUtils.parseError(code);
						
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
		map.put("url", "http://cc.spinterface.3gshow.cn/qingchundongli/getcode.ashx");
		map.put("type",TYPE);
		map.put("dmobile", "18801032292");
		map.put("orderid","151_13X265");
		map.put("amount","500");
		map.put("goodsid","050");
		
		
		logger.info(new ZztXwbbDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://123.56.252.148:9080/zsH5/m/orderSubmit.action");
		map.put("type",TYPE);
		map.put("dmobile", "18801032292");
		map.put("orderId","13X265");
		map.put("cId","Z014C030Q018");
		map.put("nid","357966485");
		map.put("verifyCode", "272998");
		map.put("msg", "2002");
		
		logger.info(new ZztXwbbDynamicService().dynamic(map));
	}
}
