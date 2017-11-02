package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class XiaMenDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(XiaMenDynamicService.class);

	private static final String TYPE = "xiamen";// 厦门夜猫

	private static final String PARAM1 = "pay={pay}&amt={amt}&channeId={channeId}&xparm={xparm}&fmt={fmt}&ibs={ibs}&imsi={imsi}&imei={imei}";

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key
	 */
	public String dynamic(Map<String, String> map) {

		String url = map.get("url");

		String xml = null;

		if (url != null && url.length() > 0) {
			String pay = map.get("pay");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String amt = map.get("amt");
			String channeId = map.get("channeId");
			String xparm = map.get("xparm");
			String fmt = map.get("fmt");
			String ibs = map.get("ibs");

			String param = PARAM1.replace("{pay}", pay).replace("{imsi}", imsi).replace("{imei}", imei)
					.replace("{amt}", amt).replace("{channeId}", channeId).replace("{xparm}", xparm).replace("{fmt}", fmt)
					.replace("{ibs}", ibs);

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
					
					
					if (code.equals("0")) {
						
						String msg = jo.getString("sms0");
						String port0 = jo.getString("port0");
						msg = CommonUtil.base64Decode(msg);
						Sms sms = new Sms();
						sms.setSmsDest(port0);
						sms.setSmsContent(msg);
						sms.setSuccessTimeOut(2);

						return XstreamHelper.toXml(sms).toString();
					} else {
						return DynamicUtils.parseError("598");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("url", "http://101.200.75.158/ydkj/query");
		map.put("type", TYPE);
		map.put("pay", "900400126038");
		map.put("amt", "1000");
		map.put("channeId", "PZW6008");
		map.put("imsi", "460078010952058");
		map.put("imei", "867451025555753");
		map.put("xparm", "lxtx2016");
		map.put("fmt", "json");
		map.put("ibs", "1");

		logger.info(new XiaMenDynamicService().dynamic(map));
	}

}
