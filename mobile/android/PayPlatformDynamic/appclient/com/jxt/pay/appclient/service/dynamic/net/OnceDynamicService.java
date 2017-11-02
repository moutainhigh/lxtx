package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class OnceDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(OnceDynamicService.class);
	
	private static final String TYPE = "once";
	
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
			try {
				url = URLDecoder.decode(url,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			logger.info("url : "+url);
			
			String channel = map.get("channel");
			
			String responseTxt = GetData.getData(url);
			
			if(responseTxt != null && responseTxt.length() > 5){
				String prefix = map.get("prefix");
				
				if(prefix != null && prefix.length() > 0 && !responseTxt.contains(prefix)){
					xml = DynamicUtils.parseError("597");
				}else{
					while(responseTxt.endsWith("\r") || responseTxt.endsWith("\n")){
						responseTxt = responseTxt.substring(0,responseTxt.length() - 1);
					}
					
					Sms sms = new Sms();
					
					sms.setSmsContent(responseTxt);
					sms.setSmsDest(map.get("dest"));
					sms.setSuccessTimeOut(2);
					
					xml = XstreamHelper.toXml(sms).toString();
				}
			}
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				Integer tryCnt = tryMap.get(channel);
				
				if(tryCnt == null){
					tryCnt = 1;
					tryMap.put(channel,tryCnt);
				}else{
					tryCnt ++;
					
					if(tryCnt >= 3){
						tryMap.remove(channel);
						return DynamicUtils.parseError("599");
					}else{
						tryMap.put(channel,tryCnt);
					}
				}
				
			}
		}
		
		return xml;
	}

	public static void main(String[] args){
		
		String url = "";
		
//		url = "http://211.166.9.29:8888/cmcc/dynamicCmd.do?staticCmd=SzZMVUejOUhVUeYjMzfNRiMxMKI3&key=ABCJHZQ1Y3K4P5627NKFZ9TCACSFERH95M419831001DGH7R#";
//		url = "http://211.166.9.29:8888/cmcc/dynamicCmd.do%3Fcontentcodes%3D10029122";
		url = "http://118.144.76.39/mm/getdgjymsg6.php?imei=862896023277212&imsi=460026985182743";
		
		url = "http://182.92.243.34:8080/qqdm/wulong.jsp%3Fcontenttype%text%26imei%3D865728023761430%26imsi%3D460009205433023%26price%3D3%26userdata%3D100110D17Wa087618599";
			
		url = "http://122.114.52.108:8001/yc8.asp";
		
		try {
			System.out.println(URLEncoder.encode(url,"utf-8"));
//			System.out.println(URLDecoder.decode(url,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url",url);
		map.put("type","once");
		map.put("channel","10B101a1234567890");
		map.put("dest","1065843110");
		
		System.out.println(new OnceDynamicService().dynamic(map));
	}
}
