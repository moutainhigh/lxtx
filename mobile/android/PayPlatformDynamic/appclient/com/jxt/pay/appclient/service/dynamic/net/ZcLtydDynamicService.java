package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class ZcLtydDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZcLtydDynamicService.class);
	
	private static final String TYPE = "zcLtyd";
	
	private static final String PARAM1 = "pid={pid}&app_special={app_special}&money={money}&time={time}&tel={tel}&imsi={imsi}&imei={imei}";
	
	private static final String RESULTCODE_SUCC = "200000";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	private int timeOut = 60;
	
	@Override
	public String getType() {
		return TYPE;
	}

	public static String gettime(){
		String time = ""+System.currentTimeMillis()/1000;
		return time;
	}

	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String pid = map.get("pid");
			String money = map.get("money");
			String time = gettime();
			String tel = StringUtils.defaultString(map.get("dmobile"));
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String app_special = map.get("app_special");
			if(app_special == null){
				app_special = "";
			}
			
			String param = PARAM1.replace("{pid}", pid).replace("{app_special}",app_special).replace("{money}",money).replace("{time}",time).replace("{tel}",tel).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseJson = GetData.getData(url, param);
			logger.info("-----"+responseJson);
			xml = parseFirst(map,responseJson);			
		}
		
		return xml;
	}
	
	private String parseFirst(Map<String,String> map,String responseJson){

		logger.info("responseJson:"+responseJson);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("resultCode")){
					String resultCode = jo.getString("resultCode");
					
					logger.info("parse first result code : "+resultCode);
					
					if(RESULTCODE_SUCC.equals(resultCode)){
						Sets sets = new Sets();
						sets.setKey("verifyDest");
						sets.setValue(jo.getString("accessNo"));
						
						Sms sms = new Sms();
						
						sms.setSmsDest(jo.getString("accessNo"));
						sms.setSmsContent(jo.getString("sms"));
						sms.setSuccessTimeOut(2);
						
						return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).toString();
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
		test1();
//		test2();
	}
	
	private static void test2(){
		String s = "2115502000000184856hz201604281848564422430000000000Wf86cbe89a455c549e890";
		
		try {
			System.out.println(CommonUtil.base64Decode(s));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("theNo", "1");
		map.put("url", "http://182.92.149.179/open_gate/web_game_fee.php");
		map.put("type",TYPE);
		map.put("pid", "BJLX");
		map.put("app_special","unireader");
		map.put("money","10");
		map.put("imsi","460014867444190");
		map.put("imei", "358574041772411");
		map.put("channel","13X265a123844862");
		map.put("dmobile", "13111933910");
		
		logger.info(new ZcLtydDynamicService().dynamic(map));
	}

}
