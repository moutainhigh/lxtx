package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 
 * 	
 * @author leoliu
 *
 */
public class JkhlDdoDynamicService implements IDynamicService{

	private static final String TYPE = "jkhlDdo";
	
	private static final String PARAM = "Mobile={Mobile}&TheFee={TheFee}&flag={flag}";

	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private int timeOut = 60;
	
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
			
			String mobile = map.get("dmobile");
			String theFee = map.get("theFee");
			String flag = map.get("flag");
			
			String param = PARAM.replace("{Mobile}", mobile).replace("{TheFee}", theFee)
					.replace("{flag}", flag);
			
			String responseXml = GetData.getData(url,param);
			
			xml = parseXml(responseXml);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseXml(String responseXml){
		
		if(responseXml != null && responseXml.length() > 1){
			try{
				
				String status = SingleXmlUtil.getNodeValue(responseXml, "StatusCode");
				
				if(status.equals("1000")){
					return "<wait>0</wait>";
				}else{
					return DynamicUtils.parseError(status);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://115.29.196.167/interface/ddo.php";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type", "jkhlDdo");
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("dmobile","13811155779");
		map.put("theFee","6");
		map.put("flag","qxhd");

		System.out.println(new JkhlDdoDynamicService().dynamic(map));
	}

}
