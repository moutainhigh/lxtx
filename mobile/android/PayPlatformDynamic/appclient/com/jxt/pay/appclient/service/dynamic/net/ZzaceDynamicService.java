package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * http://61.160.251.130:5151/interface/309?cpparam=ZZACE&imsi=xxxx&imei=xxxx&iccid=xxx&mobile=xxx&ip=127.0.0.1
 * @author leoliu
 *
 */
public class ZzaceDynamicService implements IDynamicService{

	private static final String PARAMS = "cpparam={cpparam}&imsi={imsi}&imei={imei}&iccid={iccid}&mobile=13811155779&ip={ip}";
	private static Logger logger = Logger.getLogger(ZzaceDynamicService.class);
	
	@Override
	public String getType() {
		return "zzace";
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			
			String imei = StringUtils.defaultString(map.get("imei"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String iccid = StringUtils.defaultString(map.get("iccid"));
			String ip = map.get(Constants.IPPARAM);
			String cpparam = map.get("cpparam");
			
			String params = PARAMS.replace("{imsi}", imsi).replace("{imei}",imei).replace("{iccid}",iccid).replace("{ip}", ip).replace("{cpparam}",cpparam);
			
			String responseJson = GetData.getData(url, params);
			
			if(responseJson != null && responseJson.length() > 0){
				logger.info("responseJson:"+responseJson);
				
				try{
					JSONObject jo = new JSONObject(responseJson);
					
					String ResultCode = jo.getString("ResultCode");
					
					if("000".equals(ResultCode)){
						JSONArray ja = jo.getJSONArray("MoSms");
						
						List<Sms> smsList = new ArrayList<Sms>();
						
						for(int i = 0 ; i < ja.length() ; i ++){
							JSONObject jo1 = ja.getJSONObject(i);
							
							String MoSmsMsg = jo1.getString("MoSmsMsg");
							String PayChannel = jo1.getString("PayChannel");
							
							Sms sms = new Sms();
							sms.setSmsContent(MoSmsMsg);
							sms.setSmsDest(PayChannel);
							sms.setSuccessTimeOut(2);
							
							smsList.add(sms);
						}
						
						return XstreamHelper.toXml(smsList);
						
					}else{
						return DynamicUtils.parseError(""+ResultCode);
					}
					
				}catch(Exception e){
				}
				
			}
		}
		
		
		return null;
	}

	public static void main(String[] agrs){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://61.160.251.130:5151/interface/309");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		map.put("iccid","898600810115F0387588");
		map.put(Constants.IPPARAM,"221.221.233.175");
		map.put("type", "zzace");
		map.put("cpparam","ZZACD");
		
		System.out.println(new ZzaceDynamicService().dynamic(map));
	}
	
	
}
