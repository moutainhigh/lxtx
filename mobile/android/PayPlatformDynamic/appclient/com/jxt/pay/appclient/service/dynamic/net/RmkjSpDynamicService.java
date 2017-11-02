package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class RmkjSpDynamicService implements IDynamicService{

	private static final String TYPE = "rmkjSp";
	
	private static final String PARAMS = "paykey={paykey}&imsi={imsi}&cpid={cpid}&cp_param={cp_param}";
	
	@Override
	public String getType() {
		 return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String paykey = map.get("paykey");
		String imsi = map.get("imsi");
		String cpid = map.get("cpid");
		String cp_param = map.get("cp_param");
		String theNo = map.get("theNo");
		
		String params = PARAMS.replace("{paykey}",paykey ).replace("{imsi}", imsi).replace("{cpid}",cpid ).replace("{cp_param}", cp_param);
		
		String responseJson = GetData.getData(url,params);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String serviceno = jo.getString("serviceno");
				String smsContent = jo.getString("sms");
				
				long id = jo.getLong("id");
				String reportUrl = jo.getString("reportUrl");
				
				if(serviceno != null && serviceno.length() > 0){
					if("2".equals(theNo)){
						smsContent = CommonUtil.base64Decode(smsContent);
					}
					
					Sms sms = new Sms();
					sms.setSmsContent(smsContent);
					sms.setSmsDest(serviceno);
					sms.setSuccessTimeOut(2);
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError("597");
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		test2();
	}
	
	private static void test2(){
		//http://g.biedese.cn/vb/req_sms_base64?paykey=301400430000000-10154262-618224757&imsi=460022089262849&cpid=218&cp_param=13G40Ga125172937
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://g.biedese.cn/vb/req_sms_base64");
		map.put("paykey","301400430000000-10154262-618224757");
		map.put("imsi", "460022089262849");
		map.put("cpid","218");
		map.put("cp_param","13G40Ga125172937");
		map.put("theNo", "1");
		map.put("type", "rmkjSp");
		
		System.out.println(new RmkjSpDynamicService().dynamic(map));
		
	}
	
	private static void test0(){
		String ss = "MDAwMDAwMDA4NlEzMzMvJTY1MzE3OTY0ZjAwYzQwMTdFQjh1NzJmSnFFQy9nWnlOR2dpO1d1Z2UrZzJnPT0ybT9ZNTdxdDFDNDI2MGswMjApUm0xMjlkNTQ5NDd3TTAwMHwwSDAwMDAwV0lxbTVGLVtadXYjamJaMHU+Z1JvSWdjR1pLPg==";
//
//			   ss = "MDAwMDAwMDA4NlEzMzMvJTY1MzE3OTY0ZjAwYzQwMTdFQjh1NzJ0a3FCOVMy bFRMWEVJMk9oRjd0a2F3PT0ybjhdMjZ+MjBLNDE3MG84MjApUm0xMjlkNTU2 MDN3TTAwMHwwSDAwMDAwS2d1TzZpfGYySWZsbFdMOXNSWDZIMEFYZXplPg==";
		try {
			System.out.println(CommonUtil.base64Decode(ss));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
