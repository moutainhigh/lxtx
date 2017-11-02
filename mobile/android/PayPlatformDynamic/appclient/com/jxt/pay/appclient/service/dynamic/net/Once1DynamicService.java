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
public class Once1DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(Once1DynamicService.class);
	
	private static final String TYPE = "once1";
	
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
				
				String error = jo.getString("error");
				
				if(error.equals("0")){
					String num = jo.getString("num");
					String content = jo.getString("content");
					
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
		String url = "http://182.92.3.11:8057/dmlm/strongLinePayment?channelId=477bc87008e34fc0934403c8fef4d19d&imei=862949029214504&imsi=460022101441340&fee=1000&ext=10B101a87654322";
		
		url = "http://14.17.74.121:9900/recharges.do?cpid=54097103&imei=862949029214504&imsi=460022101441340&fee=600&ext=12345";
		
		url = "http://58.64.143.245:8888/other/zwvideo.do?cpid=d9d27764&imsi=460001123143655&imei=867376023133651&fee=1000&ext=XT020";
		
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
		
		System.out.println(new Once1DynamicService().dynamic(map));
	}

}
