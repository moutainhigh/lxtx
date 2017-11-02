package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class YMSCDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(YMSCDynamicService.class);

	private static final String TYPE = "ymsc";// 夜猫书城

	private static final String PARAM1 = "cpid={cpid}&spid={spid}&paycode={paycode}&orderid={orderid}&channelId={channelId}&imsi={imsi}&imei={imei}&sign={Sign}";
	private static final String PARAM2 = "seqId={seqId}";

	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";

	private static final String RESULTCODE_SUCC = "0";

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

	private int timeOut = 60;

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key
	 */
	public String dynamic(Map<String, String> map) {

		String theNo = map.get("theNo");

		if (THENO_1.equals(theNo)) {
			return firstDynamic(map);
		} else if (THENO_2.equals(theNo)) {
			return secondDynamic(map);
		}

		return null;
	}

	public static String gettime() {
		String time = "" + System.currentTimeMillis() / 1000;
		return time;
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();

	private String firstDynamic(Map<String, String> map) {

		String url = map.get("url");

		String xml = null;

		if (url != null && url.length() > 0) {
			String cpid = map.get("cpid");
			String spid = map.get("spid");
			String paycode = map.get("paycode");
			String orderid = map.get("orderid");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String channelId = map.get("channelId");

			String Authkey = "lxtx2029";
			String Sign = MD5Encrypt.MD5Encode(Authkey + imsi + imei + cpid);

			String param = PARAM1.replace("{cpid}", cpid).replace("{spid}", spid).replace("{paycode}", paycode)
					.replace("{orderid}", orderid).replace("{channelId}", channelId).replace("{imsi}", imsi)
					.replace("{imei}", imei).replace("{Sign}", Sign.toUpperCase());

			String responseJson = GetData.getData(url, param);
			logger.info("-----" + responseJson);
			xml = parseFirst(map, responseJson);

		}

		return xml;
	}

	private String parseFirst(Map<String, String> map, String responseJson) {

		if (responseJson != null && responseJson.length() > 0) {
			try {
				int pos = responseJson.indexOf("{");

				if (pos > 0) {
					responseJson = responseJson.substring(pos);
				}

				JSONObject jo = new JSONObject(responseJson);

				if (jo.has("result")) {
					String result = jo.getString("result");

					logger.info("parse first result code : " + result);

					if (RESULTCODE_SUCC.equals(result)) {
						String seqId = jo.getString("seqId");

						Sets sets = new Sets();
						sets.setKey("seqId");
						sets.setValue(seqId);

						return XstreamHelper.toXml(sets).toString();
					}

					return DynamicUtils.parseError(result);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private String secondDynamic(Map<String, String> map) {

		String url = map.get("url");

		String xml = null;
		String seqId = map.get("seqId");

		if (url != null && url.length() > 0) {

			
			
			String param = PARAM2.replace("{seqId}", seqId);

			String responseJson = GetData.getData(url, param);

			logger.info("responseJson2:" + responseJson);

			xml = parseSecond(map, responseJson);

		}

		return xml;
	}

	private String parseSecond(Map<String, String> map, String responseJson) {
		if (responseJson != null && responseJson.length() > 0) {
			try {
				int pos = responseJson.indexOf("{");

				if (pos > 0) {
					responseJson = responseJson.substring(pos);
				}

				JSONObject jo = new JSONObject(responseJson);

				if (jo.has("result")) {
					String result = jo.getString("result");

					logger.info("parse second result code : " + result);

						Sets sets = new Sets();
						sets.setKey("result");
						sets.setValue(result);

						return XstreamHelper.toXml(sets).toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static void main(String[] args) {
		test1();
		// test2();
	}

	private static void test1() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "1");
		map.put("url", "http://123.57.25.92:13888/Cmcc_ddo/getSMS");
		map.put("type", TYPE);
		map.put("cpid", "2029");
		map.put("spid", "1031");
		map.put("paycode", "800000000624");
		map.put("imsi", "460078010952058");
		map.put("imei", "867451025555753");
		map.put("orderid", "test002");
		map.put("channelId", "700002750");

		logger.info(new YMSCDynamicService().dynamic(map));
	}

	private static void test2() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "2");
		map.put("url", "http://123.57.25.92:13888/Cmcc_ddo/sendSMS");
		map.put("type", TYPE);
		map.put("seqId", "");
		logger.info(new YMSCDynamicService().dynamic(map));
	}
}
