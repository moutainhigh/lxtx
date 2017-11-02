package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * @author leoliu
 *
 */
public class HyzxMobileDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(HyzxMobileDynamicService.class);

	private static final String TYPE = "hyzxMobile";// 华易掌信移动RDO

	private static final String PARAM1 = "mob={mob}&appid={appid}&imsi={imsi}&imei={imei}&chorderno={chorderno}&fee={fee}&opid={opid}&sdk={sdk}&ip={ip}";

	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";

	private static final String RESULTCODE_SUCC = "0";

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
			String mob = StringUtils.defaultString(map.get("dmobile"));
			String appid = "1490";
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String chorderno = map.get("chorderno");
			String fee = "1000";
			String opid = "1";
			String sdk = "2.0";
			String ip = map.get(Constants.IPPARAM);

			String param = PARAM1.replace("{mob}", mob).replace("{appid}", appid).replace("{imsi}", imsi)
					.replace("{imei}", imei).replace("{chorderno}", chorderno).replace("{fee}", fee)
					.replace("{opid}", opid).replace("{sdk}", sdk).replace("{ip}", ip);

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

				if (jo.has("code")) {
					String code = jo.getString("code");

					logger.info("parse first result code : " + code);

					if (RESULTCODE_SUCC.equals(code)) {
						String execurl = jo.getString("execurl");

						Sets sets = new Sets();
						sets.setKey("execurl");
						sets.setValue(execurl);

						return XstreamHelper.toXml(sets).toString();
					}

					return DynamicUtils.parseError(code);
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

		if (url != null && url.length() > 0) {

			String code = map.get("code");

			String param = url + code;

			String responseJson = GetData.getData(param);

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
				String code = jo.getString("code");
				if (RESULTCODE_SUCC.equals(code)) {

					logger.info("parse second result code : " + code);

					Sets sets = new Sets();
					sets.setKey("code");
					sets.setValue(code);

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
		map.put("url", "http://api.170ds.com/wlpaysdk/fee/feeInfo");
		map.put("type", TYPE);
		map.put("dmobile", "18388790741");
		map.put("appid", "1490");
		map.put("imsi", "460020662134370");
		map.put("imei", "865180024575957");
//		map.put("chorderno", "ZXZ1232412");
//		map.put("fee", "1000");
		map.put("opid", "1");
		map.put("sdk", "2.0");
		map.put(Constants.IPPARAM, "221.221.234.2");

		logger.info(new HyzxMobileDynamicService().dynamic(map));
	}

	private static void test2() {

		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "2");
		map.put("url",
				"http://api.170ds.com/wlpaysdk/fee/verifycode?pp=10763&ff=submit&oo=1000000002945860&vv=orderNo%3D1000000002945860%26mobileNum%3D18388790741%26verifyCode%3DVVCODEVV&code=");
		map.put("type", TYPE);
		map.put("code", "675");

		logger.info(new HyzxMobileDynamicService().dynamic(map));
	}
}
