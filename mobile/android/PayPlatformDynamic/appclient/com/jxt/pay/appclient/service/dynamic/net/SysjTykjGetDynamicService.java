package com.jxt.pay.appclient.service.dynamic.net;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;

/**
 * 手游世纪电信
 * @author leoliu
 *
 */
public class SysjTykjGetDynamicService implements IDynamicService{

	private static final String TYPE = "sysjTykjGet";
	private static final String PARAMS = "cpid={cpid}&appid={appid}&paycode={paycode}&imsi={imsi}&imei={imei}&ip={ip}&cpparam={cpparam}";
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String cpid = map.get("cpid");
		String appid = map.get("appid");
		String paycode = map.get("paycode");
		String imei = map.get("imei");
		String imsi = map.get("imsi");
		String ip = map.get(Constants.IPPARAM);
		String cpparam = map.get("channel");
		
		String params = PARAMS.replace("{cpid}",cpid ).replace("{appid}",appid ).replace("{paycode}",paycode ).replace("{imsi}",imsi ).replace("{imei}", imei).replace("{ip}",ip )
				.replace("{cpparam}",cpparam );
		
		
//		String responseJson = PostParamsData.postData(url,pairs);
		String responseJson = GetData.getData(url,params);
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String resultcode = jo.getString("resultcode");
				
				if(resultcode.equals("0")){
					String port = jo.getString("smsport");
					
					String content = jo.getString("sms");
					content = CommonUtil.base64Decode(content);
					
					Sms sms = new Sms();
					sms.setSmsDest(port);
					sms.setSmsContent(content);
					sms.setSuccessTimeOut(2);
					
					return XstreamHelper.toXml(sms).toString();
						
				}else{
					return DynamicUtils.parseError(resultcode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void main(String[] args){
//		http://115.28.112.17/ctccapi/service?cpid=14&appid=XXXXX&paycode=XXXX&imei=861022010004016&imsi=460030301576273&ip=111.40.201.18&cpparam=XXX
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://115.28.112.17/ctccapi/service");
		map.put("type","sysjTykjGet");
		map.put("cpid","29");
		map.put("appid","167B8A0A08593393E0530100007FEA27");
		map.put("paycode","1C142F0160AE7A04E0530100007F1AC5");
		map.put("imei","867451025555753");
		map.put("imsi","460078010952058");
		map.put(Constants.IPPARAM,"111.40.201.18");
		map.put("channel", "G10HYA");
		
		System.out.println(new SysjTykjGetDynamicService().dynamic(map));
	}
	
	
	
}
