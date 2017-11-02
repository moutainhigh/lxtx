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

public class KestrelMmqdDynamicService implements IDynamicService{

	private static final String TYPE = "kestrelMmqd";
	
	private static final String APPCODE = "mmqd";
	
	@Override
	public String getType() {
		if(type == null || type.length() == 0){
			return TYPE;
		}else{
			return type;
		}
	}

	@Override
	public String dynamic(Map<String, String> map) {
	
		int count = 3;
		
		String _count = map.get("count");
		
		if(_count != null && _count.length() > 0){
			try {
				count = Integer.parseInt(_count);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		if(appCode == null || appCode.length() == 0){
			appCode = APPCODE;
		}
		
			KestrelService kestrelService = KestrelServiceFactory.getInstance().getKestrelService(appCode, ip, port, 1000l);
			
			if(kestrelService != null){
				StringBuffer sb = new StringBuffer();
				
				for(int i = 0 ; i < count ; i ++){
					String msg = kestrelService.getData();
					
					if(msg == null || msg.length() == 0){
						break;
					}
					
					sb.append(msg);
				}
				
				return sb.toString();
			}
		
		return null;
	}

	//ioc
	private String type;
	
	private String appCode;
	
	private String ip;
	
	private int port;

	public void setType(String type) {
		this.type = type;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public static void main(String[] args){
		KestrelMmqdDynamicService service = new KestrelMmqdDynamicService();
		service.setIp("120.27.44.77");
		service.setPort(22134);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","kestrelDm");
		map.put("appCode", "renmdm");
		
		String xml = service.dynamic(map);
	
		System.out.println(xml);
	}
	
	
}
