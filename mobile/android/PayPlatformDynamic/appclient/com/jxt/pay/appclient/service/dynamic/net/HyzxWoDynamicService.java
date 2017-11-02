package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;

/**
 * 手游世纪电信
 * @author leoliu
 *
 */
public class HyzxWoDynamicService implements IDynamicService{
	
	private static final String TYPE = "hyzxWo";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else if("2".equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}
	
	private String firstDynamic(Map<String,String> map){
		String url = map.get("url");
		String mobile = map.get("mobile");
		String chargeCode = map.get("chargeCode");
		
		NameValuePair[] pairs = new NameValuePair[2]; 
		pairs[0] = new NameValuePair("mobile",mobile);
		pairs[1] = new NameValuePair("chargeCode",chargeCode);
		
		String responseJson = PostParamsData.postData(url,pairs);
		System.out.println(responseJson);
		if(responseJson != null && responseJson.length() > 0){
//			logger.info("responseJson:"+responseJson);
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String resultcode = jo.getString("code");
				
				if(resultcode.equals("0")){
					JSONObject jo1 = jo.getJSONObject("result");
					
					JSONArray actionList = jo1.getJSONArray("actionList");
					JSONObject jo2 = actionList.getJSONObject(0);
					
					String actionTarget = jo2.getString("actionTarget");
					JSONObject actionParam = jo2.getJSONObject("actionParam");
					
					String orderId = actionParam.getString("orderId");
					
					Sets sets = new Sets();
					sets.setKey("actionTarget");
					sets.setValue(actionTarget);
					
					Sets sets1 = new Sets();
					sets1.setKey("orderId");
					sets1.setValue(orderId);
						
					StringBuffer sb = new StringBuffer();
					sb.append(XstreamHelper.toXml(sets));
					sb.append(XstreamHelper.toXml(sets1));
					
					return sb.toString();
				}else{
					return DynamicUtils.parseError(resultcode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String,String> map){
		String url = map.get("url");
		String orderId = map.get("orderId");
		String authCode = map.get("authCode");
		
		try{
			JSONObject joparam = new JSONObject();
			joparam.put("orderId", orderId);
			joparam.put("authCode",authCode);
			
			String body = joparam.toString();
			
			String responseJson = new PostData().PostData(body.getBytes(),url, null);
			
			if(responseJson != null && responseJson.length() > 0){
				try {
					JSONObject jo = new JSONObject(responseJson);
					
					String code = jo.getString("code");
					
					if(code.equals("0")){
						Sets sets = new Sets();
						sets.setKey("succ_");
						sets.setValue("1");
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(code);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return null;
	}

	public static void main(String[] args){
		
		test1();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://123.206.52.71/sync/wojiareq");
		map.put("type","hyzxWo");
		map.put("mobile","15861991830");
		map.put("chargeCode","HY2202001000");
		map.put("theNo","1");
		
		System.out.println(new HyzxWoDynamicService().dynamic(map));
	}
	
	private static void test2(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://api.jiebasdk.com/SmsPay/YbkConfirmPayment/");
		map.put("type","sysjWo");
		map.put("orderId","S001011265");
		map.put("authCode","010000");
		map.put("theNo","2");
		
		System.out.println(new HyzxWoDynamicService().dynamic(map));
	}
}
