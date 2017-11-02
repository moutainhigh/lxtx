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
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.XmlUtils;

/**
 * @author leoliu
 *
 */
public class ZztFzzxDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZztFzzxDynamicService.class);
	
	private static final String TYPE = "zztFzzx";//掌智通法制在线
	
	private static final String PARAM1 = "cpid={cpid}&imsi={imsi}&imei={imei}&fee={fee}&ext={ext}&ip={ip}";
	
	private static final String RESULTCODE_SUCC = "0";
	
	
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
			
			
			String cpid = map.get("cpid");
			String imsi = map.get("imsi");		
			String imei = map.get("imei");
			String fee = map.get("fee");
			String ext = map.get("ext");
			String ip = map.get(Constants.IPPARAM);
			
			String param = PARAM1.replace("{cpid}", cpid).replace("{fee}", fee)
					.replace("{imsi}",imsi).replace("{imei}",imei).replace("{ext}",ext).replace("{ip}",ip);
			
			
			
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
				
				if(jo.has("error")){
					String error = jo.getString("error");
					
					logger.info("parse first result code : "+error);
					
					if(RESULTCODE_SUCC.equals(error)){
						
							
													
							String mo = filterNodeValue(jo.getString("content1"));
							String mo2 = filterNodeValue(jo.getString("content2"));
							mo= CommonUtil.base64Decode(mo);
							mo2= CommonUtil.base64Decode(mo2);
							
							
							String Called = jo.getString("num1");
							String Called2 = jo.getString("num2");
							
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
							
							return XstreamHelper.toXml(smsList).toString();							
							
						
														
						
					}else{
						return DynamicUtils.parseError(error);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	private String filterNodeValue(String nodeValue){
		if(nodeValue == null || nodeValue.length() == 0){
			return "";
		}
		
		return nodeValue.replace("<![CDATA[","").replace("]]>","");
	}
	
	public static void main(String[] args){

		Map<String, String> map = new HashMap<String, String>();		
//		map.put("theNo", "1");
		map.put("url", "http://58.64.143.245:8888/other/zwvideo.do");
		map.put("type",TYPE);
		map.put("cpid","28b3ba50");
		map.put("imsi","460022475975452");
		map.put("imei","867451025555753");
		map.put("fee", "1000");
		map.put("ext", "FZ6");
		map.put(Constants.IPPARAM, "114.240.84.209");
		logger.info(new ZztFzzxDynamicService().dynamic(map));
	}

	
}
