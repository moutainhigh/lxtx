package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.handler.BlackMobileHandler;
import com.jxt.pay.handler.MobileInfoHandler;
import com.jxt.pay.handler.PhoneNoRegionHandler;
import com.jxt.pay.helper.BlackMobileHelper;
import com.jxt.pay.pojo.MobileInfo;
import com.jxt.pay.pojo.PhoneNoRegion;

/**
 * 对接南京网游
 * @author leoliu
 * 
 * 
 * http://182.92.21.219:10789/cmcc/g/online/s2sAutoRegist?imei=xxx&imsi=xxxx&chargeId=xxxx&pid=xxxx&channelId=xxxx&userToken=xxxx&payId=xxxx&version=1.0.0.8
 *
 *
 * http://xxxxxxxx:10789/cmcc/g/online/s2sAutoChargeSMS?taskId=xxxxxx&pid=xxxxxx&version=1.0.0.8
 */
public class ShylWyDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ShylWyDynamicService.class);
	
	private static final String FIRSTREQUESTMODEL = "imei={imei}&imsi={imsi}&chargeId={chargeId}&pid={pid}&channelId={channelId}&payId={payId}&version=1.0.0.8";
	private static final String SECONDREQUESTMODEL = "taskId={taskId}&pid={pid}&version=1.0.0.8";

	private static final Guard guard1 = new Guard("1065889955","成功|购买",1440,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static final String STATUS_WAIT = "0";
	private static final String STATUS_SUCC = "1";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String TYPE = "shyl1";
	
	private int timeOut = 120;
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
		}else if(THENO_2.equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}
	
	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String xml = null;
		
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
			
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String pid = map.get("pid");
			String channelId = map.get("channelId");
			String payId = channel;
			String chargeId = map.get("chargeId");
			
			String script = FIRSTREQUESTMODEL.replace("{imsi}", imsi).replace("{imei}", imei).replace("{payId}", payId).replace("{chargeId}", chargeId).replace("{pid}", pid).replace("{channelId}",channelId);
		
			logger.info("firstDynamic : "+script);
			
			String responseXml = GetData.getData(url, script);
			
			xml = parseFirst(responseXml);
			
			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(7,map);
			}else{
				tryMap.remove(channel);
			}
		
		}
		
		return xml;
	}

	/**
	 * <p><status>0</status><error></error><taskId>1419861363214</taskId><ip>112.126.67.41</ip><regist><SMS>QlVCQFR8YSAgICAgIDlxZjlLUWFsajg4cmYwOUAyQ0BATUdGRjhhZzlQQGZXdEtnaXp4emcgQ3FpemtnbUA2ODg0NTkyOTg4NTg2QDQ4Q0A0MDk0ODgzNjlQQDdXOTE0MzkyODU1NzMyNkA1MDUwMUAzODk3NDY4MDIyODU0</SMS></regist></p>
	 * @param responseXml
	 * @return
	 */
	private String parseFirst(String responseXml){
		logger.info("parseFirst : "+responseXml);
		if(responseXml != null && responseXml.length() > 0){
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if("0".equals(status)){
				
				
				
			}
		}
		
		return null;
	}

	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String,String> map){
		String xml = null;
		String url = map.get("url");
		
		String content_sid = map.get("content_sid");
		
		if(url != null && url.length() > 0){
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(content_sid);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(content_sid,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(content_sid);
					return DynamicUtils.parseError("599");
				}
			}
			
			String script = SECONDREQUESTMODEL.replace("{content_sid}", content_sid);
			
			logger.info("secondDynamic : "+script);
			
			String responseXml =  new PostData().PostData(script.getBytes(), url);
		
			xml = parseSecond(map,responseXml);
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(7,map);//获取失败
//			tryMap.remove(content_sid);
		}
		
		return xml;
	}
		
	private String parseSecond(Map<String,String> map,String responseXml){
		
		logger.info("parse second : "+responseXml);
		
		if(responseXml != null && responseXml.length() > 0){
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if(STATUS_WAIT.equals(status)){//等待
				return DynamicUtils.parseWait(7,map);
			}else{ 
				tryMap.remove(map.get("content_sid"));
				
				if(STATUS_SUCC.equals(status)){//完成
					return parseSecondSucc(map,responseXml);
				}else{//错误
					
					
					return DynamicUtils.parseError(status);
				}
			}
		}
		
		return null;
	}
	
	private String parseSecondSucc(Map<String,String> map,String xml){
		logger.info("parse second succ : "+xml);
		
		
		return null;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	
	public static void main(String[] args){
		test1();
	}
	
	private static void test1(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://182.92.21.219:10789/cmcc/g/online/s2sAutoRegist");
		map.put("imei", "862594025131367");
		map.put("imsi", "460025284891073");
		map.put("theNo","1");
		map.put("pid","2241021504-2248562366");
		map.put("channel","10B001a012345678");
		map.put("chargeId", "006039990023");
		map.put("channelId", "41084000");
		
		System.out.println(new ShylWyDynamicService().dynamic(map));
	}
}
