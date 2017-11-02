package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * http://cartoon.yiqiao580.com:9800/crack/cartoon/paysms.do?ptid=40000&imsi=460001123143655&imei=867376023133651&cpparam=test123&version=1.0.0&itemId=300007590001&itemPrice=10&itemSafeLevel=2&itemMethod=11&itemEx=
 * @author leoliu
 *
 */
public class DmSdkDynamicService implements IDynamicService{

	private static final String TAG = "dmSdk";
	private static Logger logger = Logger.getLogger(DmSdkDynamicService.class);
	
	private String PARAMS = "ptid={ptid}&imsi={imsi}&imei={imei}&cpparam={cpparam}&version=1.0.0&itemId={itemId}&itemPrice={itemPrice}&itemSafeLevel={itemSafeLevel}&itemMethod={itemMethod}&itemEx=";
	
	@Override
	public String getType() {
		return TAG;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String channel = map.get("channel");
		String ptid = map.get("ptid");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String itemId = map.get("itemId");
		String itemPrice = map.get("itemPrice");
		String itemSafeLevel = map.get("itemSafeLevel");
		String itemMethod = map.get("itemMethod");
		
		String params = PARAMS.replace("{ptid}", ptid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{cpparam}",channel).replace("{itemId}",itemId).replace("{itemPrice}",itemPrice).replace("{itemSafeLevel}",itemSafeLevel).replace("{itemMethod}",itemMethod);
		
		String responseJson = GetData.getData(url, params);
		
		if(responseJson != null && responseJson.length() > 0){
			try {
				JSONObject jo = new JSONObject(responseJson);
				
				int result = jo.getInt("result");
				
				if(result == 0){
					String smsContent = jo.getString("sms");
					
					smsContent = CommonUtil.base64Decode(smsContent);
//					System.out.println(jo.getString("orderno"));
					System.out.println(smsContent);
					String port = jo.getString("port");
					
					Sms sms = new Sms();
					sms.setSmsDest(port);
					sms.setSmsContent(smsContent);
					sms.setSuccessTimeOut(2);
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError(""+result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void main(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","dmSdk");
//		map.put("url","http://cartoon.yiqiao580.com:9900/crack/cartoon/paysms.do");
		map.put("url","http://jjd.yiqiao580.com:9700/crack/cartoon/paysms.do");
		map.put("ptid", "40024");
		map.put("channel","13B101a014444316");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		
		map.put("itemId","300007631010");
		map.put("itemPrice","2000");
		map.put("itemSafeLevel","2");
		map.put("itemMethod","11");
		
		System.out.println(new DmSdkDynamicService().dynamic(map));
	}
}
