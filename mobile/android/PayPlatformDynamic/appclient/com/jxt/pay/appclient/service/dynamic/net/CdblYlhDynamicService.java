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
public class CdblYlhDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(CdblYlhDynamicService.class);
	
	private static final String TYPE = "cdblYlh";//成都贝乐娱乐汇
	
	private static final String PARAM1 = "cpid={cpid}&pid={pid}&imsi={imsi}&imei={imei}&ua={ua}";
	

	
	
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
			
			
			String pid = map.get("pid");
			String cpid = map.get("cpid");
			String imsi = map.get("imsi");		
			String imei = map.get("imei");
			String ua ="Xiaomi_Xiaomi_MI";
			
			
			String param = PARAM1.replace("{cpid}", cpid).replace("{pid}", pid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{ua}",ua);
			
			
			
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
				
				if(jo.has("result")){
					String retCode = jo.getString("result");
					
					logger.info("parse first result code : "+retCode);
					
					if("0".equals(retCode)){
						
							
							String mo = jo.getString("sms1");
							String mo2 = jo.getString("sms2");
							
							mo = CommonUtil.base64Decode(mo);
							mo2 = CommonUtil.base64Decode(mo2);
							String Called = "1065843601";
							String Called2 = "1065842230";
							
							List<Sms> smsList = new ArrayList<Sms>();
							
							Sms sms = new Sms();
							
							sms.setSmsContent(mo);
							sms.setSmsDest(Called);
							sms.setSuccessTimeOut(2);
							smsList.add(sms);
							
							Sms sms1 = new Sms();
								
							sms1.setSmsContent(mo2);
							sms1.setSmsDest(Called2);
							sms1.setSuccessTimeOut(2);	
							smsList.add(sms1);
							return XstreamHelper.toXml(smsList);
														
						
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
		map.put("url", "http://139.129.198.53:8888/migumusic/Music");
		map.put("type",TYPE);
		map.put("cpid","116");
		map.put("pid","1002883203");
		map.put("imsi","460078010952058");
		map.put("imei", "867451025555753");
		logger.info(new CdblYlhDynamicService().dynamic(map));
	}

	
}
