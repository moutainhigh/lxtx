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

public class RmkjDmdbDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(RmkjDmdbDynamicService.class);
	
	private static final String TYPE = "rmkjDmdb";//融梦科技动漫点播
	
	private static final String PARAM = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><p><pid>{pid}</pid><version>1.0.0.8</version><channelId>{channelId}</channelId><imsi>{imsi}</imsi><imei>{imei}</imei><chargeId>{chargeId}</chargeId><payId>{payId}</payId></p>";
	
	private static final String DEST = "1065842410";
	
	private int timeOut = 60;
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
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
			String pid = map.get("pid");
			String chargeId = map.get("chargeId");
			String channelId = map.get("channelId");
			String payId = map.get("payId");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			
			String param = PARAM.replace("{channelId}", channelId).replace("{pid}",pid).replace("{imsi}",imsi)
					.replace("{imei}",imei).replace("{chargeId}",chargeId).replace("{payId}",payId);
			
			String responseXml = new PostData().PostData(param.getBytes(), url);
			logger.info("responseXml:"+responseXml);
			if(responseXml != null && responseXml.length() > 0 && !"null".equals(responseXml)){
	
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
			String SMS = SingleXmlUtil.getNodeValue(data, "SMS");
			
			if(SMS != null && SMS.length() > 10){
				String address = SingleXmlUtil.getNodeValue(data, "address");
				Sms sms = new Sms();
				
				sms.setSmsContent(SMS);
				sms.setSmsDest(address);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);				
				
				
				return XstreamHelper.toXml(sms).toString();
			
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://218.5.76.147:10783/sdkServer3/getBillingSMS");
		map.put("type",TYPE);
		map.put("channelId", "800000404");
		map.put("pid", "300019");
		map.put("chargeId", "300003599006");
		map.put("payId", "MP341");
		map.put("imei","867451025555753");
		map.put("imsi", "460078010952058");
		
		
		String xml = new RmkjDmdbDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
}
