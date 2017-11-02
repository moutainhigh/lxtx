package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * @author leoliu
 *
 */
public class TuoWeiDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(TuoWeiDynamicService.class);

	private static final String TYPE = "twAyx";// 网页爱游戏

	private static final String PARAM1 = "telphone={telphone}&product_id={product_id}&pay_id={pay_id}";
	private static final String PARAM2 = "correlator={correlator}&check_code={check_code}&product_id={product_id}";

	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";

	private static final String RESULTCODE_SUCC = "1";



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
			String telphone = StringUtils.defaultString(map.get("dmobile"));
			String product_id = map.get("product_id");
			String pay_id = map.get("pay_id");

			String param = PARAM1.replace("{telphone}", telphone).replace("{product_id}", product_id)
					.replace("{pay_id}", pay_id);

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
						String correlator = jo.getString("correlator");

						Sets sets = new Sets();
						sets.setKey("correlator");
						sets.setValue(correlator);

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
			
			String correlator = map.get("correlator");
			String product_id = map.get("product_id");
			String check_code = map.get("check_code");

			String param = PARAM2.replace("{correlator}", correlator).replace("{product_id}", product_id)
					.replace("{check_code}", check_code);

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

				if (jo.has("code")) {
					String msg = jo.getString("msg");

					logger.info("parse second result code : " + msg);

					Sets sets = new Sets();
					sets.setKey("msg");
					sets.setValue(msg);

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
//		 test2();
	}

	private static void test1() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "1");
		map.put("url", "http://operator.zuoyetong.com.cn/pay/requestOrder");
		map.put("type", TYPE);
//		map.put("dmobile", "18185260331");
		map.put("dmobile", "18910119736");
		map.put("product_id", "50210246");
		map.put("pay_id", "11");

		logger.info(new TuoWeiDynamicService().dynamic(map));
	}

	private static void test2() {
		
		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "2");
		map.put("url", "http://operator.zuoyetong.com.cn/pay/confirmPlayOrder");
		map.put("type", TYPE);
		map.put("correlator", "A147366271548736");
		map.put("product_id", "50210246");
		map.put("check_code", "5328");

		logger.info(new TuoWeiDynamicService().dynamic(map));
	}
}
