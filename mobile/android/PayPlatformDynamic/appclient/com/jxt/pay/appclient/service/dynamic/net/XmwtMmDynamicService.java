package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * B,短信接口地址2: http://211.154.152.59:8080/sdk/getcon
  请求方式:GET
  请求数据格式标准GET格式，例如：
     amount=800&packet_id=405224001&imei=499000316129545&imsi=460029999154959 

	    说明:
	       amount: 使用计费点金额，单位分
		   imsi: 用户识别码imsi(如获取不到,则自定义15位46000开头的数字).必须
		   imei: 移动设备国际身份码imei(如获取不到,则自定义15位数字).必须
		  packet_id: 渠道号

	
返回成功数据格式同上。
 * @author leoliu
 *
 */
public class XmwtMmDynamicService implements IDynamicService{

	private static final String TYPE = "xmwtMm";

	private static final String REQUESTMODEL = "amount={amount}&packet_id={packet_id}&imei={imei}&imsi={imsi}";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	private static final Guard guard1 = new Guard("10658800","成功购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
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
			String amount = map.get("amount");
			String packet_id = map.get("packet_id");
			
			if(imsi == null || imsi.length() == 0){
				imsi = fillImei("46000");
			}
			
			if(imei == null || imei.length() == 0){
				imei = fillImei("");
			}
			
			String requestParam = REQUESTMODEL.replace("{imei}", imei).replace("{imsi}",imsi).replace("{amount}",amount).replace("{packet_id}",packet_id);
			
			String responseJson = GetData.getData(url,requestParam);
			
			xml = parseXml(responseJson);
			
			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseXml(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String num = jo.getString("num");
				String content = jo.getString("content");
				
				if(num != null && num.length() > 0 && content != null && content.length() > 0){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(content);
					sms.setSmsDest(num);
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
		
		String url = "http://211.154.152.59:8080/sdk/getcon";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B9KTa100546110");
		map.put("amount", "800");
		map.put("packet_id", "2141");
		map.put("imsi","460029647996597");
		map.put("imei","861032021390142");
		
		System.out.println(new XmwtMmDynamicService().dynamic(map));
		
		
		
	}
}
