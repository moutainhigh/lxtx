package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * @author leoliu
 * 
 */
public class RmkjYdspDynamicService implements IDynamicService {

	private static Logger logger = Logger
			.getLogger(RmkjYdspDynamicService.class);

	private static final String TYPE = "rmkjYdsp";// 爱游戏短代码

	private int timeOut = 60;
	private static final String PARAM1 = "cpid={cpid}&price={price}&imsi={imsi}&imei={imei}&ext={ext}";
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

			String cpid = map.get("cpid");
			String price = map.get("price");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String ext = StringUtils.defaultString(map.get("ext")); 

			String param = url
					+ "?"
					+ PARAM1.replace("{cpid}", cpid).replace("{price}", price)
							.replace("{imsi}", imsi).replace("{imei}", imei)
							.replace("{ext}", ext);

			String responseTxt = GetData.getData(param);
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
					String linkid = jo.getString("linkid");
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					String regSms = jo.getString("regSms");
					String regSpnum = jo.getString("regSpnum");
					
					Sms sms2 = new Sms();

					sms2.setSmsContent(regSms);
					sms2.setSmsDest(regSpnum);
					sms2.setSuccessTimeOut(1);
					sms2.setSendType(Sms.SENDTYPE_DATA);
					
					smsList.add(sms2);
					

					String sms1 = jo.getString("sms");
					String spnum = jo.getString("spnum");
					Sms sms = new Sms();

					sms.setSmsContent(sms1);
					sms.setSmsDest(spnum);
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

		map.put("url", "http://123.57.41.251/service/sp/sms/get/20");
		map.put("type", TYPE);
		map.put("cpid", "73");
		map.put("price", "20");
		map.put("imsi", "460022089262850");
		map.put("imei", "865675023147719");

		logger.info(new RmkjYdspDynamicService().dynamic(map));
	}

}
