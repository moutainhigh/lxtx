package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

public class OldDdoDynamicService implements IDynamicService{

	private static final String TYPE = "ddo";
	
	private static final String REQUESTPARAM1 = "mobile={mobile}&configAppId={appId}&configPayCode={payCode}&channelCode={channelCode}";
	private static final String REQUESTPARAM2 = "orderId={orderId}&verifyCode={verifyCode}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else{
			return secondDynamic(map);
		}
		
	}
	
	private Map<String, Integer> tryMap1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String,String> map){
		String channel = map.get("channel");
		String url = map.get("url");
		String appId = map.get("appId");
		String payCode = map.get("payCode");
		String mobile = map.get("mobile");
		String channelCode = StringUtils.defaultString(map.get("channelCode"));
				
		String requestParam = REQUESTPARAM1.replace("{mobile}", mobile).replace("{appId}",appId).replace("{payCode}",payCode).replace("{channelCode}",channelCode);
		
		String response = GetData.getData(url,requestParam);
		
		if(response != null && response.length() > 0){
			try {
				JSONObject jo = new JSONObject(response);
			
				String errorCode = jo.getString("errorCode");
				
				if(!"0".equals(errorCode)){
					return DynamicUtils.parseError(errorCode);
				}else{
					Sets sets = new Sets();
					sets.setKey("orderId");
					sets.setValue(jo.getString("orderId"));
					
					return XstreamHelper.toXml(sets).toString();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			Integer cnt = tryMap1.get(channel);
			if(cnt == null){
				cnt = 0;
			}
			if(cnt < 2){
				tryMap1.put(channel, cnt + 1);
				return DynamicUtils.parseWait(5,map);
			}
		}
		
		return DynamicUtils.parseError("597");
	}
	
	private Map<String, Integer> tryMap2 = new HashMap<String, Integer>();
	
	private String secondDynamic(Map<String,String> map){
		String url = map.get("url");
		String orderId = map.get("orderId");
		String verifyCode = map.get("verifyCode");
		
		String requestParam = REQUESTPARAM2.replace("{orderId}", orderId).replace("{verifyCode}",verifyCode);
		
		String response = GetData.getData(url,requestParam);
		
		if(response != null && response.length() > 0){
			try{
				JSONObject jo = new JSONObject(response);
				
				String errorCode = jo.getString("errorCode");
				
				if("0".equals(errorCode)){
					return "<wait>1</wait>";
				}else{
					return DynamicUtils.parseError(errorCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			Integer cnt = tryMap2.get(orderId);
			if(cnt == null){
				cnt = 0;
			}
			if(cnt < 2){
				tryMap2.put(orderId, cnt + 1);
				return DynamicUtils.parseWait(5,map);
			}
		}
		
		return DynamicUtils.parseError("597");
	}
	
	public static void main(String[] args){
//		testInit();
		
		testVerify();
	}

	private static void testInit(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://115.28.52.43:9001/ddo/appclient/init.do");
		map.put("type","ddo");
		map.put("theNo","1");
		map.put("appId","1001");
		map.put("payCode","001");
		map.put("channelCode", "lx");
		map.put("mobile","13811155779");
		
		System.out.println(new OldDdoDynamicService().dynamic(map));
	}
	
	private static void testVerify(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","ddo");
		map.put("theNo","2");
		map.put("url","http://115.28.52.43:9001/ddo/appclient/verify.do");
		map.put("orderId", "1447230525652");
		map.put("verifyCode","122450");
	
		System.out.println(new OldDdoDynamicService().dynamic(map));
	}
}
