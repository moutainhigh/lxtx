package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * http://115.28.254.133:8888/cmdmapi/Charge?cpid=838&pid=1637763201&imsi=***&imei=***&ua=Xiaomi_MI&os=4.2.2&ip=127.0.0.1&chid=ziqudaohao&cpparam=zidingyicanshu
 * @author leoliu
 *
 */
public class WillGuoDynamicService implements IDynamicService{

	private static final String TYPE = "willGuo";
	
	private static final String PARAMS = "cpid={cpid}&pid={pid}&imsi={imsi}&imei={imei}&ua=Xiaomi_Xiaomi_MI&os=4.2.2&ip={ip}&chid={chid}&cpparam={cpparam}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String cpid = map.get("cpid");
		String pid = map.get("pid");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String chid = StringUtils.defaultString(map.get("chid"));
		String cpparam = StringUtils.defaultString(map.get("cpparam"));
		String ip = map.get(Constants.IPPARAM);
		
		imsi = fill(imsi,15);
		imei = fill(imei,15);
		
		String params = PARAMS.replace("{cpid}",cpid).replace("{pid}",pid)
				.replace("{imsi}",imsi).replace("{imei}",imei)
				.replace("{chid}",chid).replace("{cpparam}",cpparam).replace("{ip}",ip);
		
		String responseJson = GetData.getData(url, params);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String result = jo.getString("result");
				
				if("0".equals(result)){
					String smsport = jo.getString("smsport");
					String smsContent = CommonUtil.base64Decode(jo.getString("sms"));
					System.out.println(smsContent);
					Sms sms = new Sms();
					sms.setSmsContent(smsContent);
					sms.setSmsDest(smsport);
					sms.setSuccessTimeOut(2);
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError(result);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	static String[] arr = new String[]{"0","1","2","3","4","5","6","7","8","9"};
	
	private static String fill(String s,int len){
		int length = s.length();
		
		if(length < len){
			for(int i = 0 ; i < len - length ; i ++){
				s += arr[new Random().nextInt(arr.length)];
			}
		}
		
		return s;
	}
	
	public static void main(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://115.28.254.133:8888/cmdmapi/Charge");
		map.put("type","willGuo");
		map.put("cpid","854");
		map.put("pid","1687672910");
		map.put("imsi","460001421592091");
		map.put("imei","867451025555753");
		map.put("chid","10B101");
		map.put("cpparam","10B101a01321144314");
		map.put(Constants.IPPARAM,"127.0.0.1");
		
		System.out.println(new WillGuoDynamicService().dynamic(map));
	
//		System.out.println(fill("46000",15));
	}

}
