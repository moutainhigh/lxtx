package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;

/**
 * @author leoliu
 *
 */
public class CdYyDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(CdYyDynamicService.class);
	
	private static final String TYPE = "CdYy";//成都贝乐咪咕音乐
	
	private static final String PARAM1 = "action={action}&confin={confin}&cpid={cpid}&imsi={imsi}&imei={imei}&serviceid={serviceid}&mode={mode}";
	
	private static final String THENO_1 = "1";
	
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		
//		String theNo = map.get("theNo");
		
//		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
//		}		
		
//		return null;
	}
	
	public static String getDate(SimpleDateFormat formatter) {
		 return formatter.format(new Date());
	}

	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			
			String action = map.get("action");
			String confin = map.get("confin");
			String cpid = map.get("cpid");
			String imsi = map.get("imsi");		
			String imei = map.get("imei");
			String serviceid = map.get("serviceid");
			String mode = map.get("mode");
			
			
			String param = PARAM1.replace("{action}", action).replace("{confin}", confin).replace("{cpid}", cpid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{serviceid}",serviceid).replace("{mode}",mode);
			
			
			
			String responseXml = GetData.getData(url,param);
//			String responseJson = new PostData().PostData(param.getBytes(), url);
			logger.info("-----"+responseXml);
			xml = parseFirst(map,responseXml);

			if(xml == null){
				xml = DynamicUtils.parseError("598");//获取失败	
			}
		}
		return xml;
	}
	
	private String parseFirst(Map<String, String> map,String responseXml){
		if(responseXml != null && responseXml.length() > 0){
			String return_code = SingleXmlUtil.getNodeValue(responseXml, "return_code");
			
			
			if( "0".equals(return_code)){
				
				String sms_msg = SingleXmlUtil.getNodeValue(responseXml, "sms_msg");
				String sms_add = SingleXmlUtil.getNodeValue(responseXml, "sms_add");
				
				String smsContent;
				try {
					smsContent = CommonUtil.base64Decode(sms_msg);
					
					
					Sms sms = new Sms();
					sms.setSmsDest(sms_add);
					sms.setSmsContent(smsContent);
					sms.setSuccessTimeOut(2);
					
					
					return XstreamHelper.toXml(sms).toString();
				} catch (UnsupportedEncodingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				
				
			}else{
				return DynamicUtils.parseError(return_code);
			}
		}
		
		return null;
	}
	
	
	public static void main(String[] args){
		test1();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();		
//		map.put("theNo", "1");
		map.put("url", "http://182.92.204.31:8080/migu_12t/proc");
		map.put("type",TYPE);
		map.put("action","sms_t2");
		map.put("confin","2");
		map.put("cpid", "1005");
		map.put("imsi","867451025555753");
		map.put("imei", "867451025555753");
		map.put("serviceid","10");
		map.put("mode","1");
		
		logger.info(new CdYyDynamicService().dynamic(map));
	}

	
}
