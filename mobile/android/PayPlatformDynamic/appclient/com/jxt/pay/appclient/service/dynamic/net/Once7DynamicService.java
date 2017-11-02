package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 
 * 	 http://tianchili.xn--fiqs8scxj63l.com/SMSInterface/root/getFeeCode.do?partnerid=80632791&appid=1&channelid=1&money=100&operator=1&area=07&imsi=460019232728217&imei=861554344325678&code=TCL29

{"returncode":"0","feecodelist":[{"orderid":"3416249","money":100,"mocode":"MM#WLAN#Fp0djv32seo5/RxJ/b1MwQ==#1945648161#399900003000","moLongnum":"10658424","isRegist":false,"type":1}]}
 * @author leoliu
 *
 */
public class Once7DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(Once7DynamicService.class);
	
	private static final String TYPE = "once7";
	
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
			
			logger.info("url : "+url);
			
			String responseJson = GetData.getData(url);
			
			xml = parse(responseJson,map);

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
	
	private String parse(String responseJson,Map<String,String> map){
		
		if(responseJson != null && responseJson.length() > 1){
			try{
				int pos = responseJson.indexOf("{");
				
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				String error = jo.getString("returncode");
				
				if(error.equals("0")){
					JSONArray feecodelist = jo.getJSONArray("feecodelist");
					
					JSONObject jo1 = feecodelist.getJSONObject(0);
					
					String num = jo1.getString("moLongnum");
					String content = jo1.getString("mocode");
					
					Sms sms = new Sms();
					
					sms.setSmsContent(content);
					sms.setSmsDest(num);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					if(error.length() == 0){
						error = "597";
					}
					return DynamicUtils.parseError(error);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://tianchili.xn--fiqs8scxj63l.com/SMSInterface/root/getFeeCode.do?partnerid=80632791&appid=1&channelid=1&money=100&operator=1&area=07&imsi=460019232728217&imei=861554344325678&code=TCL29";
		
		try {
			url = URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println(url);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type", "once7");
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		
		System.out.println(new Once7DynamicService().dynamic(map));
	}

}
