package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * http://182.92.21.219:10789/cmcc/g/single/s2sChargeSMS?pid=6821848435-2353122602&imsi=460023800359009&imei=865372027050530&regist=0&version=1.0.0.7&payId=0000000000000000&chargeId=006053633004&contentId=638416022497&channelId=10558000
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <p><status>0</status><error></error><errorInfo></errorInfo><registSMS><SMS>QlVCQFR8R0AgICAgIGhMS05mTFBHZDg5Smo5MkAyT0BATUdGRjNFVEs3MjcwWHN0QGdvYWFtMjR6WDcxQDI2Mzg2NDA5QDg1ODgwNTg5NTNAMEA4OTg3MjU4Mjg3MzYyNTZAMzAxMzVAMTgxODg2ODgwMjgzNA==</SMS><sessionId>bBlDCXDOnFhO</sessionId></registSMS><chargeSMS><SMS>OnA6b0gqanJHL3x6XFtYZWVHLlxYVDNuOitSOm9mNTBnfnBiWFUqRCs6ZVA6OjpmOjpBOjpPOm86ViB0Qy5tMys8QF46ei06OjE6Onc+bExoWXNoNDltO1w6PTpHUStwPFdwRVd+OCBVPH46OWMnXmxMY3R+QGVdMG1PdG1AV3UzbWRkSA==</SMS></chargeSMS></p>
 * 
 * @author leoliu
 *
 */
public class Once5DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(Once5DynamicService.class);
	
	private static final String TYPE = "once5";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Integer> tryMap = new HashMap<String, Integer>();

	private int timeOut = 60;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			try {
				url = URLDecoder.decode(url,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
				
			String responseXml = GetData.getData(url);
			
			xml = parse(responseXml,map);

			if(xml == null || xml.length() == 0){
				
				Integer tryCnt = tryMap.get(channel);
				
				if(tryCnt == null){
					tryCnt = 1;
					tryMap.put(channel,tryCnt);
				}else{
					tryCnt = tryCnt + 1;
					
					if(tryCnt >= 3){
						tryMap.remove(channel);
						return DynamicUtils.parseError("599");
					}else{
						tryMap.put(channel,tryCnt);
					}
				}
				
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseXml,Map<String,String> map){
		
		if(responseXml != null && responseXml.length() > 0 ){
			try{
				String status = SingleXmlUtil.getNodeValue(responseXml, "status");
				
				if(status != null && status.equals("0")){
					
					String registSMSXml = SingleXmlUtil.getNodeValue(responseXml, "registSMS");
					String chargeSMSXml = SingleXmlUtil.getNodeValue(responseXml, "chargeSMS");
					
					String registSms = SingleXmlUtil.getNodeValue(registSMSXml, "SMS");
					String chargeSms = SingleXmlUtil.getNodeValue(chargeSMSXml, "SMS");
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(CommonUtil.base64Decode(registSms));
					sms.setSmsDest(map.get("dest1"));
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
					
					Sms sms1 = new Sms();
					
					sms1.setSmsContent(CommonUtil.base64Decode(chargeSms));
					sms1.setSmsDest(map.get("dest2"));
					sms1.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms1);
					
					StringBuffer sb = new StringBuffer();
					
					sb.append(XstreamHelper.toXml(sms));
					
					String wait = map.get("wait");
					if(wait == null || wait.length() == 0){
						wait = "5";
					}
					
					sb.append("<wait>").append(wait).append("</wait>");
					sb.append(XstreamHelper.toXml(sms1));
					
					return sb.toString();					
				}				
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://182.92.21.219:10789/cmcc/g/single/s2sChargeSMS?pid=6821848435-2353122602&imsi=460023800359009&imei=865372027050530&regist=0&version=1.0.0.7&payId=0000000000000000&chargeId=006053633004&contentId=638416022497&channelId=10558000";
		
		try {
			url = URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println(url);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type", "once5");
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("dest1","10658422");
		map.put("dest2","1065889923");
		
		System.out.println(new Once5DynamicService().dynamic(map));
	}

}
