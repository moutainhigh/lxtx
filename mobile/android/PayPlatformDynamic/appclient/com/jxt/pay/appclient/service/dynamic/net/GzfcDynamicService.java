package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;





import org.apache.log4j.Logger;
import org.json.JSONObject;

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
public class GzfcDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(GzfcDynamicService.class);
	
	private static final String TYPE = "gzfc";
	
	private static final String PARAM1 = "iMei={iMei}&iMsi={iMsi}&callNumber=00000000000&appId={appId}&productId={productId}&pNumber={pNumber}";
	
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
		
			return firstDynamic(map);

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
			
								
			String param = PARAM1.replace("{iMei}", iMei).replace("{iMsi}",iMsi).replace("{appId}",appId).replace("{productId}",productId).replace("{pNumber}",pNumber);
																				
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
				
				if(jo.has("sms")){

					String send_content = jo.getString("sms");
					String dest_num = jo.getString("port");
					
					Sms sms = new Sms();
					
					sms.setSmsContent(send_content);
					sms.setSmsDest(dest_num);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					return XstreamHelper.toXml(sms).toString();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		test1();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
	
		map.put("url", "http://shouyousdk.com/menager/requestPay");	
		map.put("productId", "0000001331");
		map.put("appId","3000004828");
		map.put("imsi","460011532923134");
		map.put("imei", "869460011612203");
		map.put("pNumber", "1101lx");
		
		System.out.println(new GzfcDynamicService().dynamic(map));
	}
}
