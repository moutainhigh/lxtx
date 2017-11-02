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
num	String	否	发送短信的号码	   
content	String	否	发送短信内容	   
error	String	否	成功获取短信为0，失败请见错误代码	 

 * @author leoliu
 *
 */
public class HhkjSpjdDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjSpjdDynamicService.class);
	
	private static final String TYPE = "hhkjSpjd";
	
	private static final String PARAMS = "channelNo={channelNo}&imsi={imsi}&ext={ext}";
	
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
			
			String channelNo = map.get("channelNo");
			String imsi = map.get("imsi");
			String ext = map.get("ext");
			
			String params = PARAMS.replace("{channelNo}", channelNo).replace("{imsi}", imsi).replace("{ext}",ext);
			
			String responseJson = GetData.getData(url,params);
			
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
			logger.info("responseJson:"+responseJson);
			
			try{
				int pos = responseJson.indexOf("{");
				
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				String error = jo.getString("state");
				
				if(error.equals("0")){
					JSONObject data = jo.getJSONObject("data");
					
					String num = data.getString("port");
					String content = data.getString("sms");
					
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
		String url = "http://112.74.13.159:8088/sync/0BKW2146/requestData";
		
		System.out.println(url);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type", "hhkjSpjd");
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("channelNo","K3HSN6Q6");
		map.put("imsi","460001123143655");
		map.put("ext","123");
		
		System.out.println(new HhkjSpjdDynamicService().dynamic(map));
	}

}
