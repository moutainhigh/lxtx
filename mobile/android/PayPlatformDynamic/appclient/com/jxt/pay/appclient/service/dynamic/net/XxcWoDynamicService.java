package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;

public class XxcWoDynamicService implements IDynamicService{

	private static final String TYPE = "xxcWo";
	
//	private static final String MSGCONTENT = "{iap}&{time}&{otnid}{channel}&{appid}@@{goodsid}@@{callbackData}";
	private static final String MSGCONTENT = "{iap}&{time}&{otnid}{channel}&{appid}@@{goodsid}@@{imei}@@{imsi}@@{callbackData}";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static final Guard guard1 = new Guard("10655477","成功|购买",1440,"1",0);
	private static final Guard guard2 = new Guard("10655477","",960,null,1);
	
	private static final String DEST = "10655477477478";
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		String parm = map.get("parm");
		
		String[] arr = parm.split("_");
		
		String time = sdf.format(new Date());
		
		String _channel = arr[4];
		
		int channelLen = _channel.length();
		
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0 ; i < 15 - channelLen; i ++){
			sb.append("0");
		}
		sb.append(_channel);
		
		String msg = MSGCONTENT.replace("{iap}", arr[0]).replace("{time}", time).replace("{otnid}", arr[1]);
		msg = msg.replace("{channel}",sb.toString()).replace("{appid}",arr[2]).replace("{goodsid}",arr[3]);
		msg = msg.replace("{callbackData}", arr[4]);
		
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		
		msg = msg.replace("{imei}",imei).replace("{imsi}",imsi);
		
		List<Sms> smsList = new ArrayList<Sms>();
		
		Sms sms = new Sms();
		
		sms.setSmsContent(msg);
		sms.setSmsDest(DEST);
		sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
		
		smsList.add(sms);
		
		Sms guardSms = new Sms();
		
		List<Guard> guardList = new ArrayList<Guard>();
		
		guardList.add(guard1);
		guardList.add(guard2);
		
		guardSms.setGuardList(guardList);
		
		smsList.add(0, guardSms);

		return XstreamHelper.toXml(smsList);
	}
	
	public static void main(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("parm","000016000004_005410002900085_29_85_87654321");
		map.put("imsi","460017402063064");
		map.put("imei","862012020094373");
		
		System.out.println(new XxcWoDynamicService().dynamic(map));
	}

}
