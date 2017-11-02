package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.XmlStreamReader;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 
 * 	http://122.114.52.108:8001/cm4.asp
 * @author leoliu
 *
 */
public class ZzfApiDynamicService implements IDynamicService{

	private static final String TYPE = "zzfApi";
	private static Logger logger = Logger.getLogger(ZzfApiDynamicService.class);
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	private static final String PARAMS = "imsi={imsi}&imei={imei}&fee={fee}&type={type}&channelid={channelid}&cpparam={cpparam}&ip={ip}&bsc_lac={bsc_lac}&bsc_cid={bsc_cid}";
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			String channelid = map.get("channelid");
			String opType = map.get("opType");
			String fee = map.get("fee");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String ip = map.get(Constants.IPPARAM);
			
			String bsc_cid = StringUtils.defaultString(map.get("bsc_cid"));
			String bsc_lac = StringUtils.defaultString(map.get("bsc_lac"));
			
			String params = PARAMS.replace("{imsi}", imsi).replace("{imei}", imei).replace("{fee}",fee).replace("{channelid}",channelid).replace("{cpparam}",channel).replace("{ip}",ip).replace("{type}", opType);
			params = params.replace("{bsc_lac}", bsc_lac).replace("{bsc_cid}",bsc_cid);
			
			String responseJson = GetData.getData(url,params);
			
			xml = parse(responseJson,map);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseJson,Map<String,String> map){
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info("responseJson:"+responseJson);
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String resultCode = jo.getString("resultCode");
				
				if("0".equals(resultCode)){
					Sms sms = new Sms();
					sms.setSmsDest(jo.getString("accessNo"));
					String content = jo.getString("sms");
					
					System.out.println("content:"+content);
					
					if("15".equals(map.get("opType"))){
						sms.setSendType("3");
					}
					
					sms.setSmsContent(content);
					sms.setSuccessTimeOut(2);
					
					return XstreamHelper.toXml(sms).toString();
				}else if("1".equals(resultCode)){
					Sets sets = new Sets();
					sets.setKey("succ_");
					sets.setValue("1");
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(resultCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://112.74.111.56:9039/gamesit/puburl";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "201");
		map.put("channelid", "293");
		map.put("imsi", "460078010952058");
		map.put("imei", "867451025555753");
		map.put(Constants.IPPARAM, "221.220.249.176");
		map.put("fee", "1000");
		map.put("opType", "15");
		map.put("bsc_cid", "55272");
		map.put("bsc_lac","28730");
		
		System.out.println(new ZzfApiDynamicService().dynamic(map));
	}

}
