package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class RmkjWoDynamicService implements IDynamicService{

	private static final Logger logger = Logger.getLogger(RmkjWoDynamicService.class);
	
	private static final String TYPE = "rmkjWo";
	
	private static final String PARAMS1 = "channelid={channelid}&imsi={imsi}&mobile={mobile}&p_price={p_price}&callbackdata={callbackdata}&appname=yx";
	private static final String PARAMS2 = "transactionid={transactionid}&verifycode={verifycode}&orderid={orderid}";
	
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
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String mobile = map.get("mobile");
		String imsi = map.get("imsi");
		String p_price = map.get("p_price");
		String callbackdata = map.get("callbackdata");
		String channelid = map.get("channelid");
		
		String params = PARAMS1.replace("{mobile}",mobile ).replace("{imsi}", imsi).replace("{p_price}",p_price ).replace("{callbackdata}", callbackdata).replace("{channelid}",channelid);
		
		String responseJson = GetData.getData(url,params);
		
		logger.info("responseJson1:"+responseJson);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				int resultcode = jo.getInt("resultcode");
				
				if(resultcode == 0){
					String transactionid = jo.getString("transactionid");
					String orderid = jo.getString("orderid");
					
					Sets sets1 = new Sets();
					sets1.setKey("transactionid");
					sets1.setValue(transactionid);
					
					Sets sets2 = new Sets();
					sets2.setKey("orderid");
					sets2.setValue(orderid);
					
					return XstreamHelper.toXml(sets1).append(XstreamHelper.toXml(sets2)).toString();
				}else{
					return DynamicUtils.parseError(resultcode+"");
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String orderid = map.get("orderid");
		String verifycode = map.get("verifycode");
		String transactionid = map.get("transactionid");
		
		String params = PARAMS2.replace("{orderid}", orderid).replace("{verifycode}", verifycode).replace("{transactionid}", transactionid);
		
		String responseJson = GetData.getData(url,params);
		
		logger.info("responseJson2:"+responseJson);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				int resultcode = jo.getInt("resultcode");
				
				if(resultcode == 0){
					Sets sets = new Sets();
					sets.setKey("succ_");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(resultcode+"");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
//		test1();
		test2();
	}
	
	private static void test2(){
		//http://g.biedese.cn/vb/req_sms_base64?paykey=301400430000000-10154262-618224757&imsi=460022089262849&cpid=218&cp_param=13G40Ga125172937
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://27.50.136.66:8901/tsms/dopay");
		map.put("verifycode","1234");
		map.put("orderid","1605053AFY8N00671186");
		map.put("transactionid","146242877718757632");
		map.put("theNo", "2");
		map.put("type", "rmkjWo");
		
		System.out.println(new RmkjWoDynamicService().dynamic(map));
		
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://27.50.136.66:8901/tsms/getvcode");
		map.put("channelid","1186");
		map.put("imsi", "460014976002358");
		map.put("mobile","13064979649");
		map.put("p_price","10");
		map.put("callbackdata","1113G41Aa126822200");
		map.put("theNo", "1");
		map.put("type", "rmkjWo");
		
		System.out.println(new RmkjWoDynamicService().dynamic(map));
	}

}
