package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SendSms;
import com.jxt.pay.appclient.utils.StringUtils;

public class MmCheckDynamicService implements IDynamicService{

	private static final String TYPE = "mmCheck";
	private static Logger logger = Logger.getLogger(MmCheckDynamicService.class);
	private static final String URL = "http://114.215.148.230:8080/MsgCenter.asmx/CheckMM?imsi={imsi}&imei={imei}&phoneip={ip}";
	private static final String PARAMS = "&bsc_lac={bsc_lac}&bsc_cid={bsc_cid}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	private static final String SUCCRET = "<sets><key>checkRet</key><value>1</value></sets>";
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		
		if(hour <= 6 || hour >= 20){
			return SUCCRET;
		}
		
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String ip = map.get(Constants.IPPARAM);
		
		String url = URL.replace("{imsi}",imsi).replace("{imei}",imei).replace("{ip}",ip);
		
		String bsc_cid = StringUtils.defaultString(map.get("bsc_cid"));
		String bsc_lac = StringUtils.defaultString(map.get("bsc_lac"));
		
		if(bsc_cid.length() > 0 && bsc_lac.length() > 0){
			String params = PARAMS.replace("{bsc_lac}", bsc_lac).replace("{bsc_cid}",bsc_cid);
			url = url + params;
		}
		
		String responseJson = GetData.getData(url);
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info("responseJson:"+responseJson);
			try {
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.getInt("result") == 0){
					return SUCCRET;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			return SUCCRET;
		}
		
//		SendSms.send("mmcheckurl err");
		
		return "<error>mmCheckErr</error>";
	}
	
	public static void main(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","mmCheck");
		map.put("imsi","460004032235980");
		map.put("imei", "351910056265692");
		map.put(Constants.IPPARAM, "221.176.33.67");
		map.put("bsc_cid","");
		map.put("bsc_lac","");
		
		System.out.println(new MmCheckDynamicService().dynamic(map));
	}

}
