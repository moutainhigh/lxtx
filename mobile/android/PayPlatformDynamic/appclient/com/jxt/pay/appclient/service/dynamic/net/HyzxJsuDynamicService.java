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
public class HyzxJsuDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(HyzxJsuDynamicService.class);

	private static final String TYPE = "hyzxJsu";// 融梦科技超级马里奥

	private static final String PARAM1 = "pid={pid}&imei={imei}&imsi={imsi}&phone={phone}&ip={ip}&cpparam={cpparam}";
	private static final String PARAM2 = "code={code}&linkid={linkid}";

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

			String pid = map.get("pid");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String phone = StringUtils.defaultString(map.get("dmobile"));
			String cpparam = map.get("cpparam");
			String ip = map.get(Constants.IPPARAM);

			String param = PARAM1.replace("{pid}", pid).replace("{imsi}", imsi).replace("{imei}", imei)
					.replace("{phone}", phone).replace("{ip}", ip).replace("{cpparam}", cpparam);

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

				if (jo.has("state")) {
					String state = jo.getString("state");

					logger.info("parse first result code : " + state);

					if (state.equals("YZM")) {
						String linkid = jo.getString("linkid");

						Sets sets = new Sets();
						sets.setKey("linkid");
						sets.setValue(linkid);

						return XstreamHelper.toXml(sets).toString();
					}

					return DynamicUtils.parseError(state);
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

			String linkid = map.get("linkid");
			String code = map.get("code");

			String param = PARAM2.replace("{linkid}", linkid).replace("{code}", code);

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

				String state = jo.getString("state");

				if (RESULTCODE_SUCC.equals(state)) {

					logger.info("parse second result code : " + state);

					Sets sets = new Sets();
					sets.setKey("state");
					sets.setValue(state);

					return XstreamHelper.toXml(sets).toString();
				}
				return DynamicUtils.parseError(state);
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
		map.put("url", "http://p.mibeipay.com:8888/umpay");
		map.put("type", TYPE);
		map.put("pid", "63909");
		map.put("imei", "860814027011553");
		map.put("imsi", "460078616317857");
		map.put("dmobile", "18861635105");
		map.put(Constants.IPPARAM, "223.104.33.164");
		map.put("cpparam", "JDZX");

		logger.info(new HyzxJsuDynamicService().dynamic(map));
	}

	private static void test2() {

		Map<String, String> map = new HashMap<String, String>();

		map.put("theNo", "2");
		map.put("url", "http://p.mibeipay.com:8888/umcode");
		map.put("type", TYPE);
		map.put("linkid", "1234567");
		map.put("code", "795168");

		logger.info(new HyzxJsuDynamicService().dynamic(map));
	}
}
