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
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * @author leoliu
 * 
 */
public class CdblmmDynamicService implements IDynamicService {

	private static Logger logger = Logger
			.getLogger(CdblmmDynamicService.class);

	private static final String TYPE = "cdblmm";// 成都贝乐mm弱联

	private int timeOut = 60;
	private static final String PARAM1 = "channelid={channelid}&type={type}&imsi={imsi}&imei={imei}&fee={fee}&cpparam={cpparam}&ip={ip}";
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

			String channelid = map.get("channelid");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String type = map.get("TYPE1");
			String fee = map.get("fee");
			String cpparam = map.get("cpparam");
			String ip = map.get(Constants.IPPARAM);
			
			
			String param2 = url
					+ "?"
					+ PARAM1.replace("{channelid}", channelid).replace("{type}", type)
							.replace("{imsi}", imsi).replace("{imei}", imei)
							.replace("{fee}", fee).replace("{cpparam}", cpparam).replace("{ip}", ip);

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

				String resultCode = jo.getString("resultCode");
				
				if (resultCode.equals("0")) {
			
					String msgtxt = jo.getString("sms");
//					String smsContent = CommonUtil.base64Decode(msgtxt);
					String accessNo = jo.getString("accessNo");

					Sms sms = new Sms();

					sms.setSmsContent(msgtxt);
					sms.setSmsDest(accessNo);
					sms.setSuccessTimeOut(1);
					sms.setSendType("3");
					return XstreamHelper.toXml(sms).toString();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;

	}

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("url", "http://112.74.111.56:9039/gamesit/puburl");
		map.put("type", TYPE);
		map.put("channelid", "293");
		map.put("TYPE1", "15");
		map.put("imsi", "460078010952058");
		map.put("imei", "867451025555753");
		map.put("fee", "1000");
		map.put("cpparam", "201");
		map.put(Constants.IPPARAM,"221.221.234.2");

		logger.info(new CdblmmDynamicService().dynamic(map));
	}

}
