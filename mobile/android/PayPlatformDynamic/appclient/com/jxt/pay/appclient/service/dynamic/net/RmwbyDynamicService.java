package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class RmwbyDynamicService implements IDynamicService{

	private static final String TYPE = "rmwby";
	
	private static final String REQUESTPARAM1 = "coopcode={coopcode}&productid={productid}&paycode={paycode}&paycode={paycode}&cpid={cpid}&type=1";
	private static final String REQUESTPARAM2 = "orderid={orderid}&verifycode={verifycode}&type=1";
	
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
		
		String coopcode = map.get("coopcode");
		String productid = map.get("productid");
		String paycode = map.get("paycode");
		String mobile = map.get("mobile");
		String cpid = map.get("cpid");		
		
		String requestParam = REQUESTPARAM1.replace("{coopcode}", coopcode).replace("{productid}",productid).replace("{paycode}",paycode).replace("{mobile}",mobile).replace("{cpid}",cpid);
		
		String response = GetData.getData(url,requestParam);
		
		if(response != null && response.length() > 0){
			try {
				JSONObject jo = new JSONObject(response);
			
				String status = jo.getString("status");
				
				if(!"000000".equals(status)){
					return DynamicUtils.parseError(status);
				}else{
					Sets sets = new Sets();
					sets.setKey("orderid");
					sets.setValue(jo.getString("orderid"));
					
					return XstreamHelper.toXml(sets).toString();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		{
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
		String orderid = map.get("orderid");
		String verifycode = map.get("verifycode");
		
		String requestParam = REQUESTPARAM2.replace("{orderid}", orderid).replace("{verifycode}",verifycode);
		
		String response = GetData.getData(url,requestParam);
		
		if(response != null && response.length() > 0){
			try{
				JSONObject jo = new JSONObject(response);
				
				String status = jo.getString("status");
				
				if("000000".equals(status)){
					Sets sets = new Sets();
					sets.setKey("_succRet");
					sets.setValue("1");
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(status);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			Integer cnt = tryMap2.get(orderid);
			if(cnt == null){
				cnt = 0;
			}
			if(cnt < 2){
				tryMap2.put(orderid, cnt + 1);
				return DynamicUtils.parseWait(5,map);
			}
		}
		
		return DynamicUtils.parseError("597");
	}
	
	public static void main(String[] args){
//		test1();
		
//		test2();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://119.90.41.134/api/migucommic/querycode");
		map.put("type","rmwby");
		map.put("theNo","1");
		map.put("coopcode","");
		map.put("paycode","");
		map.put("cpid", "");
		map.put("productid", "");
		map.put("mobile","13811155779");
		
		System.out.println(new RmwbyDynamicService().dynamic(map));
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","rmwby");
		map.put("theNo","2");
		map.put("url","http://119.90.41.134/api/migucommic/submitcode");
		map.put("orderid", "1447230525652");
		map.put("verifycode","122450");
	
		System.out.println(new RmwbyDynamicService().dynamic(map));
	}

}
