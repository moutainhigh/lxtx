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

public class RmkjRdoDynamicService implements IDynamicService{

	private static final Logger logger = Logger.getLogger(RmkjRdoDynamicService.class);
	
	private static final String TYPE = "rmkjRdo";
	
	private static final String PARAMS1 = "ppid={ppid}&imsi={imsi}&msisdn={mobile}&custom={custom}";
	private static final String PARAMS2 = "ppid={ppid}&code={code}&orderid={orderid}";
	
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
		String ppid = map.get("ppid");
		String custom = map.get("custom");
		
		String params = PARAMS1.replace("{mobile}",mobile ).replace("{imsi}", imsi).replace("{ppid}",ppid ).replace("{custom}", custom);
		
		String responseJson = GetData.getData(url,params);
		
		logger.info("responseJson1:"+responseJson);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				int status = jo.getInt("status");
				
				if(status == 200){
					String orderid = jo.getString("orderid");
					
					Sets sets2 = new Sets();
					sets2.setKey("orderid");
					sets2.setValue(orderid);
					
					return XstreamHelper.toXml(sets2).toString();
				}else{
					return DynamicUtils.parseError(status+"");
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
		String ppid = map.get("ppid");
		String code = map.get("code");
		
		String params = PARAMS2.replace("{orderid}", orderid).replace("{code}", code).replace("{ppid}", ppid);
		
		String responseJson = GetData.getData(url,params);
		
		logger.info("responseJson2:"+responseJson);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				int status = jo.getInt("status");
				
				if(status == 200){
					Sets sets = new Sets();
					sets.setKey("succ_");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(status+"");
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
		
		map.put("url","http://api.umread.com:90/rdo/confirm");
		map.put("code","1234");
		map.put("orderid","rong_000002218129");
		map.put("ppid","RONGM_006");
		map.put("theNo", "2");
		map.put("type", "rmkjRdo");
		
		System.out.println(new RmkjRdoDynamicService().dynamic(map));
		
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://api.umread.com:90/rdo/order");
		map.put("ppid","RONGM_006");
		map.put("imsi", "460022475975452");
		map.put("mobile","15247539458");
		map.put("custom","11dadssa");
		map.put("theNo", "1");
		map.put("type", "rmkjRdo");
		
		System.out.println(new RmkjRdoDynamicService().dynamic(map));
	}

}
