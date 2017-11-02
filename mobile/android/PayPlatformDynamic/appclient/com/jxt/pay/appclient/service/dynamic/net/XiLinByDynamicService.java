package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.qlzf.commons.helper.MD5Encrypt;

public class XiLinByDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(XiLinByDynamicService.class);
	private static final String PARAMS = "MsgType=GetSmsReq&Msisdn={mobile}&Passwd=111111&PayCode={payCode}&ExData={exData}&ChannelId={channelId}&Sign={sign}";
	
	@Override
	public String getType() {
		return "xilinby";
	}

	@Override
	public String dynamic(Map<String, String> map) {
		String url = map.get("url");
		String channel = map.get("channel");
		String mobile = map.get("mobile");
		String payCode = map.get("payCode");
		String appId = map.get("appId");
		String cp = map.get("cp");
		String channelId = map.get("channelId");
		String key = map.get("key");
		
		String exData = appId+cp+channel;
		String md5 = key+payCode+exData+appId+channelId;
		String sign = MD5Encrypt.MD5Encode(md5).toUpperCase();
		
		logger.info("before md5:"+md5);
		logger.info("after md5:"+sign);
		
		String params = PARAMS.replace("{mobile}",mobile).replace("{payCode}",payCode).replace("{exData}",exData).replace("{channelId}",channelId).replace("{sign}",sign);
		
		String resp = GetData.getData(url, params);
		
		logger.info("first resp:"+resp);
		
		if(resp != null && resp.length() > 0){
			try{
				JSONObject jo = new JSONObject(resp);
				String result = jo.getString("result");
				
				if(result.startsWith("http")){
					Sets sets = new Sets();
					sets.setKey("smsCodeUrl");
					sets.setValue(result);
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(result);
				}
			}catch(Exception e){
				
			}
		}
		
		return null;
	}

	public static void main(String[] args){
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("type","xilinby");
		map.put("url","http://117.25.133.11:13888/Cmcc_Monthly/by_pay");
		map.put("channel","test"+System.currentTimeMillis());
		map.put("mobile","13811155779");
		map.put("payCode","300008007001");
		map.put("appId","300000008007");
		map.put("cp","2156");
		map.put("channelId","700000009");
		map.put("key","3)*cp8j2156");
		
		XiLinByDynamicService service = new XiLinByDynamicService();
		String ret = service.dynamic(map);
		
		logger.info("ret:"+ret);
	}
}
