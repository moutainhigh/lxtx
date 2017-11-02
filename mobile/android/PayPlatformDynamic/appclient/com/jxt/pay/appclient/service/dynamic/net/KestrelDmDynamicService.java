package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.qlzf.commons.kestrel.KestrelService;
import com.qlzf.commons.kestrel.KestrelServiceFactory;

public class KestrelDmDynamicService implements IDynamicService{

	private static final String TYPE = "kestrelDm";

	private static final String DEST = "1065843102";
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final Guard guard1 = new Guard("10658099","成功|购买|点播|资费",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费|动漫|资费",2880,null,1);
	private static final Guard guard3 = new Guard("10658099","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
	
		String appCode = map.get("appCode");
		
		if(appCode != null && appCode.length() > 0){
			KestrelService kestrelService = KestrelServiceFactory.getInstance().getKestrelService(appCode, ip, port, 1000l);
			
			if(kestrelService != null){
				String msg = kestrelService.getData();
				
				if(msg != null && msg.length() > 0){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(msg);
					
					String dest = map.get("dest");
					if(dest == null || dest.length() == 0){
						dest = DEST;
					}
					
					sms.setSmsDest(dest);
					
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
			}
		}
		
		return null;
	}

	//ioc
	private String ip;
	
	private int port;

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public static void main(String[] args){
		KestrelDmDynamicService service = new KestrelDmDynamicService();
		service.setIp("120.27.44.77");
		service.setPort(22134);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","kestrelDm");
		map.put("appCode", "renmdm");
		
		String xml = service.dynamic(map);
	
		System.out.println(xml);
	}
	
	
}
