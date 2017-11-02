package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

public class ZztDdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztDdoDynamicService.class);
	
	private static final String PARAMS = "content={content}&imsi={imsi}&imei={imei}&phone={phone}&ip={ip}";
	
	private static final String PARAMS1 = "orderid={orderid}&code={code}&phone={phone}";
	
	@Override
	public String getType() {
		return "zztDdo";
	}

	@Override
	public String dynamic(Map<String, String> map) {
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return first(map);
		}else if("2".equals(theNo)){
			return second(map);
		}
		
		return null;
	}
		
	private String first(Map<String, String> map){	
		String url = map.get("url");
		String content = map.get("content");
		String phone = map.get("phone");
		String imsi = StringUtils.defaultString(map.get("imsi"));
		String imei = StringUtils.defaultString(map.get("imei"));
		String ip = map.get(Constants.IPPARAM);
		
		String params = PARAMS.replace("{content}", content).replace("{imsi}",imsi).replace("{imei}",imei).replace("{phone}",phone).replace("{ip}",ip);
		
		String responseJson = GetData.getData(url,params);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				int status = jo.getInt("status");
			
				if(status == 0){
					String orderid = jo.getString("orderid");
					
					Sets sets = new Sets();
					sets.setKey("orderid");
					sets.setValue(orderid);
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(status+"");
				}
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	private String second(Map<String, String> map){
		
		String url = map.get("url");
		String orderid = map.get("orderid");
		String code = map.get("code");
		String phone = map.get("phone");
		
		String params = PARAMS1.replace("{orderid}",orderid).replace("{code}",code).replace("{phone}",phone);
		
		String responseJson = GetData.getData(url,params);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				int status = jo.getInt("status");
				
				if(status == 0){
					return "<wait>1</wait>";
				}else{
					return DynamicUtils.parseError(status+"");
				}
			}catch(Exception e){
			
			}
		}
		
		return null;
	}

	public static void main(String[] args){
//		test1();
		test2();
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("phone","13811155779");
		map.put("theNo","2");
		map.put("url","http://219.234.87.16:8088/CmccSmsMmsTunnelPlatform/bjddoconfirm");
		map.put("type","zztDdo");
		map.put("orderid","20160411115215849472304366379314");
		map.put("code","234850");
		
		System.out.println(new ZztDdoDynamicService().dynamic(map));
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","zztDdo");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		map.put("content","BJ10C");
		map.put("phone","13811155779");
		map.put("theNo","1");
		map.put(Constants.IPPARAM, "221.217.179.96");
		map.put("url","http://219.234.87.16:8088/CmccSmsMmsTunnelPlatform/bjddogetcode");
		
		System.out.println(new ZztDdoDynamicService().dynamic(map));
	}
	
}
