package com.lxtech.cloud.cache;

import java.util.Map;

import com.lxtech.cloud.util.JsonUtil;
import com.lxtech.cloud.util.http.HttpRequest;

public class AccessTokenCache extends SystemCache<String, String>{
	public AccessTokenCache(int maxSize, int expire) {
		super(maxSize, expire);
	}
	
	@Override
	public String loadFromStore(String k) {
		String[] app_config = k.split("_");
		String param = String.format("grant_type=client_credential&appid=%s&secret=%s", app_config[0],
				app_config[1]);
		String response = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token", param);
		Map map = (Map) JsonUtil.convertStringToObject(response);
		return (String) map.get("access_token");
	}
}
