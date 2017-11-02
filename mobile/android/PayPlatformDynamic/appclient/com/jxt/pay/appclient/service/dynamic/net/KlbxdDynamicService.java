package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 快乐八仙斗
 * @author leoliu
 *
 */
public class KlbxdDynamicService implements IDynamicService{

	private static final String TYPE = "klbxd";
	private static final String MODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><msgType>Req</msgType><imei>{imei}</imei><imsi>{imsi}</imsi><tel>{tel}</tel><linkId>{linkId}</linkId></request> ";
	
	private static final Guard guard1 = new Guard("1065889955", "无法|有误|请核对", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("1065889955","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("1065889955","感谢购买|欢乐八仙斗",2880,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("1065889955","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private int timeOut = 300;
	
	@Override
	public String getType() {
		return TYPE;
	}


	private Map<String, Long> tryMap = new HashMap<String, Long>();

	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		String mobileTaskId = map.get("mobileTaskId");
		
		Calendar cal = Calendar.getInstance();
		Long startTime = tryMap.get(mobileTaskId);
		
		if(startTime == null){
			startTime = cal.getTimeInMillis();
			tryMap.put(mobileTaskId,startTime);
		}else{
			if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
				tryMap.remove(mobileTaskId);
				return DynamicUtils.parseError("599");
			}
		}
		
		if(url != null && url.length() > 0){
			
			String imei = StringUtils.defaultString(map.get("imei"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String mobile = StringUtils.defaultString(map.get("mobile"));
			
			String linkId = StringUtils.defaultString(map.get("linkId"));
			
			String content = MODEL.replace("{imei}", imei).replace("{imsi}", imsi).replace("{tel}",mobile).replace("{linkId}",linkId);
			
			String result = new PostData().PostData(content.getBytes(), url);
			
			if(result != null && result.length() > 0){
				xml = parse(result);
			}
			
			if(xml == null){
				xml = DynamicUtils.parseWait(map);
			}else{
				tryMap.remove(mobileTaskId);
			}
		}else{
			tryMap.remove(mobileTaskId);
			return DynamicUtils.parseError("598");
		}
		
		return xml;
	}
	
	private String parse(String xml){
		if(xml != null && xml.length() > 0){
			String smsStr = SingleXmlUtil.getNodeValue(xml, "sms");
			
			try {
				String result = CommonUtil.base64Decode(smsStr);
			
				if(result != null && result.length() > 0){
					if(result.indexOf("<to>") >= 0){
						String to = SingleXmlUtil.getNodeValue(result, "to");
						String info = SingleXmlUtil.getNodeValue(result, "info");
						
						List<Sms> smsList = new ArrayList<Sms>();
						
						Sms sms = new Sms();
						
						sms.setSmsContent(info);
						sms.setSmsDest(to);
						sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
						
						smsList.add(sms);
						
						Sms guardSms = new Sms();
						
						List<Guard> guardList = new ArrayList<Guard>();
						
						guardList.add(guard1);
						guardList.add(guard2);
								
						guardList.add(guard6);
						guardList.add(guard3);
						guardList.add(guard4);
						guardList.add(guard5);
						
						guardSms.setGuardList(guardList);
						
						smsList.add(0, guardSms);
							
						return XstreamHelper.toXml(smsList);
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	private String fillImei(String imei){
		if(imei.length() < 15){
			int len = imei.length();
			
			for(int i = 0 ; i < 15 - len ; i ++){
				imei += new Random().nextInt(10);
			}
		}
		
		return imei;
	}
	
	public static void main(String[] args){
//		url=http://112.25.15.148:8080/forwardProj/sms/klbxd0200b_MTAwMDAz&type=klbxd&linkId=lkbxd0200b&mobile=15816609429&mobileId=2088066&imei=860874020589609&imsi=460028166050302&mobileTaskId=5120454
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://112.25.15.148:8080/forwardProj/sms/klbxd0200a_MTAwMDAz");
		map.put("imei", "869460011612203");
		map.put("imsi", "460001153105779");
		map.put("linkId", "klbxd0200a");
		map.put("mobileTaskId", "5120454");
		map.put("mobile", "13811155779");
		
		String xml = new KlbxdDynamicService().dynamic(map);
		
		System.out.println(xml);
	}
}
