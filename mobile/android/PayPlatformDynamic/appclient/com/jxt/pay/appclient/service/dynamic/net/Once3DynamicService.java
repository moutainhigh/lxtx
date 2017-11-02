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
 * http://yys.astep.cn:8070/sms/request/content/game.htm?base64=0&cpid=0074&imei=xxxxxxxxxxxxxxx&imsi=xxxxxxxxxxxxxxx&price=xxx&cpparam=xxxxxxxxxxxx
 * msg1####msg2
 * @author leoliu
 *
 */
public class Once3DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(Once3DynamicService.class);
	
	private static final String TYPE = "once3";
	
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
				
			String responseTxt = GetData.getData(url);
			
			xml = parse(responseTxt,map);

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
	
	private String parse(String responseTxt,Map<String,String> map){
		
		if(responseTxt != null && responseTxt.length() > 0 && responseTxt.contains("####")){
			try{
				String[] arr = responseTxt.split("####");
				
				if(arr.length == 2){
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(CommonUtil.base64Decode(arr[0]));
					sms.setSmsDest(map.get("dest1"));
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
					
					Sms sms1 = new Sms();
					
					sms1.setSmsContent(CommonUtil.base64Decode(arr[1]));
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
		String url = "http://yys.astep.cn:8070/sms/request/content/game.htm?base64=1&cpid=0074&imei=862949029214504&imsi=460022101441340&price=1000";
		
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
		map.put("dest1","10658422");
		map.put("dest2","1065889923");
		
		System.out.println(new Once3DynamicService().dynamic(map));
	}

}
