package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;

/**
 * 对接南京网游
 * @author leoliu
 *
 */
public class SjdyDmSdkDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(SjdyDmSdkDynamicService.class);
	
	private static final String FIRSTREQUESTMODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><gameId>{gameId}</gameId><imei>{imei}</imei><imsi>{imsi}</imsi><extData>{extData}</extData></request>";
	
	private static final String TYPE = "sjdydmsdk";
		
	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String xml = null;
		
		if(url != null && url.length() > 0){
			String gameId = map.get("gameId");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String extData = map.get("extData");
			
			String script = FIRSTREQUESTMODEL.replace("{gameId}", gameId).replace("{imei}", imei).replace("{imsi}", imsi).replace("{extData}", extData+"");
			
			logger.info("firstDynamic : "+script);
				
			String responseXml = new PostData().PostData(script.getBytes(), url);
				
			xml = parseFirst(responseXml);
			
		}
		
		if(xml == null){
			xml = DynamicUtils.parseError("598");//获取失败
		}
		
		return xml;
	}

	private String parseFirst(String responseXml){
		logger.info("parseFirst : "+responseXml);
		if(responseXml != null && responseXml.length() > 0){
			String state = SingleXmlUtil.getNodeValue(responseXml, "state");
			
			if("0".equals(state)){
				String spNumber = SingleXmlUtil.getNodeValue(responseXml, "spNumber");
				String moContent = SingleXmlUtil.getNodeValue(responseXml, "moContent");
				String smsContent;
				try {
					smsContent = CommonUtil.base64Decode(moContent);
					Sms sms = new Sms();
					sms.setSmsContent(smsContent);
					sms.setSmsDest(spNumber);
					sms.setSuccessTimeOut(1);
					
					return XstreamHelper.toXml(sms).toString();
				} catch (UnsupportedEncodingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				
			}else{
				return DynamicUtils.parseError(state);
			}
		}
		
		return null;
	}

	
	
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public static void main(String[] args){
		test1();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("url","http://121.52.208.188:3001/dmappsdk");
		map.put("type","sjdydmsdk");
		map.put("gameId","14631");
		map.put("imei","867376023133651");
		map.put("imsi","460001123143655");
		map.put("extData","12345");
		
		System.out.println(new SjdyDmSdkDynamicService().dynamic(map));
	}
	
	
}

