package com.jxt.pay.appclient.service.dynamic.net;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * 掌智通-人民视讯
 * 	http://182.92.243.34:8080/qqdm/zhongqing.jsp?imsi=46002001929445621&price=10
 * @author leoliu
 *
 */
public class ZztVideoDynamicService implements IDynamicService{

	private static final String TYPE = "zztVideo";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "imei={imsi}&imsi={imsi}&iccid={iccid}&cpparam={cpparam}&ua={ua}&video_ua={video_ua}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			try{
				String imei = map.get("imei");
				String imsi = map.get("imsi");
				String iccid = map.get("iccid");
				String cpparam = map.get("cpparam");
				String ua = URLEncoder.encode("Huawei_HUAWEI_HUAWEI MT7-TL00","utf-8");
				String video_ua = URLEncoder.encode("Mozilla/5.0 (Linux; Android 4.4.2; HUAWEI MT7-TL00 Build/HuaweiMT7-TL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36","utf-8");
				
				String param = REQUESTMODEL.replace("{imei}",imei).replace("{imsi}",imsi).replace("{iccid}",iccid).replace("{cpparam}",cpparam).replace("{ua}",ua).replace("{video_ua}",video_ua);
				
				String responseJson = GetData.getData(url, param);
				
				xml = parseJson(responseJson);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return xml;
	}
	
	private String parseJson(String responseJson){
		
		if(responseJson != null && responseJson.length() > 50){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String first_num = jo.getString("first_num");
				String first_sms = jo.getString("first_sms");
				
				String second_num = jo.getString("second_num");
				String second_sms = jo.getString("second_sms");
				
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				sms.setSmsDest(first_num);
				sms.setSmsContent(CommonUtil.base64Decode(first_sms));
				sms.setSuccessTimeOut(2);
					
				Sms sms1 = new Sms();
				sms1.setSmsDest(second_num);
				sms1.setSmsContent(CommonUtil.base64Decode(second_sms));
				sms1.setSuccessTimeOut(2);
				
				smsList.add(sms);
				smsList.add(sms1);
				
				return XstreamHelper.toXml(smsList);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://120.26.206.127:8001/sc/video/rmls";//?cmd=6&price=8&g=10&f=18&imei=860174010602000&imsi=460030912121002&phone=13800138000";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "zztVideo");
		map.put("cpparam", "r005E");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		map.put("iccid","898600810115F0387588");
		
		
		System.out.println(new ZztVideoDynamicService().dynamic(map));
	}

}
