package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;
import org.apache.commons.httpclient.NameValuePair;

public class SztsLtDynamicService implements IDynamicService{

	private static final String TYPE = "sztsLt";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	private static final Guard guard0 = new Guard("10655477","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10655","",960,null,1);
	
	private int timeOut = 60;
	
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	@Override
	public String getType() {
		return TYPE;
	}

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
			
			String goodsId = map.get("goodsId");
			String goodsInf = map.get("goodsInf");
			String key = map.get("key");
			
			goodsInf = goodsInf+"999999";
			
			String sign = MD5Encrypt.MD5Encode(goodsId+"#"+goodsInf+"#"+key).toLowerCase();
			
			NameValuePair[] pairs = new NameValuePair[3];
			pairs[0] = new NameValuePair("goodsId", goodsId);
			pairs[1] = new NameValuePair("goodsInf", goodsInf);
			pairs[2] = new NameValuePair("sign", sign);
			
			String responseXml = PostParamsData.postData(url,pairs);
			System.out.println(responseXml);
			xml = parseXml(responseXml);
			
			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}

	private String parseXml(String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			String SpNumber = SingleXmlUtil.getNodeValue(responseXml, "SpNumber");
			String Code = SingleXmlUtil.getNodeValue(responseXml, "Code");
			
			if(SpNumber != null && SpNumber.length() > 0 && Code != null && Code.length() > 0){
				Code = Code.replace("&amp;","&");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(Code);
				sms.setSmsDest(SpNumber);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				smsList.add(sms);
				
				Sms guardSms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard0);
				guardList.add(guard1);
							
				guardSms.setGuardList(guardList);
				
				smsList.add(0, guardSms);
					
				return XstreamHelper.toXml(smsList);
				
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		String url = "http://183.62.161.183:8180/mobile/LTWO/RecOrder";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("goodsId", "000020000027");
		map.put("goodsInf","000020000027042#");
		map.put("key","LTWO#legaopay");
		map.put("channel","10B201a123456789");
		
		System.out.println(new SztsLtDynamicService().dynamic(map));
	}
}
