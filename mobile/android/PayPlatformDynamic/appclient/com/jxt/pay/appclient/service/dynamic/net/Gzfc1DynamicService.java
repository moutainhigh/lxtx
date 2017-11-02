package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;





import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;


/**
 * 赞成联通web
 * 1、http://vcc.geiliyou.com/web_gate_gly/fee.php?pid=BJLX&svcid=BJLXCX&paymentUser=18500973532&&imsi=460010970503904&imei=860806025781740sign=eab7745a880ea2ff956d8f2c5aa7705a
 * 2、http://vcc.geiliyou.com/web_gate_gly/callback.php?pid=BJLX&svcid=BJLXCX&paymentUser=18500973532&outTradeNo=BJLX120141107122932827963&paymentcodesms=651921&timeStamp=20130128023312&sign=ac3cd31fa903e1a8166f0d14319f6bb8
 * @author leoliu
 *
 */
public class Gzfc1DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(Gzfc1DynamicService.class);
	
	private static final String TYPE = "gzfc1";
	
	private static final String PARAM1 = "iMei={iMei}&iMsi={iMsi}&callNumber={mobile}&appId={appId}&productId={productId}&pNumber={pNumber}";
	private static final String PARAM2 = "taskid={taskid}&codesms={codesms}&phone={phone}";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
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
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else if("2".equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String productId = map.get("productId");
			String appId = map.get("appId");
			String iMsi = map.get("imsi");
			String iMei = map.get("imei");
			String pNumber = map.get("pNumber");
			String mobile = map.get("mobile");
								
			String param = PARAM1.replace("{iMei}", iMei).replace("{iMsi}",iMsi).replace("{appId}",appId).replace("{productId}",productId).replace("{pNumber}",pNumber).replace("{mobile}",mobile);
																				
			String responseJson = GetData.getData(url, param);
			logger.info("-----"+responseJson);
			xml = parseFirst(map,responseJson);

			if(xml == null){
				Integer cnt = map1.get(channel);
				
				if(cnt == null){
					cnt = 0;
				}
				
				if(cnt >= 3){
					map1.remove(channel);
					xml = DynamicUtils.parseError("598");
				}else{
					cnt ++;
					map1.put(channel, cnt);
					
					xml = DynamicUtils.parseWait(10,map);//获取失败
				}
			}else{
				map1.remove(channel);
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
				
				if(jo.has("resultcode")){
					String resultCode = jo.getString("resultcode");
					if("0".equals(resultCode)){
						Sets sets = new Sets();
						sets.setKey("taskid");
						sets.setValue(jo.getString("taskid"));
						
						return XstreamHelper.toXml(sets).toString();
					}else{
						return DynamicUtils.parseError(resultCode);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private Map<String, Integer> tryMap2 = new HashMap<String, Integer>();
	
	private String secondDynamic(Map<String, String> map){
		
		String taskid = map.get("taskid");
		String verifyCode = map.get("verifyCode");
		String mobile = map.get("mobile");
		String url = map.get("url");
		
		String param = PARAM2.replace("{taskid}", taskid).replace("{codesms}", verifyCode).replace("{phone}",mobile);
		
		String responseJson = GetData.getData(url, param);
		
		if(responseJson != null && responseJson.length() > 0){
			
			int pos = responseJson.indexOf("{");
			
			if(pos > 0){
				responseJson = responseJson.substring(pos);
			}
			
			try {
				tryMap2.remove(taskid);
				JSONObject jo = new JSONObject(responseJson);
				
				String resultcode = jo.getString("resultcode");
				if("0".equals(resultcode)){
					return "<wait>1</wait>";
				}else{
					return DynamicUtils.parseError(resultcode);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			Integer tryCnt = tryMap2.get(taskid);
			
			if(tryCnt == null){
				tryCnt = 0;
			}
			
			if(tryCnt < 2){
				tryMap2.put(taskid, tryCnt + 1);
				return DynamicUtils.parseWait(5,map);
			}else{
				tryMap2.remove(taskid);
				return DynamicUtils.parseError("599");
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
	
		map.put("url", "http://sypay.shouyousdk.com/menager/requestPay");	
		map.put("type", "gzfc1");
		map.put("theNo", "1");
		map.put("productId", "0000001323");
		map.put("appId","3000004875");
		map.put("imsi","460011532923134");
		map.put("imei", "869460011612203");
		map.put("pNumber", "95204000001");
		map.put("mobile", "18688889480");
		
		System.out.println(new Gzfc1DynamicService().dynamic(map));
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
	
		map.put("url", "http://sypay.shouyousdk.com/menager/cpcodeSmsPay");	
		map.put("type", "gzfc1");
		map.put("theNo", "2");
		map.put("taskid", "993ffcfb00b82e7f");
		map.put("verifyCode", "009362");
		map.put("mobile", "18688889480");
		
		System.out.println(new Gzfc1DynamicService().dynamic(map));
	}
}
