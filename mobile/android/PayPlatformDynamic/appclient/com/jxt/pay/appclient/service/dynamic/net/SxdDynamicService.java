package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 神仙道
 * @author leoliu
 *
 */
public class SxdDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(SxdDynamicService.class);
	
	private static final String TYPE = "sxd";
	
	private static final String PARAM1 = "app={app}&pid={pid}&money={money}&time={time}&sign={sign}&tel={tel}";
	private static final String PARAM2 = "app={app}&pid={pid}&money={money}&time={time}&sign={sign}&orderid={orderid}&verifycode={verifycode}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "200000";
	private static final String RESULTCODE_WAIT = "200002";
	
	private int timeOut = 60;
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key 
	 */
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
			String app = map.get("app");
			String pid = map.get("pid");
			String money = map.get("money");
			String key = map.get("key");
			
			long time = System.currentTimeMillis()/1000;
		
			StringBuffer sb = new StringBuffer();
			sb.append("app=").append(app).append("&pid=").append(pid);
			sb.append("&time=").append(time).append("&key=").append(key);
			
			String src = sb.toString();
			logger.info("src : "+src);
			
			String sign = MD5Encrypt.MD5Encode(src).toLowerCase();
			logger.info("sign : "+sign);
			
			
			String param = PARAM1.replace("{pid}", pid).replace("{app}",app).replace("{money}",money).replace("{time}",time+"").replace("{sign}",sign);
			param = param.replace("{tel}", mobile);
			
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
					String orderid = jo.getString("orderid");
					
					Sets sets = new Sets();
					sets.setKey("orderid");
					sets.setValue(orderid);
					
					return XstreamHelper.toXml(sets).toString();
				}else if(RESULTCODE_WAIT.equals(resultCode)){
					return null;
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
			
			
			String app = map.get("app");
			String pid = map.get("pid");
			String money = map.get("money");
			String key = map.get("key");
			
			long time = System.currentTimeMillis()/1000;
			
			StringBuffer sb = new StringBuffer();
			sb.append("app=").append(app).append("&pid=").append(pid);
			sb.append("&time=").append(time).append("&key=").append(key);
			
			String src = sb.toString();
			logger.info("src : "+src);
			
			String sign = MD5Encrypt.MD5Encode(src).toLowerCase();
			logger.info("sign : "+sign);
			
			String verifycode = map.get("verifycode");
			
			String param = PARAM2.replace("{pid}", pid).replace("{app}",app).replace("{money}",money).replace("{time}",time+"").replace("{sign}",sign);
			param = param.replace("{orderid}", orderid).replace("{verifycode}",verifycode);
			
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
				}else if(RESULTCODE_WAIT.equals(resultCode)){
					return null;
				}else{
					return DynamicUtils.parseError(resultCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void main(String[] args){
		test2();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "1");
		map.put("url", "http://111.13.47.76:81/open_gate/web_game_fee.php");
		map.put("money", "8");
		map.put("pid", "5001");
		map.put("app","dgcs");
		map.put("dmobile", "13811155779");
		map.put("key", "bcdd8f67c9620bb0d2286c41fe904249");
		
		System.out.println(new SxdDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://111.13.47.76:81/open_gate/web_game_callback.php");
		map.put("money", "8");
		map.put("pid", "5001");
		map.put("app","dgcs");
		map.put("key", "bcdd8f67c9620bb0d2286c41fe904249");
		
		map.put("orderid", "0900000f1e961f74004acf50");
		map.put("verifycode","001168");
		
		System.out.println(new SxdDynamicService().dynamic(map));
	}
}
