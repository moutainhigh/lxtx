package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

public class BjjsDdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(BjjsDdoDynamicService.class);
	
	private static final String TYPE = "bjjsDdo";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
//	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
//	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
//	private static final Guard guard3 = new Guard("10658800","",960,null,1);
//	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyyMMddHHmmss");
	}
	
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
			
			String orderid = channel+"00";
			String msisdn = map.get("msisdn");
			String serviceId = map.get("serviceId");
			String cpid = map.get("cpid");
			String spid = map.get("spid");
			String cpCode = map.get("cpCode");
			String timestamp = sdf.format(new Date());
			String key = map.get("key");
			String authvalue = MD5Encrypt.MD5Encode(orderid+key);
			
			NameValuePair[] params = new NameValuePair[8];
			params[0] = new NameValuePair("orderid", orderid);
			params[1] = new NameValuePair("msisdn", msisdn);
			params[2] = new NameValuePair("serviceId", serviceId);
			params[3] = new NameValuePair("cpid", cpid);
			params[4] = new NameValuePair("spid",spid);
			params[5] = new NameValuePair("cpCode",cpCode);
			params[6] = new NameValuePair("timestamp",timestamp);
			params[7] = new NameValuePair("authvalue",authvalue);
			
			String responseXml = PostParamsData.postData(url, params);
			
			xml = parse(responseXml);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			logger.info(responseXml);
			
			String msgcode = SingleXmlUtil.getNodeValue(responseXml, "msgcode");
				
			if(msgcode != null && msgcode.equals("1000")){
				return "<wait>1</wait>";
			}
			
			return DynamicUtils.parseError("597");
		}
		
		return null;
	}
	
	public static void main(String[] args){
					  
		String url = "http://117.25.133.11:8888/LoveCartoonCMCC/smsGetMessageServlet";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654322");
		map.put("msisdn", "13811155779");
		map.put("serviceId", "200000003650");
		map.put("cpid", "2000");
		map.put("spid", "1001");
		map.put("cpCode","2030");
		map.put("key","3)*,cp8j");
		map.put("type", "bjjsDdo");
		
		
		System.out.println(new BjjsDdoDynamicService().dynamic(map));
	}

}
