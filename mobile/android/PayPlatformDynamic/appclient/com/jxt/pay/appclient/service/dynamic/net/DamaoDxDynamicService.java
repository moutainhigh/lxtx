package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class DamaoDxDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(DamaoDxDynamicService.class);
	
	private static final String PARAMS = "fee={fee}&imsi={imsi}&EXDATA={EXDATA}&Orderid={Orderid}&productName=test&chargeName=test";
	
	@Override
	public String getType() {
		return "damaodx";
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String fee = map.get("fee");
		String imsi = map.get("imsi");
		String channelNo = map.get("channelNo");
		String taskId = map.get("taskId");
		
		String params = PARAMS.replace("{fee}", fee).replace("{imsi}", imsi).replace("{EXDATA}",channelNo+taskId).replace("{Orderid}",taskId);
		String url = map.get("url");
		
		String resp = GetData.getData(url,params);
		
		if(resp != null && resp.length() > 0){
			try{
				JSONObject jo = new JSONObject(resp);
				
				String code = jo.getString("code");
				
				if("000".equals(code)){
					String spnumber = jo.getString("spnumber");
					String content = jo.getString("content");
					
					Sms sms = new Sms();
					sms.setSmsDest(spnumber);
					sms.setSmsContent(content);
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError(code);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void main(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","damaodx");
		map.put("fee", "1");
		map.put("imsi", "460031414000744");
		map.put("channelNo", "j21m");
		map.put("taskId", "12314241");
		map.put("url", "http://fred5.top:38080/parsePassCode/telcommunications/tykj/getsmscmd_2");
		
		DamaoDxDynamicService service = new DamaoDxDynamicService();
		String xml = service.dynamic(map);
		
		logger.info("xml:"+xml);
	}
	
}
