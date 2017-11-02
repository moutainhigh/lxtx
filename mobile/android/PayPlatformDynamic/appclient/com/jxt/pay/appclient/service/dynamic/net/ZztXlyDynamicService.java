package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.appclient.utils.XmlUtils;

/**
 * GET访问： http://cc.channel.3gshow.cn/common/req.ashx?imsi=460000000000000&imei=86000000000000&mb=13800000000&cid=151&orderId=1234&pid=1113&payCodeID=5255&responseType=xml
得到返回结果：
<?xml version="1.0"?>
<Reponse>
<Status>1000</Status>
<Pay>
<SMS>
<Num>1065800810155979</Num>
<Content>020#151_1234_35#20141009#95979693424</Content>
</SMS>
</Pay>
</Reponse>
 * @author leoliu
 *
 */
public class ZztXlyDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(ZztXlyDynamicService.class);
	
	private static final String TYPE = "zztXly";
	private static final String URLPARAM = "imsi={imsi}&imei={imei}&mb=13800000000&cid={cid}&orderId={orderId}&pid={pid}&payCodeID={payCodeID}&responseType=xml&ip={ip}";
	private int timeOut = 60;
	
	private static final Guard guard1 = new Guard("10658800", "无法|有误|请核对", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658800","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("10658800","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
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
			String orderId = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(orderId);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(orderId,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(orderId);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			
			if(imsi.length() == 0){
				imsi = "46000115310";
			}
			
			imsi = fillImei(imsi);
			
			String param = URLPARAM.replace("{imsi}", imsi);
			
			String imei = StringUtils.defaultString(map.get("imei"));
			
			imei = imei.replaceAll("[a-zA-Z]","1");
			
			imei = fillImei(imei);
			
			param = param.replace("{imei}", imei);
			
			String len = map.get("length");
			
			if(len != null && len.length() > 0){
				orderId = orderId.substring(orderId.length() - Integer.parseInt(len));
			}
			
			String ip = map.get(Constants.IPPARAM);
			
			param = param.replace("{orderId}", orderId);
			param = param.replace("{cid}", StringUtils.defaultString(map.get("cid")));
			param = param.replace("{pid}", StringUtils.defaultString(map.get("pid")));
			param = param.replace("{payCodeID}", StringUtils.defaultString(map.get("payCodeID")));
			param = param.replace("{ip}", ip);
			
			String responseXml = GetData.getData(url+"?"+param);
//	System.out.println(responseXml);		
			if(responseXml != null && responseXml.length() > 0){
				xml = parseXml(map,responseXml);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parseXml(Map<String, String> map,String xml){
		
		try {
			String status = XmlUtils.getNodeValue(xml, "Status");
			
			if("1000".equals(status)){
				List<String> arr = XmlUtils.getNodeValues(xml, "SMS");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				for(String s : arr){
					Sms sms = new Sms();
					
					String content = XmlUtils.getNodeValue(s, "Content");
					
					content = content.replace("&lt;","<").replace("&gt;", ">");
					
					sms.setSmsContent(content);
					sms.setSmsDest(XmlUtils.getNodeValue(s, "Num"));
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
				}
				
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
				tryMap.remove(map.get("orderId"));
				
				return XstreamHelper.toXml(smsList);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tryMap.remove(map.get("orderId"));
		return DynamicUtils.parseError("597");
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
		
//		test();
		
//		http://cc.channel.3gshow.cn/common/req.ashx&type=zztXly&imsi=460026620811823&imei=354026050655833&mobileId=9872554&channel=100101a049397252&cid=151&pid=1113&payCodeId=5255
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://cc.channel.3gshow.cn/common/req.ashx");
		map.put("imei","869460011612203");
		map.put("imsi","460028174282753");
		map.put("channel","1773263599465");
		map.put("cid","151");
		map.put("pid","1604");
		map.put("payCodeID","7263");
		map.put(Constants.IPPARAM, "115.28.52.64");
		
		String xml = new ZztXlyDynamicService().dynamic(map);
		
		System.out.println(xml);

//		String orderId = "10B601a087654321";
//		System.out.println(orderId.substring(orderId.length() - 7));
	}
	
	private static void test(){
		String a = "::pHRp[z:f2V![[::2j'WCH:j+g:v[|To)&gt;&gt;$::::9:eP::2jj3k::v:U3[m[::\6:HpW3cYIfmDAo$M11-1:l6Xc[2=::pW[Vp}]1'qCTzEzEX0n}Eg@2oWJ%u&lt;CCk:";
		
		try {
			System.out.println(a.replace("&lt;","<").replace("&gt;", ">"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
