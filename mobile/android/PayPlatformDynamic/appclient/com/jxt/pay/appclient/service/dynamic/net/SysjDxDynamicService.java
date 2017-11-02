package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;

/**
 * 手游世纪电信
 * @author leoliu
 *
 */
public class SysjDxDynamicService implements IDynamicService{

	private static final String TYPE = "sysjDx";
	
	private static final String PARAMS = "channel={channelid}&imsi={imsi}&id_fee={id_fee}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String channelid = map.get("channelid");
		String imsi = map.get("imsi");
		String id_fee = map.get("id_fee");
		
		String params = PARAMS.replace("{channelid}", channelid).replace("{imsi}", imsi).replace("{id_fee}",id_fee);
		
		String responseJson = new PostData().PostData(params.getBytes(),url);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String result = jo.getString("result");
				
				if(result.equals("0")){
					String port = jo.getString("sms");
					
					JSONArray arr = jo.getJSONArray("detail");
					
					JSONObject jo1 = arr.getJSONObject(0);
					
					if(jo1 != null){
						Iterator<String> keys = jo1.keys();
						
						if(keys.hasNext()){
							String content = jo1.getString(keys.next());
							
							Sms sms = new Sms();
							sms.setSmsDest(port);
							sms.setSmsContent(content);
							sms.setSuccessTimeOut(2);
							
							return XstreamHelper.toXml(sms).toString();
						}
					}
				}else{
					return DynamicUtils.parseError(result);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void main(String[] args){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://101.200.210.58:8080/Pay/API");
		map.put("type","sysjDx");
		map.put("channelid","9051");
		map.put("imsi","460038414424852");
		map.put("id_fee","1008144597351_2");
		
		System.out.println(new SysjDxDynamicService().dynamic(map));
	}
	
	
	
}
