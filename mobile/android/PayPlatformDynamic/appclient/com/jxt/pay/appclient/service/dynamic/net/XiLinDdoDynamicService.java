package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class XiLinDdoDynamicService implements IDynamicService{

	private static final String PORT = "1065842232";
	
	private static Logger logger = Logger.getLogger(XiLinDdoDynamicService.class);
	
	@Override
	public String getType() {
		return "xilinddo";
	}

	@Override
	public String dynamic(Map<String, String> map) {
		 
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else{
			return secondDynamic(map);
		}
		
	}
	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String appid = map.get("appid");
		String channelId = map.get("channelId");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String price = map.get("price");
		String cpparam = map.get("cpparam");
		String nyurl = map.get("nyurl");
		
		String url1 = url+"?msgType=GetSmsReq&appid="+appid+"&channelId="+channelId+"&imsi="+imsi+"&imei="+imei+"&price="+price+"&cpparam="+cpparam+"&nyurl="+CommonUtil.base64Encode(nyurl);
		
		String resp = GetData.getData(url1);
		
		logger.info("resp1:"+resp);
		
		if(resp != null && resp.length() > 0){
			try{
				JSONObject jo = new JSONObject(resp);
				
				String result = jo.getString("result");
				
				if("0".equals(result)){
					
					String seqId = jo.getString("seqId");
					Sets sets = new Sets();
					sets.setKey("seqId");
					sets.setValue(seqId);
					
					String msg = jo.getString("sms");
					
					Sms sms0 = new Sms();
					sms0.setSmsDest(PORT);
					sms0.setSmsContent(msg);
					sms0.setSuccessTimeOut(2);
					
					Sms sms1 = new Sms();
					sms1.setSmsDest(PORT);
					sms1.setSmsContent(msg);
					sms1.setSuccessTimeOut(2);
					sms1.setSendType("2");
					
					return XstreamHelper.toXml(sms0).append(XstreamHelper.toXml(sms1)).append(XstreamHelper.toXml(sets)).toString();
					
					
				}else{
					return DynamicUtils.parseError(result);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String seqId = map.get("seqId");
		
		String url1 = url+"?msgType=OrderReq&seqId="+seqId;
		
		String resp = GetData.getData(url1);
		
		logger.info("resp2:"+resp);
		
		if(resp != null && resp.length() > 0){
			try{
				JSONObject jo = new JSONObject(resp);
				
				String result = jo.getString("result");
				
				if("0".equals(result)){
					Sets sets = new Sets();
					sets.setKey("succRet");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(result);
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
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo","1");
		map.put("url","http://123.57.25.92:13888/Cmcc_ddo/Pay");
		map.put("appid","500000000016");
		map.put("channelId","12396000");
		map.put("imsi", "460078010952058");
		map.put("imei", "867451025555753");
		map.put("price","100");
		
		map.put("cpparam",""+System.currentTimeMillis());
		map.put("nyurl","http://www.cyjd1300.com:9020/pay/synch/xilinddo");
		
		XiLinDdoDynamicService service = new XiLinDdoDynamicService();
		
		String xml = service.dynamic(map);
		
		System.out.println(xml);
	}

}
