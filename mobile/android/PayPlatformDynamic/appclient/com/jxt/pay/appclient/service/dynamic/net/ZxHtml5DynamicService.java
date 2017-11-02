package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

public class ZxHtml5DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZxHtml5DynamicService.class);
	
	private static final String TYPE = "zxHtml5";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
//	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
//	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
//	private static final Guard guard3 = new Guard("10658800","",960,null,1);
//	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyyMMddHHmmss");//Wed May 11 2016 21:44:57 GMT+0800
	}//week month dd yyyy HH:mm:ss GMT+0800
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private int timeOut = 60;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
//		String url = map.get("url");
//		
//		if(url != null && url.length() > 0){
//			String channel = map.get("channel");
//			
//			Calendar cal = Calendar.getInstance();
//			Long startTime = tryMap.get(channel);
//			
//			if(startTime == null){
//				startTime = cal.getTimeInMillis();
//				tryMap.put(channel,startTime);
//			}else{
//				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
//					tryMap.remove(channel);
//					return DynamicUtils.parseError("599");
//				}
//			}
			
//			String orderId = channel+"00";
//			String orderId = map.get("orderId");
//			String cId = map.get("cId");
//			String nid = map.get("nid");
//			String mobile = map.get("mobile");
//			String cm = map.get("cm");
			
//			String key = map.get("key");
//			String authvalue = MD5Encrypt.MD5Encode(orderId+key);
			
//			NameValuePair[] params = new NameValuePair[8];
//			params[0] = new NameValuePair("orderid", orderId);
//			params[1] = new NameValuePair("cId", cId);
//			params[2] = new NameValuePair("nid", nid);
//			params[3] = new NameValuePair("mobile", mobile);
//			params[4] = new NameValuePair("cm",cm);
		    String cn = new Date()+" ";
			Sets sets = new Sets();
			sets.setKey("cn");
			sets.setValue(cn);
			
			return XstreamHelper.toXml(sets).toString();
//			String responseXml = PostParamsData.postData(url, params);
//			
//			xml = parse(responseXml);
//
//			if(xml == null || xml.length() == 0){
//				xml = DynamicUtils.parseWait(10,map);
//			}else{
//				tryMap.remove(channel);
//			}
//		}
//		
//		return xml;
	}
	
	private String parse(String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			logger.info(responseXml);
			
			String msgcode = SingleXmlUtil.getNodeValue(responseXml, "msgcode");
				
			if(msgcode != null && msgcode.equals("1000")){
				return "<wait>1</wait>";
			}
			
			return DynamicUtils.parseError("597");
		}
		
		return null;
	}
	
	public static void main(String[] args){
					  
		String url = "http://http://123.56.252.148:9080/zsH5/wap/checkImagYzm.action";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("cId", "Z014C030Q018");
		map.put("nid", "357966485");
		map.put("orderId", "13A_301");
		map.put("mobile", "18801032292");
		map.put("type", "zxHtml5");
		map.put("cm", "M31F0003");
		
		
		System.out.println(new ZxHtml5DynamicService().dynamic(map));
	}

}
