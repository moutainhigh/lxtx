package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class JhtcDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(JhtcDynamicService.class);
	
	private static final String TYPE = "jhtc";
	
	private static final String REQUESTPARAMS = "ei={imei}&si={imsi}&cpparam={cpparam}&va={price}&iccid={iccidpro}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {

		String url = map.get("url");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String cpparam = map.get("cpparam");
		String price = map.get("price");
		String iccid = map.get("iccid");
		String channel = map.get("channel");
		
		String iccidpro = "01";
		
		if(iccid != null && iccid.length() >= 10){
			iccidpro = iccid.substring(8,10);
		}
		
		String requestParams = REQUESTPARAMS.replace("{imsi}",imsi).replace("{imei}", imei)
				.replace("{cpparam}",cpparam+channel.substring(3)).replace("{price}", price)
				.replace("{iccidpro}",iccidpro);
		
		String responseJson = GetData.getData(url, requestParams);
		
		String xml = "";
		
		if(responseJson != null && responseJson.length() > 0){
			xml = parse(responseJson);
			
			if(xml == null || xml.length() == 0){
				return DynamicUtils.parseError("598");
			}
		}else{
			return DynamicUtils.parseError("597");
		}
		
		return xml;
	}

	private String parse(String responseJson){
		logger.info("responseJson:"+responseJson);
		try{
			JSONObject jo = new JSONObject(responseJson);
			
			String upportinit = jo.getString("upportinit");
			String msg64init = jo.getString("msg64init");
			msg64init = CommonUtil.base64Decode(msg64init);
			
			String upport = jo.getString("upport");
			String msg64 = jo.getString("msg64");
			msg64 = CommonUtil.base64Decode(msg64);
			
			boolean hasmore = jo.getBoolean("hasmore");
			int nextslot = jo.getInt("nextslot");
			
			if(hasmore == false && nextslot < 0){
				return DynamicUtils.parseError("596");
			}else{
				StringBuffer sb = new StringBuffer();
				
				if(msg64init != null && msg64init.length() > 0){
					Sms sms0 = new Sms();
					sms0.setSmsDest(upportinit);
					sms0.setSmsContent(msg64init);
					sms0.setSuccessTimeOut(2);
				
					sb.append(XstreamHelper.toXml(sms0));
				}
				
				if(msg64 != null && msg64.length() > 0){
					Sms sms1 = new Sms();
					sms1.setSmsDest(upport);
					sms1.setSmsContent(msg64);
					sms1.setSuccessTimeOut(2);
					
					if(sb.length() > 0){
						sb.append("<wait>30</wait>");
					}
					
					sb.append(XstreamHelper.toXml(sms1));
				}
				return sb.toString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] agrs){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","jhtc");
		map.put("url","http://114.215.103.105:8080/normalreqv2");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		map.put("iccid","898600810115F0387588");
		map.put("cpparam","JHF");
		map.put("channel","10B101a101234567");
		map.put("price","200");
		
		String xml = new JhtcDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
	
}
