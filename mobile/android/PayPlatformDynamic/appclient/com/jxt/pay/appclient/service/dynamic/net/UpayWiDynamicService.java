package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class UpayWiDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(UpayWiDynamicService.class);
	
	private static final String TYPE = "upayWi";
	
	private static final String PARAM = "sig={sig}&price={price}&imsi={imsi}&imei={imei}&iccid={iccid}&videoUa={videoUa}&ip={ip}&cpparam={cpparam}";
	
	private int timeOut = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			String sig = map.get("sig");
			String price = map.get("price");
			
			String videoUa = "Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; Lenovo A708t Build/JDQ39) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
			try {
				videoUa = URLEncoder.encode(videoUa, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			String ip = map.get(Constants.IPPARAM);
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String iccid = StringUtils.defaultString(map.get("iccid"));
			String cpparam = StringUtils.defaultString(map.get("cpparam"));
			
			String param = PARAM.replace("{sig}", sig).replace("{price}",price).replace("{videoUa}",videoUa).replace("{ip}",ip).replace("{imsi}",imsi).replace("{imei}",imei).replace("{iccid}",iccid).replace("{cpparam}",cpparam);
			
			String responseJson = GetData.getData(url, param);
			logger.info("-----"+responseJson);
			
			xml = parse(map,responseJson);
			
		}
		
		return xml;
	}
	
	private String parse(Map<String,String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("result")){
					String resultCode = jo.getString("result");
					
					logger.info("parse first result code : "+resultCode);
					
					if("success".equals(resultCode)){
						
						JSONArray ja = jo.getJSONArray("smsList");
						
						List<Sms> smsList = new ArrayList<Sms>();
						
						for(int i = 0 ; i < ja.length() ; i ++){
							JSONObject subJo = ja.getJSONObject(i);
							
							String encode = subJo.getString("encode");
							String smsContent = subJo.getString("smsContent");
							String smsPort = subJo.getString("smsPort");
							
							Sms sms = new Sms();
							
							sms.setSmsDest(smsPort);
							
							if("plain".equals(encode)){
								
							}else if("base64".equals(encode)){
								smsContent = CommonUtil.base64Decode(smsContent);
							}else{//base64byte
								sms.setSendType("3");
							}
							
							sms.setSmsContent(smsContent);
							sms.setSuccessTimeOut(2);
							
							smsList.add(sms);
						}
						
						return XstreamHelper.toXml(smsList).toString();
					}else{
						return DynamicUtils.parseError(resultCode);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	

	public static void main(String[] args){
		test();
	}

	private static void test(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("url", "http://api.wiipay.cn/api.do");
		map.put("type","upayWi");
		map.put("sig", "05b02276787c50f17db7db71f179ac60");
		map.put("price","10");
		map.put("iccid","898600f2261578514218");
		map.put("imsi","460026201181073");
		map.put("imei", "860572034718186");
		map.put(Constants.IPPARAM,"113.133.155.70");
		map.put("cpparam","LX10B101a123456784");
		
		logger.info(new UpayWiDynamicService().dynamic(map));
	}
}
