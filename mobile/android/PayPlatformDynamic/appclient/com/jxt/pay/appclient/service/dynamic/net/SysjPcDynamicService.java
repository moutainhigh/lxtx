package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 赞成科技 网游
 * @author leoliu
 *
 */
public class SysjPcDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(SysjPcDynamicService.class);
	
	private static final String TYPE = "sysjPc";
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String PARAM_MODEL1 = "userid={userid}&consumecode={consumecode}&tel={tel}";
	
	private static final String PARAM_MODEL2 = "userid={userid}&consumecode={consumecode}&tel={tel}&verifycode={verifycode}&orderid={orderid}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
		}else if(THENO_2.equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}

	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String userid = map.get("userid");
			String consumecode = map.get("consumecode");
			String tel = map.get("mobile");
			
			String param = PARAM_MODEL1.replace("{userid}", userid).replace("{consumecode}",consumecode)
					.replace("{tel}", tel);
			
			String responseJson = GetData.getData(url,param);
			
			if(responseJson != null && responseJson.length() > 0){
				try{
					JSONObject jo = new JSONObject(responseJson);
					
					String resultCode = jo.getString("resultCode");
					
					if("200000".equals(resultCode)){
						String orderid = jo.getString("orderid");
						
						Sets sets = new Sets();
						sets.setKey("orderid");
						sets.setValue(orderid);
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(resultCode);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String,String> map){
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String userid = map.get("userid");
			String consumecode = map.get("consumecode");
			String tel = map.get("mobile");
			String verifycode = map.get("verifycode");
			String orderid = map.get("orderid");
			
			String param = PARAM_MODEL2.replace("{userid}", userid).replace("{consumecode}",consumecode)
					.replace("{tel}", tel).replace("{orderid}",orderid).replace("{verifycode}",verifycode);
			
			String responseJson = GetData.getData(url,param);
			
			if(responseJson != null && responseJson.length() > 0){
				try{
					JSONObject jo = new JSONObject(responseJson);
					
					String resultCode = jo.getString("resultCode");
					
					if("200000".equals(resultCode)){
						
						Sets sets = new Sets();
						sets.setKey("succ_");
						sets.setValue("1");
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(resultCode);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}
		
		
		return null;
	}
	
	public static void main(String[] args){
//		test1();
		test2();
	}
	
	private static void test1(){
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("url", "http://112.74.127.84:8080/cs_open/inputTel.servlet");
		map.put("userid", "4008");
		map.put("consumecode", "006063393008");
		map.put("mobile", "18801032292");
		map.put("type", "sysjPc");
		map.put("theNo", "1");
		
		logger.info("result : "+new SysjPcDynamicService().dynamic(map));
	}
	
	private static void test2(){
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("url", "http://115.29.201.88/sync/zhwlconfirmpc");
		map.put("userid", "4008");
		map.put("consumecode", "006063393008");
		map.put("mobile", "18801032292");
		map.put("type", "sysjPc");
		map.put("theNo", "2");
		map.put("orderid","0200000f23d55ce3000555c7");
		map.put("verifycode","354523");
		
		logger.info("result : "+new SysjPcDynamicService().dynamic(map));
	}
}
