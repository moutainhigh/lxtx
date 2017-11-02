package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * @author leoliu
 * 
 */
public class CdblppDynamicService implements IDynamicService {

	private static Logger logger = Logger
			.getLogger(CdblppDynamicService.class);

	private static final String TYPE = "cdblpp";// 成都贝乐MM(pp9)

	private int timeOut = 60;
	private static final String PARAM1 = "operator={operator}&province={province}&imsi={imsi}&imei={imei}&fee={fee}&phoneType={phoneType}&ip={ip}&osbuild={osbuild}&cpid={cpid}&channelOrderId={channelOrderId}";
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

			String operator = map.get("operator");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String province = map.get("province");
			String fee = map.get("fee");
			String phoneType = map.get("phoneType");
			String ip = map.get(Constants.IPPARAM);
			String brand = map.get("brand");
			String osbuild = map.get("osbuild");
			String cpid = map.get("cpid");
			String channelOrderId = map.get("channelOrderId");
			
			
			
			String param2 = url
					+ "?"
					+ PARAM1.replace("{operator}", operator).replace("{province}", province)
							.replace("{imsi}", imsi).replace("{imei}", imei)
							.replace("{fee}", fee).replace("{phoneType}", phoneType).replace("{ip}", ip).replace("{channelOrderId}", channelOrderId)
							.replace("{brand}", brand).replace("{osbuild}", osbuild).replace("{cpid}", cpid);

			String responseTxt = GetData.getData(param2);
			logger.info("-----" + responseTxt);
			xml = parse(responseTxt);

			if (xml == null) {
				xml = DynamicUtils.parseError("598");// 获取失败
			}
		}
		return xml;
	}

	private String parse(String responseJson) {
		logger.info("responseJson:"+responseJson);
		
		if (responseJson != null && responseJson.length() > 1) {
			try {
				int pos = responseJson.indexOf("{");

				if (pos > 0) {
					responseJson = responseJson.substring(pos);
				}

				JSONObject jo = new JSONObject(responseJson);

				String rc = jo.getString("rc");
				
				if (rc.equals("0")) {
					
					String result = jo.getString("result");
					
					if (result.equals("0")) {
					
						
						
					String codetype = jo.getString("codetype");
					
					if (codetype.equals("1")) {
						String smsmsg = jo.getString("smsmsg");
//						String smsContent = CommonUtil.base64Decode(msgtxt);
						String smsport = jo.getString("smsport");

						Sms sms = new Sms();

						sms.setSmsContent(smsmsg);
						sms.setSmsDest(smsport);
						sms.setSuccessTimeOut(1);
						sms.setSendType("3");
						return XstreamHelper.toXml(sms).toString();
					}else if (codetype.equals("2")) {
						String smsmsg = jo.getString("smsmsg");
//						String smsContent = CommonUtil.base64Decode(msgtxt);
						String smsport = jo.getString("smsport");

						Sms sms = new Sms();

						sms.setSmsContent(smsmsg);
						sms.setSmsDest(smsport);
						sms.setSuccessTimeOut(1);
						sms.setSendType("2");
						return XstreamHelper.toXml(sms).toString();
					}
					
					
				}
					if (result.equals("1")) {
						
						Sets sets = new Sets();
						
						sets.setKey("_succ");
						sets.setValue("1");
						
						return XstreamHelper.toXml(sets).toString();
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

		map.put("url", "http://14.17.74.121:9900/sppayv2.do");
		map.put("type", TYPE);
		map.put("operator", "1");
		map.put("province", "1");
		map.put("imsi", "460078010952058");
		map.put("imei", "867451025555753");
		map.put("fee", "1000");
		map.put("cpid", "1b42c870");
		map.put("channelOrderId", "pp9");
		map.put("osbuild", "18");
		map.put("phoneType", "Xiaomi_Xiaomi_MI");
		map.put("brand", "18");
		map.put(Constants.IPPARAM,"221.221.234.2");

		logger.info(new CdblppDynamicService().dynamic(map));
	}

}
