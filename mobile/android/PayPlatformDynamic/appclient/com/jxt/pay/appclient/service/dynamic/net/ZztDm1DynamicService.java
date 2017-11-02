package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * @author leoliu
 *
 */
public class ZztDm1DynamicService implements IDynamicService{

	private static final String TYPE = "zztDm1";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imsi={imsi}&imei={imei}&sid={sid}&cpparam={cpparam}&chid={chid}&os=19";
	
	@Override
	public String getType() {
		return TYPE;
	}

	private int timeOut = 2;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String cpparam = map.get("cpparam");
			String sid = map.get("sid");
			String chid = map.get("chid");
			
			String param = REQUESTMODEL.replace("{imei}",imei).replace("{imsi}",imsi).replace("{cpparam}",cpparam).replace("{sid}",sid).replace("{chid}", chid);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parse(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 10){
			
			if(responseTxt.contains("####")){
				String[] arr = responseTxt.split("####");
				
				Sms sms = new Sms();
				sms.setSmsDest(arr[0]);
				sms.setSmsContent(arr[1]);
				sms.setSuccessTimeOut(2);
				
				return XstreamHelper.toXml(sms).toString();
				
			}else{
				return DynamicUtils.parseError("597");
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://120.26.214.98:8080/dm/sms";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		map.put("sid","foot2000");
		map.put("chid","foot");
		map.put("cpparam","388000000");
		map.put("type","zztDm1");
		
		System.out.println(new ZztDm1DynamicService().dynamic(map));
	}

}
