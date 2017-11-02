package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class HhkjYxjdbyDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjYxjdbyDynamicService.class);
	
	private static final String TYPE = "hhkjYxjdby";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "http://112.74.13.159:8088/sync/NTL35253/requestData?channelNo=6D60VG3A&imsi={imsi}&imei={imei}&fee=1000&channelOrderId={orderId}&province={province}&ext=XT1001";
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private int timeOut = 60;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		
		{
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
			
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String province = map.get("province");
			
			if(province != null && province.length() > 0){
				province = proMap.get(province);
			}
			
			String param = REQUESTMODEL.replace("{imei}",imei).replace("{imsi}",imsi).replace("{province}",province).replace("{orderId}",channel);
			
			String responseJson = GetData.getData(param);
			
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
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info("responseJson:"+responseJson);
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				int state = jo.getInt("state");
				
				if(state == 0){
					JSONObject data = jo.getJSONObject("data");
					
					String smsport = data.getString("smsport");
					String smsmsg = data.getString("smsmsg");
					
					Sms sms = new Sms();
					
					sms.setSmsContent(smsmsg);
					sms.setSmsDest(smsport);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
							
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError("statError");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static Map<String, String> proMap = new HashMap<String, String>();
	
	/**
	 * 01 安徽    02 北京  03 福建  04 甘肃  05 广东  06 广西  07 贵州  08 海南  09 河北  10 河南  11 黑龙江		
		12 湖北  13 湖南 14 吉林 15 江苏 16 江西 17 辽宁 18 内蒙古 19 宁夏 20 青海 21 山东 22 山西
		23 陕西 24 上海 25 四川 26 天津 27 西藏 28 新疆 29 云南 30 浙江 31 重庆
	 * 
	 */
	
	static{
		proMap.put("8611", "1");
		proMap.put("8612", "2");
		proMap.put("8613", "3");
		proMap.put("8614", "4");
		proMap.put("8615", "5");
		proMap.put("8621", "6");
		proMap.put("8622", "7");
		proMap.put("8623", "8");
		proMap.put("8631", "9");
		proMap.put("8632", "10");
		proMap.put("8633", "11");
		proMap.put("8634", "12");
		proMap.put("8635", "13");
		proMap.put("8636", "14");
		proMap.put("8637", "15");
		proMap.put("8641", "16");
		proMap.put("8642", "17");
		proMap.put("8643", "18");
		proMap.put("8644", "19");
		proMap.put("8645", "20");
		proMap.put("8646", "21");
		proMap.put("8650", "22");
		proMap.put("8651", "23");
		proMap.put("8652", "24");
		proMap.put("8653", "25");
		proMap.put("8654", "26");
		proMap.put("8661", "27");
		proMap.put("8662", "28");
		proMap.put("8663", "29");
		proMap.put("8664", "30");
		proMap.put("8665", "31");
	}
	
	
	
	
	public static void main(String[] args){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("channel", "10B101a87654321");
		map.put("imei", "862949029214504");
		map.put("imsi", "460022101441340");
		map.put("province", "8611");
		
		System.out.println(new HhkjYxjdbyDynamicService().dynamic(map));
	}

}
