package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.HttpClientUtils;

/**
 * 
 * 	 
num	String	否	发送短信的号码	   
content	String	否	发送短信内容	   
error	String	否	成功获取短信为0，失败请见错误代码	 

 * @author leoliu
 *
 */
public class Once10DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(Once10DynamicService.class);
	
	private static final String TYPE = "once10";
	
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
			
			String responseJson = new HttpClientUtils(url, "get", null, null).send();
			
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
				
				String num = jo.getString("receiver");
				String content = jo.getString("message");
				
				Sms sms = new Sms();
				
				sms.setSmsContent(content);
				sms.setSmsDest(num);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				return XstreamHelper.toXml(sms).toString();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "https://vm.jf55555.com:2443/client/api/v0/pay.api?imei=864205021458898&imsi=460025398426811&price=10&chapterId=00000002&channel=00000001";
		
		try {
			url = URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println(url);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type", "once10");
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		
		System.out.println(new Once10DynamicService().dynamic(map));
	}
	

}
