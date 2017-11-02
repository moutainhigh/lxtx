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
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 
 * 	 
{"smsport":"10658424","smsmsg":"TU0jV0xBTiNuR2craUFpOU5XdU9zZGwzVE10VFhRPT0jMzY2NzYyODU5IzM5OTkwMDAwMzAwMA==","exdata":"MTIzNDU2Nzg5","result":"0","sid":"123456789","tradeid":"MjdBQ0NCNjcxMTVBRDMwQUQzRDg3RkQzQzA2NDEwOTc="}

 * @author leoliu
 *
 */
public class Once4DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(Once4DynamicService.class);
	
	private static final String TYPE = "once4";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Integer> tryMap = new HashMap<String, Integer>();

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
			
			xml = parse(responseJson);

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
	
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 1){
			try{
				int pos = responseJson.indexOf("{");
				
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				String code = jo.getString("result");
				
				if(code.equals("0")){
					String moport = jo.getString("smsport");
					String cmd = jo.getString("smsmsg");
					
					Sms sms = new Sms();
					
					sms.setSmsContent(CommonUtil.base64Decode(cmd));
					sms.setSmsDest(moport);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError(code);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://112.74.111.56:9039/gamesit/zy/common/fee?imsi=460022101441340&cpparam=UWRSBC&paycode=30000891783504&imei=862949029214504";
		
		try {
			url = URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println(url);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type", "once3");
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		
		System.out.println(new Once4DynamicService().dynamic(map));
	}

}
