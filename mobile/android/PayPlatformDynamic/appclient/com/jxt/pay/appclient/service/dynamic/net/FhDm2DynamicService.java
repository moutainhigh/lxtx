package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 凤凰短信
 * 	http://114.215.174.42:8080/online/getall?imsi=***&imei=***&linkId=****&cpparam=***&chid=***
 * @author leoliu
 *
 */
public class FhDm2DynamicService implements IDynamicService{
	
	private static Logger logger = Logger.getLogger(FhDm2DynamicService.class);

	private static final String TYPE = "fhDm2";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imsi={imsi}&imei={imei}&linkId={linkId}&cpparam={cpparam}&chid={chid}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private int timeOut = 2;
	
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
			String imei = map.get("imei");
			String chid = map.get("chid");
			String linkId = map.get("linkId");
			
			String param = REQUESTMODEL.replace("{imsi}",imsi).replace("{imei}",imei).replace("{chid}",chid).replace("{linkId}",linkId).replace("{cpparam}",channel);
			
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
				
		if(responseTxt != null && responseTxt.length() > 10){
		
			logger.info("responseTxt : __"+responseTxt+"__");
			
			if(responseTxt.contains("error")){
				return DynamicUtils.parseError("597");
			}
			
			String[] arr = responseTxt.split("####");
			
			List<Sms> smsList = new ArrayList<Sms>();
			
			Sms sms = new Sms();
			
			sms.setSmsContent(arr[1]);
			sms.setSmsDest(arr[0]);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			smsList.add(sms);
					
			return XstreamHelper.toXml(smsList);
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://121.40.32.247:8090/online/getall";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("imsi", "460001123143655");
		map.put("imei", "867376023133651");
		map.put("chid","wangzh");
		map.put("linkId","hxtang0800");
		
		System.out.println(new FhDm2DynamicService().dynamic(map));
	}

}
