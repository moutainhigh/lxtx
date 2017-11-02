package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class HyzxRdoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HyzxRdoDynamicService.class);
	
	private static final String TYPE = "hyzxRdo";
	
	private static final String PARAM1 = "mobile={mobile}";
	private static final String PARAM2 = "orderid={orderid}&verifyCode={verifyCode}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String RESULTCODE_SUCC = "1";
	
	
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
	
	public static String gettime(){
		String time = ""+System.currentTimeMillis()/1000;
		return time;
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			String mobile = StringUtils.defaultString(map.get("dmobile"));
			
			
			
			String param = PARAM1.replace("{mobile}",mobile);
			
			String responseJson = GetData.getData(url, param);
			logger.info("-----"+responseJson);
			xml = parseFirst(map,responseJson);

			
		}else{
					
			xml = DynamicUtils.parseWait(597);//获取失败
		
			
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
				
				if(jo.has("resultcode")){
					String resultcode = jo.getString("resultcode");
					
					logger.info("parse first result code : "+resultcode);
					
					if(RESULTCODE_SUCC.equals(resultcode)){
						String orderid = jo.getString("orderid");
						
						Sets sets = new Sets();
						sets.setKey("orderid");
						sets.setValue(orderid);
						
						
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						
						
						
						return DynamicUtils.parseError(resultcode);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
		
	private String secondDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String orderid = map.get("orderid");
		
		if(url != null && url.length() > 0){
			
			String verifyCode = map.get("verifyCode");
			
			
			String param = PARAM2.replace("{verifyCode}",verifyCode).replace("{orderid}",orderid);
			
			String responseJson = GetData.getData(url, param);
			
			logger.info("responseJson2:"+responseJson);
			
			xml = parseSecond(map,responseJson);
			
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
				
				if(jo.has("result")){
					String result = jo.getString("result");
					
					logger.info("parse second result code : "+result);
					
					if(RESULTCODE_SUCC.equals(result)){
						Sets sets = new Sets();
						sets.setKey("result");
						sets.setValue(result);
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						
						
						return DynamicUtils.parseError(result);
					}
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

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();	
		
		map.put("theNo", "1");
		map.put("url", "http://61.160.251.130:5151/interface/362");
		map.put("type",TYPE);
		map.put("dmobile", "18240324873");
		
		logger.info(new HyzxRdoDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("url", "http://baokuz.cn/sp/GZYX/nrdo/mr.php");
		map.put("type",TYPE);
		map.put("orderid", "14659608221881139");
		map.put("verifyCode","916828");
		
		logger.info(new HyzxRdoDynamicService().dynamic(map));
	}
}
