package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.utils.GetData;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 赞成科技 网游
 * @author leoliu
 *
 */
public class ZcDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZcDynamicService.class);
	
	private static final String TYPE = "zc";
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String PARAM_MODEL1 = "app={app}&pid={pid}&money={money}&time={time}&sign={sign}";
	
	private static final String PARAM_MODEL2 = "app={app}&sign={sign}&time={time}&pid={pid}&orderid={orderid}&verifycode={verifycode}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
		}else if(THENO_2.equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}

	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String app = map.get("app");
			String pid = map.get("pid");
			String money = map.get("money");
			String key = map.get("key");
			long time = System.currentTimeMillis()/1000;
			
			StringBuffer sb = new StringBuffer();
			sb.append("app=").append(app).append("&pid=").append(pid);
			sb.append("&time=").append(time).append("&key=").append(key);
			
			String src = sb.toString();
			logger.info("src : "+src);
			
			String sign = MD5Encrypt.MD5Encode(src).toLowerCase();
			logger.info("sign : "+sign);
			
			String param = PARAM_MODEL1.replace("{app}", app).replace("{pid}",pid)
					.replace("{money}", money).replace("{time}", time+"").replace("{sign}",sign);
			
			String requestUrl = url + "?" + param;
			
			logger.info("requestUrl : "+requestUrl);
			
//			String reponseJson = GetData.getData(requestUrl);
//			
//			return reponseJson;
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String,String> map){
		
		
		return null;
	}
	
	public static void main(String[] args){
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("url", "http://xq2.1277527.com/0901?http://111.13.47.76:81/open_gate/web_game_fee.php");
		map.put("app", "sxd");
		map.put("pid", "2004");
		map.put("key", "4eccd8af01b41852fb01c20875fabb07");
		map.put("money", "2");
		map.put("theNo", "1");
		
		logger.info("result : "+new ZcDynamicService().dynamic(map));
	}
}
