package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 掌智通动漫
 * 	http://182.92.243.34:8080/qqdm/zhongqing.jsp?imsi=46002001929445621&price=10
 * @author leoliu
 *
 */
public class ZztDmDynamicService implements IDynamicService{

	private static final String TYPE = "zztDm";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imsi={imsi}&price={price}";
	
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
			
			String price = map.get("price");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{price}",price).replace("{imsi}",imsi);
			
			String responseJson = GetData.getData(url, param);
			
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
		
		if(responseJson != null && responseJson.length() > 50){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String serviceno = jo.getString("serviceno");
				String smsContent = jo.getString("sms");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(smsContent.replaceAll("\\n", "\n"));
				sms.setSmsDest(serviceno);
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
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://182.92.243.34:8080/qqdm/zhongqing.jsp";//?cmd=6&price=8&g=10&f=18&imei=860174010602000&imsi=460030912121002&phone=13800138000";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("imsi","460025080049281");
		map.put("price","10");
		
		System.out.println(new ZztDmDynamicService().dynamic(map));
	}

}
