package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Map;

public interface IDynamicService {

	public String getType();
	
	public String dynamic(Map<String,String> map);
	
}
