package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * @author leoliu
 * 
 */
public class RmkjYkspDynamicService implements IDynamicService {

	private static Logger logger = Logger
			.getLogger(RmkjYkspDynamicService.class);

	private static final String TYPE = "rmkjYksp";// 爱游戏短代码

	private static final String PARAM1 = "ptid={ptid}&itemId={itemId}&itemPrice={itemPrice}&imsi={imsi}&imei={imei}&noid={noid}&ctid={ctid}";
	
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

			String ptid = map.get("ptid");
			String itemId = map.get("itemId");
			String itemPrice = map.get("itemPrice");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String noid = map.get("noid");
			String ctid = map.get("ctid");

			String param = PARAM1.replace("{ptid}", ptid).replace("{itemId}", itemId).replace("{itemPrice}", itemPrice)
							.replace("{imsi}", imsi).replace("{imei}", imei).replace("{noid}", noid).replace("{ctid}", ctid);

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

				String result = jo.getString("result");
				
				if (result.equals("0")) {
					
					String content = jo.getString("sms");
					String port = jo.getString("port");
					
					String[] smsContent = content.split("&");
					String[] smsDest = port.split("&");
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					String mo1 = CommonUtil.base64Decode(smsContent[0]);
					String mo2 = CommonUtil.base64Decode(smsContent[1]);
										
					Sms sms = new Sms();

					sms.setSmsContent(mo1);
					sms.setSmsDest(smsDest[0]);
					sms.setSuccessTimeOut(1);
					
					smsList.add(sms);
					
					
					Sms sms2 = new Sms();

					sms2.setSmsContent(mo2);
					sms2.setSmsDest(smsDest[1]);
					sms2.setSuccessTimeOut(1);
					
					smsList.add(sms2);
					
					
					
					return XstreamHelper.toXml(smsList).toString();
				}
				else{
					return DynamicUtils.parseError(result);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;

	}

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("url", "http://123.57.16.73:8080/bas/video/crackvideo");
		map.put("type", TYPE);
		map.put("ptid", "60004");
		map.put("itemId", "2028595110");
		map.put("itemPrice", "600");
		map.put("imsi", "460078010952058");
		map.put("imei", "867451025555753");
		map.put("noid", "10438566");
		map.put("ctid", "600775887");
		
		logger.info(new RmkjYkspDynamicService().dynamic(map));
	}

}
