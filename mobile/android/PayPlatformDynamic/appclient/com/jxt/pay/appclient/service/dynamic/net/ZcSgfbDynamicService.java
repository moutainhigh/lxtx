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
public class ZcSgfbDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZcSgfbDynamicService.class);
	
	private static final String TYPE = "zcSgfb";//赞成水果风暴
	
	private static final String PARAM1 = "cpid={cpid}&amount={amount}&mobile={mobile}&iccid={iccid}&appid={appid}&imsi={imsi}&imei={imei}&cpparam={cpparam}";
	
	private static final String RESULTCODE_SUCC = "0";
	
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
		
		if(url != null && url.length() > 0){
			String cpid = map.get("cpid");
			String amount = map.get("amount");
			String iccid = map.get("iccid");
			String mobile = StringUtils.defaultString(map.get("dmobile"));
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String appid = map.get("appid");
			String cpparam = map.get("cpparam");
			
			String param = PARAM1.replace("{cpid}", cpid).replace("{amount}",amount).replace("{iccid}",iccid).replace("{mobile}",mobile)
					.replace("{appid}",appid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{cpparam}", cpparam);
			
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
				
				if(jo.has("ResultCode")){
					String ResultCode = jo.getString("ResultCode");
					
					logger.info("parse first result code : "+ResultCode);
					
					if(RESULTCODE_SUCC.equals(ResultCode)){
						
						String Type = jo.getString("Type");
						if(Type.equals("2")){
							
							String SpCmd = jo.getString("SpCmd");
//							SpCmd = CommonUtil.base64Decode(SpCmd);
							
							Sms sms = new Sms();
							
							sms.setSmsDest(jo.getString("SpNum"));
							sms.setSmsContent(SpCmd);
							sms.setSuccessTimeOut(2);
							sms.setSendType("3");
							
							return XstreamHelper.toXml(sms).toString();
							
						}else{
							Sms sms = new Sms();
							
							sms.setSmsDest(jo.getString("SpNum"));
							sms.setSmsContent(jo.getString("SpCmd"));
							sms.setSuccessTimeOut(2);
							
							return XstreamHelper.toXml(sms).toString();
						}
						
						
						
					}else{
						return DynamicUtils.parseError(ResultCode);
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
		map.put("url", "http://123.57.31.113:700/GetSpContent.aspx");
		map.put("type",TYPE);
		map.put("cpid", "888");
		map.put("amount","600");
		map.put("mobile","18693052709");
		map.put("imsi","460013059617392");
		map.put("imei", "867392050632195");
		map.put("iccid","bjj");
		map.put("appid", "000717");
		map.put("cpparam", "dbjzzt");
		
		logger.info(new ZcSgfbDynamicService().dynamic(map));
	}

}
