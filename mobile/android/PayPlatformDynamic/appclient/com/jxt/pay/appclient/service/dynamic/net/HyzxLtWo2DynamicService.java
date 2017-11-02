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
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class HyzxLtWo2DynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(HyzxLtWo2DynamicService.class);

	private static final String TYPE = "hyzxLtWo2";// 华易掌信联通小沃

	private static final String PARAM1 = "type={type}&siteid={siteid}&codeid={codeid}&serial={serial}&imsi={imsi}";

	private static final String RESULTCODE_SUCC = "0";

	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();

	@Override
	/**
	 * url app pid money key
	 */
	public String dynamic(Map<String, String> map) {

		String url = map.get("url");

		String xml = null;

		if (url != null && url.length() > 0) {
			String type = map.get("Ttype");
			String siteid = map.get("siteid");
			String codeid = map.get("codeid");
			String serial = map.get("serial");
			String imsi = map.get("imsi");

			String param = PARAM1.replace("{type}", type).replace("{siteid}", siteid).replace("{codeid}", codeid)
					.replace("{imsi}", imsi).replace("{serial}", serial);

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
				responseJson = responseJson.replaceAll("\r", "").replace("\n", "");
				logger.info("JSON:" + responseJson);
				JSONObject jo = new JSONObject(responseJson);

				if (jo.has("hRet")) {
					String hRet = jo.getString("hRet");

					logger.info("parse first result code : " + hRet);

					if (RESULTCODE_SUCC.equals(hRet)) {

						String Login = jo.getString("Login");
						JSONObject jo1 = new JSONObject(Login);

						String num = jo1.getString("num");
						String smsContent = jo1.getString("sms");

						Sms sms = new Sms();
						sms.setSmsDest(num);
						sms.setSmsContent(smsContent);
						sms.setSendType("2");

						return XstreamHelper.toXml(sms).toString();
					}

					return DynamicUtils.parseError(hRet);
					// }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();

		map.put("url", "http://ivas.iizhifu.com/init.php");
		map.put("type", TYPE);
		map.put("Ttype", "180");
		map.put("siteid", "237");
		map.put("codeid", "1256005");
		map.put("serial", "TYZX");
		map.put("imsi", "460013348300424");

		// test1();
		logger.info(new HyzxLtWo2DynamicService().dynamic(map));
	}
}
