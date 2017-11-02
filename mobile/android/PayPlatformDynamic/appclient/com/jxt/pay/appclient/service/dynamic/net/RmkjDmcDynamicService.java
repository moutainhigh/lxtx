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
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * @author leoliu
 * 
 */
public class RmkjDmcDynamicService implements IDynamicService {

	private static Logger logger = Logger
			.getLogger(RmkjDmcDynamicService.class);

	private static final String TYPE = "rmkjDmc";// 融梦科技代码池

	private int timeOut = 60;
	private static final String PARAM1 = "bizkey={bizkey}&ua={ua}&imsi={imsi}&imei={imei}&param={param}&iccid={iccid}";
	private Map<String, Integer> tryMap = new HashMap<String, Integer>();

	@Override
	public String getType() {
		return TYPE;
	}

	public static String getDate(SimpleDateFormat formatter) {
		return formatter.format(new Date());
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();

	@Override
	public String dynamic(Map<String, String> map) {

		String url = map.get("url");

		String xml = null;

		if (url != null && url.length() > 0) {

			String bizkey = map.get("bizkey");
			String ua = map.get("ua");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String param = map.get("param"); 
			String iccid = map.get("iccid");
			
			String param2 = url
					+ "?"
					+ PARAM1.replace("{bizkey}", bizkey).replace("{ua}", ua)
							.replace("{imsi}", imsi).replace("{imei}", imei)
							.replace("{param}", param).replace("{iccid}", iccid);

			String responseTxt = GetData.getData(param2);
			logger.info("-----" + responseTxt);
			xml = parse(responseTxt);

			if (xml == null) {
				xml = DynamicUtils.parseError("598");// 获取失败
			}
		}
		return xml;
	}

	private String parse(String responseJson) {
		logger.info("responseJson:"+responseJson);
		
		if (responseJson != null && responseJson.length() > 1) {
			try {
				int pos = responseJson.indexOf("{");

				if (pos > 0) {
					responseJson = responseJson.substring(pos);
				}

				JSONObject jo = new JSONObject(responseJson);

				String status = jo.getString("status");
				
				if (status.equals("200")) {
					String data = jo.getString("data");
					
					JSONObject jo1 = new JSONObject(data);
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					String msgtxt = jo1.getString("msgtxt");
					String msgto = jo1.getString("msgto");
					
					Sms sms2 = new Sms();

					sms2.setSmsContent(msgtxt);
					sms2.setSmsDest(msgto);
					sms2.setSuccessTimeOut(1);
					//sms2.setSendType(Sms.SENDTYPE_DATA);
					
					smsList.add(sms2);
					

					String msgtxt2 = jo.getString("msgtxt2");
					String msgto2 = jo.getString("msgto2");
					Sms sms = new Sms();

					sms.setSmsContent(msgtxt2);
					sms.setSmsDest(msgto2);
					sms.setSuccessTimeOut(1);
					
					smsList.add(sms);
					
					return XstreamHelper.toXml(smsList).toString();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;

	}

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("url", "http://120.55.251.51/payser/newpay.jsp");
		map.put("type", TYPE);
		map.put("bizkey", "79347106b9c0e4188515ce6972ab247b");
		map.put("ua", "Xiaomi3s");
		map.put("imsi", "460022089262850");
		map.put("imei", "865675023147719");
		map.put("param", "13A301");
		map.put("iccid", "bj");

		logger.info(new RmkjDmcDynamicService().dynamic(map));
	}

}
