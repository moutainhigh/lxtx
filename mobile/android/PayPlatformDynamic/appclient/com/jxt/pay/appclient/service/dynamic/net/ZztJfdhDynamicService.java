package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 手游世纪电信
 * @author leoliu
 *
 */
public class ZztJfdhDynamicService implements IDynamicService{
	
	private static final String TYPE = "zztjfdh";
	private static Logger logger = Logger.getLogger(ZztJfdhDynamicService.class);
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String appkey = map.get("appkey");
		String vcode = map.get("vcode");
		String merchantcode = map.get("merchantcode");
		String bizcode = map.get("bizcode");
		String sign = MD5Encrypt.MD5Encode(appkey+vcode+"q5hvF21uh");
		
		NameValuePair[] pairs = new NameValuePair[5]; 
		pairs[0] = new NameValuePair("appkey",appkey);
		pairs[1] = new NameValuePair("vcode",vcode);
		pairs[2] = new NameValuePair("merchantcode",merchantcode);
		pairs[3] = new NameValuePair("sign",sign);
		pairs[4] = new NameValuePair("bizcode",bizcode);
		
		String responseJson = PostParamsData.postData(url,pairs);
		
		
//		logger.info(responseJson);
		if(responseJson != null && responseJson.length() > 0){
			
			try {
				JSONObject jo = new JSONObject(responseJson);
				String data = jo.getString("data");
				if(data != null && data.length() > 0){
					
				JSONObject jo1 = new JSONObject(data);
				
				String code = jo1.getString("code");
				
				if(code.equals("0")){
					
					String msg = jo1.getString("msg");
					Sets sets = new Sets();
					sets.setKey(msg);
					sets.setValue("msg");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(code);
				}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			
		return null;
	}
	
	

	public static void main(String[] args){
		
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://121.40.51.194:9090/rest/open/exec");
		map.put("type","zztjfdh");
		map.put("appkey","jf02");
		map.put("merchantcode","");
		map.put("vcode","1753228891");
		map.put("bizcode","ZW1001");
		System.out.println(new ZztJfdhDynamicService().dynamic(map));
	}
	
	
}
