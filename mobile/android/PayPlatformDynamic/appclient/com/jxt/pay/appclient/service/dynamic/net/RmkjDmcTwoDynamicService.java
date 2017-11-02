package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * @author leoliu
 * 
 */
public class RmkjDmcTwoDynamicService implements IDynamicService {

	private static Logger logger = Logger.getLogger(RmkjDmcTwoDynamicService.class);

	private static final String TYPE = "rmkjDmctwo";// 融梦科技代码池2

	private int timeOut = 60;
	private static final String PARAM1 = "bizkey={bizkey}&ua={ua}&imsi={imsi}&imei={imei}&param={param}&location={location}";
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

			String bizkey = map.get("bizkey");
			String ua = "Xiaomi3s";
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String param = map.get("port");
			String location = getProvince(map.get("province"));

			String param2 =  PARAM1.replace("{bizkey}", bizkey).replace("{ua}", ua).replace("{imsi}", imsi)
					.replace("{imei}", imei).replace("{param}", param).replace("{location}", location);

			String responseTxt = GetData.getData(url ,param2);
			logger.info("-----" + responseTxt);
			xml = parse(responseTxt);

			if (xml == null) {
				xml = DynamicUtils.parseError("598");// 获取失败
			}
		}
		return xml;
	}

	private static String getProvince(String srcPro) {
		int srcProI = Integer.parseInt(srcPro);

		switch (srcProI) {
		case 8611:
			return "bj";
		case 8612:
			return "tj";
		case 8613:
			return "heb";
		case 8614:
			return "sx";
		case 8615:
			return "nmg";
		case 8621:
			return "ln";
		case 8622:
			return "jl";
		case 8623:
			return "hlj";
		case 8631:
			return "sh";
		case 8632:
			return "js";
		case 8633:
			return "zj";
		case 8634:
			return "ah";
		case 8635:
			return "fj";
		case 8636:
			return "jx";
		case 8637:
			return "sd";
		case 8641:
			return "hen";
		case 8642:
			return "hub";
		case 8643:
			return "hn";
		case 8644:
			return "gd";
		case 8645:
			return "gx";
		case 8646:
			return "hain";
		case 8650:
			return "cq";
		case 8651:
			return "sc";
		case 8652:
			return "gz";
		case 8653:
			return "yn";
		case 8654:
			return "xz";
		case 8661:
			return "shxi";
		case 8662:
			return "gs";
		case 8663:
			return "qh";
		case 8664:
			return "nx";
		case 8665:
			return "xj";
		}

		return "1";
	}

	private String parse(String responseJson) {
		logger.info("responseJson:" + responseJson);

		if (responseJson != null && responseJson.length() > 1) {
			try {
				int pos = responseJson.indexOf("{");

				if (pos > 0) {
					responseJson = responseJson.substring(pos);
				}

				JSONObject jo = new JSONObject(responseJson);

				String status = jo.getString("status");

				if (status.equals("200")) {
					String data = jo.getString("data");

					JSONObject jo1 = new JSONObject(data);

					List<Sms> smsList = new ArrayList<Sms>();

					String msgtxt = jo1.getString("msgtxt");
					String msgto = jo1.getString("msgto");

					Sms sms2 = new Sms();

					sms2.setSmsContent(msgtxt);
					sms2.setSmsDest(msgto);
					sms2.setSuccessTimeOut(1);
					// sms2.setSendType(Sms.SENDTYPE_DATA);

					smsList.add(sms2);

					String msgtxt2 = jo1.getString("msgtxt2");
					String msgto2 = jo1.getString("msgto2");
					Sms sms = new Sms();

					sms.setSmsContent(msgtxt2);
					sms.setSmsDest(msgto2);
					sms.setSuccessTimeOut(1);

					smsList.add(sms);

					return XstreamHelper.toXml(smsList).toString();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;

	}

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("url", "http://120.55.251.51/payser/newpay.jsp");
		map.put("type", TYPE);
		map.put("bizkey", "79347106b9c0e4188515ce6972ab247b");
		map.put("imsi", "460022475975452");
		map.put("imei", "867451025555753");
		map.put("port", "a001513A301");
		map.put("province", "8615");

		logger.info(new RmkjDmcTwoDynamicService().dynamic(map));
	}

}
