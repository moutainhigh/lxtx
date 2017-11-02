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
public class HyzxWydDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(HyzxWydDynamicService.class);

	private static final String TYPE = "hyzxwyd";// 华易掌信包月

	private static final String PARAM1 = "paycode={paycode}&params={params}&imsi={imsi}&imei={imei}";
	private static final String PARAM2 = "vcode={vcode}&ordernum={ordernum}";

	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";

	private static final String RESULTCODE_SUCC = "1011";



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
			String paycode = map.get("paycode");
			String params = map.get("buzzy");
			String imsi = map.get("imsi");
			String imei =map.get("imei");
			
			String param = PARAM1.replace("{paycode}", paycode)
					.replace("{params}", params).replace("{imsi}", imsi).replace("{imei}", imei);

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

				if (jo.has("status")) {
					String status = jo.getString("status");

					logger.info("parse first result code : " + status);

					if (RESULTCODE_SUCC.equals(status)) {
						String orderNum = jo.getString("orderNum");

						Sets sets = new Sets();
						sets.setKey("ordernum");
						sets.setValue(orderNum);

						return XstreamHelper.toXml(sets).toString();
					}

					return DynamicUtils.parseError(status);
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
			
			String ordernum = map.get("ordernum");
			String vcode = map.get("vcode");

			String param = PARAM2.replace("{ordernum}", ordernum)
					.replace("{vcode}", vcode);

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
				String status = jo.getString("status");
				if (status.equals("1013")) {
					
					Sets sets = new Sets();
					sets.setKey("status");
					sets.setValue(status);

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
		map.put("url", "http://thread1.n8wan.com/request.jsp");
		map.put("type", TYPE);
		map.put("paycode", "101221");
		map.put("buzzy", "S10ZHY13A301");
		map.put("imsi", "460078010952058");
		map.put("imei", "867451025555753");
		
		logger.info(new HyzxWydDynamicService().dynamic(map));
	}

	private static void test2() {
		
		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "2");
		map.put("url", "http://thread1.n8wan.com/vcode.jsp");
		map.put("type", TYPE);
		map.put("ordernum", "2016101614933");
		map.put("vcode", "639213");

		logger.info(new HyzxWydDynamicService().dynamic(map));
	}
}
