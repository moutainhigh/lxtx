package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * @author leoliu
 * 
 */
public class ZztZtspDynamicService implements IDynamicService {

	private static Logger logger = Logger
			.getLogger(ZztZtspDynamicService.class);

	private static final String TYPE = "zztZtsp";// 掌智通中投视频

	private static final String PARAM1 = "MobileType={MobileType}&Fee={Fee}&MchId={MchId}&IMSI={IMSI}&IMEI={IMEI}&CPParameter={CPParameter}&ClientIP={ClientIP}&ua={ua}&video_ua={video_ua}&sdcidsd={sdcidsd}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	public static String getDate(SimpleDateFormat formatter) {
		return formatter.format(new Date());
	}

	@Override
	public String dynamic(Map<String, String> map) {

		String url = map.get("url");

		String xml = null;

		if (url != null && url.length() > 0) {

			String MobileType = map.get("MobileType");
			String Fee = map.get("Fee");
			String MchId = map.get("MchId");
			String IMSI  = map.get("IMSI");
			String IMEI  = map.get("IMEI");
			String CPParameter = map.get("CPParameter");
			String ClientIP = map.get(Constants.IPPARAM);
			String ua = map.get("ua");
			String video_ua = map.get("video_ua");
			String sdcidsd = map.get("sdcidsd");

			String param = PARAM1.replace("{MobileType}", MobileType).replace("{Fee}", Fee).replace("{MchId}", MchId)
							.replace("{IMSI}", IMSI).replace("{IMEI}", IMEI).replace("{CPParameter}", CPParameter).replace("{ClientIP}", ClientIP)
							.replace("{ua}", ua).replace("{video_ua}", video_ua).replace("{sdcidsd}", sdcidsd);
			logger.info("responseJson:" + param);
			String responseJson = GetData.getData(url,param);
			logger.info("responseJson:" + responseJson);
			xml = parse(responseJson);

			if (xml == null) {
				xml = DynamicUtils.parseError("598");// 获取失败
			}
		}
		return xml;
	}

	private String parse(String responseJson) {

		if (responseJson != null && responseJson.length() > 1) {
			try {
				int pos = responseJson.indexOf("{");

				if (pos > 0) {
					responseJson = responseJson.substring(pos);
				}

				JSONObject jo = new JSONObject(responseJson);
				
				

				String ResultCode = jo.getString("ResultCode");
				
				if (ResultCode.equals("000")) {
					
					
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					String MoSms = jo.getString("MoSms");
					
					JSONArray text = new JSONArray(MoSms);
					//第一条短信
					JSONObject one = text.optJSONObject(0);
					
					if (one != null && one.length() > 1) {
					
						//JSONObject oneMoSms = new JSONObject(one);
						
						String oneMoSmsContent = one.getString("MoSmsMsg");
						String oneMoSmsDest = one.getString("PayChannel");
						
						Sms sms = new Sms();
	
						sms.setSmsContent(oneMoSmsContent);
						sms.setSmsDest(oneMoSmsDest);
						sms.setSuccessTimeOut(1);
						
						smsList.add(sms);
					
					
					}
					//第二条短信
					JSONObject two = text.optJSONObject(1);
					
					
					if (two != null && two.length() > 1) {
						
//						JSONObject twoMoSms = new JSONObject(two);
						
						String twoMoSmsContent = two.getString("MoSmsMsg");
						String twoMoSmsDest = two.getString("PayChannel");
								
						Sms sms2 = new Sms();
	
						sms2.setSmsContent(twoMoSmsContent);
						sms2.setSmsDest(twoMoSmsDest);
						sms2.setSuccessTimeOut(1);
						
						smsList.add(sms2);
					
					}
					
					return XstreamHelper.toXml(smsList).toString();
				}
				else{
					return DynamicUtils.parseError(ResultCode);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;

	}

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("url", "http://bysms.5151pay.com/GetDYSms.ashx");
		map.put("type", TYPE);
		map.put("MobileType", "YD");
		map.put("Fee", "20");
		map.put("CPParameter", "ZT08111111");
		map.put("MchId", "BSEBFD");
		map.put("IMSI", "460021458871803");
		map.put("IMEI", "867451025555753");
		map.put(Constants.IPPARAM,"221.221.234.2");
		map.put("ua", "");
		map.put("video_ua", "");
		map.put("sdcidsd", "");
		
		logger.info(new ZztZtspDynamicService().dynamic(map));
	}

}
