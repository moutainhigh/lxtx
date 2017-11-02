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
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

public class BjzdDdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(BjzdDdoDynamicService.class);
	
	private static final String TYPE = "bjzdDdo";
	private static String PARAMS = "cpparam={cpparam}&paycode={paycode}&cpid={cpid}&msisdn={msisdn}";
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
						
			String msisdn = map.get("msisdn");
			String paycode = map.get("paycode");
			String cpid = map.get("cpid");
			
			String params = PARAMS.replace("{cpparam}", channel).replace("{paycode}", paycode).replace("{cpid}", cpid).replace("{msisdn}",msisdn);
			
			String responseJson = GetData.getData(url, params);
			
			xml = parse(responseJson);
			
			logger.info("xml:"+xml);
		}
		
		return xml;
	}
	
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info(responseJson);
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String code = jo.getString("code");
				
				if("0".equals(code)){
					String url = jo.getString("url");
					
					url = CommonUtil.base64Decode(url);
					
					logger.info("url:"+url);
					
					Sets sets = new Sets();
					sets.setKey("url");
					sets.setValue(url);
					
					return XstreamHelper.toXml(sets).toString();
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
					  
		String url = "http://113.11.195.115:8012/ddo/getinfo.aspx";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654322");
		map.put("msisdn", "13811155779");
		map.put("cpid", "2059");
		map.put("paycode","8000000018002077");
		map.put("type", "bjzdDdo");
		
		
		System.out.println(new BjzdDdoDynamicService().dynamic(map));
	}

}
