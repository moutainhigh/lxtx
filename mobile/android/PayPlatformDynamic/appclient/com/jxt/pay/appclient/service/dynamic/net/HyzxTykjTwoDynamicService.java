package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.XmlUtils;

/**
 * @author leoliu
 *
 */
public class HyzxTykjTwoDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(HyzxTykjTwoDynamicService.class);

	private static final String TYPE = "hyzxTykjTwo";// 华易掌信天翼空间TWO

	private static final String PARAM1 = "FeeId={FeeId}&MyOrderId={MyOrderId}&IMEI={IMEI}&IMSI={IMSI}&ChannelId={ChannelId}&Mobile={Mobile}";

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

		String url = map.get("url");

		String xml = null;

		if (url != null && url.length() > 0) {

			String FeeId = map.get("FeeId");
			String MyOrderId = map.get("MyOrderId");
			String IMEI = map.get("IMEI");
			String IMSI = map.get("IMSI");
			String ChannelId = map.get("ChannelId");
			String Mobile = map.get("Mobile");

			String param = PARAM1.replace("{FeeId}", FeeId).replace("{MyOrderId}", MyOrderId).replace("{IMEI}", IMEI)
					.replace("{IMSI}", IMSI).replace("{ChannelId}", ChannelId).replace("{Mobile}", Mobile);

			String responseXml = GetData.getData(url, param);
			// String responseJson = new PostData().PostData(param.getBytes(),
			// url);
			logger.info("-----" + responseXml);
			xml = parseFirst(map, responseXml);

			if (xml == null) {
				xml = DynamicUtils.parseError("598");// 获取失败
			}
		}
		return xml;
	}

	private String parseFirst(Map<String, String> map, String responseXml) {

		logger.info("parseFirst : " + responseXml);

		try {

			String RetCode = XmlUtils.getNodeValue(responseXml, "RetCode");

			if (RESULTCODE_SUCC.equals(RetCode)) {

				String SpNumber_2 = XmlUtils.getNodeValue(responseXml, "SpNumber_2");
				if (SpNumber_2 != null && SpNumber_2.length() > 0) {

					String mo = XmlUtils.getNodeValue(responseXml, "OrderSms64");
					String mo2 = XmlUtils.getNodeValue(responseXml, "OrderSms64_2");

					mo = CommonUtil.base64Decode(mo);
					mo2 = CommonUtil.base64Decode(mo2);

					String Called = XmlUtils.getNodeValue(responseXml, "SpNumber");
					String Called2 = XmlUtils.getNodeValue(responseXml, "SpNumber_2");

					// List<Sms> smsList = new ArrayList<Sms>();

					String IntervalTime = XmlUtils.getNodeValue(responseXml, "IntervalTime");

					Sms sms = new Sms();

					sms.setSmsContent(mo);
					sms.setSmsDest(Called);
					sms.setSuccessTimeOut(2);
					// smsList.add(sms);

					Sms sms1 = new Sms();

					sms1.setSmsContent(mo2);
					sms1.setSmsDest(Called2);
					sms1.setSuccessTimeOut(2);
					return XstreamHelper.toXml(sms).toString() + "<wait>" + IntervalTime + "</wait>"
							+ XstreamHelper.toXml(sms1).toString();

				}

				String mo = XmlUtils.getNodeValue(responseXml, "OrderSms64");
				String Called = XmlUtils.getNodeValue(responseXml, "SpNumber");
				mo = CommonUtil.base64Decode(mo);

				Sms sms = new Sms();

				sms.setSmsContent(mo);
				sms.setSmsDest(Called);
				sms.setSuccessTimeOut(2);

				return XstreamHelper.toXml(sms).toString();

			} else {
				return DynamicUtils.parseError(RetCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();
		// map.put("theNo", "1");
		map.put("url", "http://if.unidian.com/TYKJ/GetOrderSms_API.aspx");
		map.put("type", TYPE);
		map.put("FeeId", "88");
		map.put("MyOrderId", "88");
		map.put("IMEI", "867451025555753");
		map.put("IMSI", "460022475975452");
		map.put("ChannelId", "31");
		map.put("Mobile", "15247539458");
		logger.info(new HyzxTykjTwoDynamicService().dynamic(map));
	}

}
