package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class FfkjPcDynamicService implements IDynamicService{
	
	private static Logger logger = Logger.getLogger(FfkjPcDynamicService.class);

	private static final String TYPE = "ffkjPc";
	
	private static final String PARAM1 = "Tel={Tel}&bid={bid}&cid={cid}";
	private static final String PARAM2 = "sid={sid}&vid={vid}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "0";
	
	private int timeOut = 60;
	
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

private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String mobile = map.get("dmobile");
		
		if(url != null && url.length() > 0){
			String bid = map.get("bid");
			String cid = map.get("cid");
			
			String param = PARAM1.replace("{bid}", bid).replace("{cid}",cid);
			param = param.replace("{Tel}", mobile);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parseFirst(map,responseJson);

			if(xml == null){
				Integer cnt = map1.get(mobile);
				
				if(cnt == null){
					cnt = 0;
				}
				
				if(cnt >= 3){
					map1.remove(mobile);
					xml = DynamicUtils.parseError("598");
				}else{
					cnt ++;
					map1.put(mobile, cnt);
					
					xml = DynamicUtils.parseWait(10,map);//获取失败
				}
			}else{
				map1.remove(mobile);
			}
			
		}
		
		return xml;
	}
	
	private String parseFirst(Map<String,String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				String resultCode = jo.getString("resultCode");
				
				logger.info("parse first result code : "+resultCode);
				
				if(RESULTCODE_SUCC.equals(resultCode)){
					String orderid = jo.getString("sid");
					
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
		
		return null;
	}
	
	//记录重复请求
		private Map<String, Long> tryMap = new HashMap<String, Long>();
		
		private String secondDynamic(Map<String, String> map) {
			
			String url = map.get("url");
			
			String xml = null;
			
			if(url != null && url.length() > 0){
				String orderid = map.get("orderid");
				
				Calendar cal = Calendar.getInstance();
				Long startTime = tryMap.get(orderid);
				
				if(startTime == null){
					startTime = cal.getTimeInMillis();
					tryMap.put(orderid,startTime);
				}else{
					if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
						tryMap.remove(orderid);
						return DynamicUtils.parseError("599");
					}
				}
				
				String verifycode = map.get("verifycode");
				
				String param = PARAM2.replace("{sid}", orderid).replace("{vid}",verifycode);
				
				String responseJson = GetData.getData(url, param);
				
				logger.info("responseJson2:"+responseJson);
				
				xml = parseSecond(map,responseJson);
				
			}
			
			if(xml == null){
				xml = DynamicUtils.parseWait(10,map);//获取失败
			}
			
			return xml;
		}

		
		private String parseSecond(Map<String, String> map, String responseJson) {
			if(responseJson != null && responseJson.length() > 0){
				try{
					int pos = responseJson.indexOf("{");
					
					if(pos > 0){
						responseJson = responseJson.substring(pos);
					}
					
					JSONObject jo = new JSONObject(responseJson);
					
					String resultCode = jo.getString("resultCode");
					
					logger.info("parse second result code : "+resultCode);
					
					if(RESULTCODE_SUCC.equals(resultCode)){
						Sets sets = new Sets();
						sets.setKey("resultCode1");
						sets.setValue(resultCode);
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(resultCode);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			return null;
		}
}