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
public class RmkjSuperDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(RmkjSuperDynamicService.class);

	private static final String TYPE = "rmkjSuper";// 融梦科技超级马里奥

	private static final String PARAM1 = "phone={phone}&content={content}";
	private static final String PARAM2 = "tradeid={tradeid}&code={code}";

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
			String phone = StringUtils.defaultString(map.get("dmobile"));
			String content = map.get("content");

			String param = PARAM1.replace("{phone}", phone).replace("{content}", content);

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
						String tradeid = jo.getString("tradeid");

						Sets sets = new Sets();
						sets.setKey("tradeid");
						sets.setValue(tradeid);

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

			String tradeid = map.get("tradeid");
			String code = map.get("code");

			String param = PARAM2.replace("{tradeid}", tradeid).replace("{code}", code);

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
				
				if (jo.has("status")) {
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
//		test1();
		 test2();
	}

	private static void test1() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "1");
		map.put("url", "http://139.196.53.146:8888/keku/wo/getsmscode");
		map.put("type", TYPE);
		map.put("dmobile", "18655040758");
		map.put("content", "wo03");

		logger.info(new RmkjSuperDynamicService().dynamic(map));
	}

	private static void test2() {

		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "2");
		map.put("url", "http://139.196.53.146:8888/keku/wo/confirm");
		map.put("type", TYPE);
		map.put("tradeid", "201609231849112622319969");
		map.put("code", "675");

		logger.info(new RmkjSuperDynamicService().dynamic(map));
	}
}
