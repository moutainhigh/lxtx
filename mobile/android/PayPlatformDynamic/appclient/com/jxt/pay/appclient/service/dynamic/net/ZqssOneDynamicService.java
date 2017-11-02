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

/**
 * @author leoliu
 *
 */
public class ZqssOneDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZqssOneDynamicService.class);
	
	private static final String TYPE = "zqssOne";//中青盛世第一次合作代码
	
	private static final String PARAM1 = "cid={cid}&bid={bid}&imsi={imsi}&imei={imei}&iccid={iccid}";
	
	private static final String RESULTCODE_SUCC = "0000";
	
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		
	
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			
			String cid = map.get("cid");
			String bid = map.get("bid");
			String imsi = map.get("imsi");		
			String imei = map.get("imei");
			String iccid = map.get("iccid");
			
			
			String param = PARAM1.replace("{cid}", cid).replace("{bid}", bid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{iccid}",iccid);
			
			
			
			String responseJson = GetData.getData(url,param);
//			String responseJson = new PostData().PostData(param.getBytes(), url);
			logger.info("-----"+responseJson);
			xml = parseFirst(map,responseJson);

			if(xml == null){
				xml = DynamicUtils.parseError("598");//获取失败	
			}
		}
		return xml;
	}
	
	private String parseFirst(Map<String, String> map,String responseJson){
		logger.info("parseFirst : "+responseJson);
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("retCode")){
					String retCode = jo.getString("retCode");
					
					logger.info("parse first result code : "+retCode);
					
					if(RESULTCODE_SUCC.equals(retCode)){
						
							
						if(jo.has("smscount")){
													
							String mo = jo.getString("mo");
							String mo2 = jo.getString("mo2");

							String Called = jo.getString("called");
							String Called2 = jo.getString("called2");
							
							//List<Sms> smsList = new ArrayList<Sms>();
							
							Sms sms = new Sms();
							
							sms.setSmsContent(mo);
							sms.setSmsDest(Called);
							sms.setSuccessTimeOut(2);
							//smsList.add(sms);
							
							Sms sms1 = new Sms();
								
							sms1.setSmsContent(mo2);
							sms1.setSmsDest(Called2);
							sms1.setSuccessTimeOut(2);	
							return XstreamHelper.toXml(sms).toString()+"<wait>5</wait>"+XstreamHelper.toXml(sms1).toString();							
							
						}
												
							String mo = jo.getString("mo");
							String Called = jo.getString("called");						
							Sms sms = new Sms();
							
							sms.setSmsContent(mo);
							sms.setSmsDest(Called);
							sms.setSuccessTimeOut(2);

							return XstreamHelper.toXml(sms).toString();
														
						
					}else{
						return DynamicUtils.parseError(retCode);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	public static void main(String[] args){

		Map<String, String> map = new HashMap<String, String>();		
//		map.put("theNo", "1");
		map.put("url", "http://120.27.43.42:8008/bdms/orderump");
		map.put("type",TYPE);
		map.put("cid","lwta");
		map.put("bid","4");
		map.put("imsi","460078010952058");
		map.put("imei", "867451025555753");
		map.put("iccid", "898600710115f0181494");
		logger.info(new ZqssOneDynamicService().dynamic(map));
	}

	
}
