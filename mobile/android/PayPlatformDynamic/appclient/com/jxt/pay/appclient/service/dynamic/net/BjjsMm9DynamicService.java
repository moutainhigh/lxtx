package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

/**
 * http://116.205.4.157:9900/recharges.do
请求方式
Get
传入参数
 
参数名称	类型	是否可为空	描述信息	   
cpid	String	否	合作方的渠道ID，由我方分配	   
imsi	String	否	用户终端的imsi信息	   
imei	String	否	用户终端的imei信息	   
fee	String	否	金额	   
ext	String	否	我们自己定义，但是渠道上量的时候必须是我们定义的这个值，以便区分同步,ext=dataX00.X表示资费。	 
      cpid=5dc01d10 
      同步端口：10658800
      同步指令：dateXK，X表示资费，K大写
      资费1-10.是1/2/3/4/5/6/7/8/9/10元
      
            返回结果
 
参数名称	类型	是否可为空	描述信息	   
num	String	否	发送短信的号码	   
content	String	否	发送短信内容	   
error	String	否	成功获取短信为0，失败请见错误代码	 

 * @author leoliu
 *
 */
public class BjjsMm9DynamicService implements IDynamicService{

	private static final String TYPE = "bjjsMm9";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "10658424";
	
	private static final String REQUESTMODEL = "imei={imei}&imsi={imsi}&cpid={cpid}&fee={fee}&ext={ext}&ip={ip}";
	
	private static final Guard guard1 = new Guard("10658800","成功|购买",2880,"1",0);
	private static final Guard guard2 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard3 = new Guard("10658800","",960,null,1);
	private static final Guard guard4 = new Guard("10086","",960,null,1);
	
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
			
			String ext = map.get("ext");
			String fee = map.get("fee");
			String cpid = map.get("cpid");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{ext}",ext).replace("{fee}",fee).replace("{cpid}", cpid).replace("{imei}",imei).replace("{imsi}",imsi).replace("{ip}", map.get(Constants.IPPARAM));
			
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
	
	private String parse(String responseJson){
		
		if(responseJson != null && responseJson.length() > 10){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String num = jo.getString("num");
				String content = jo.getString("content");
				
				if(num != null && num.length() > 0 && content != null && content.length() > 0){
					List<Sms> smsList = new ArrayList<Sms>();
					
					Sms sms = new Sms();
					
					sms.setSmsContent(content);
					sms.setSmsDest(num);
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
				}else{
					String error = jo.getString("error");
					
					if(error != null && error.length() > 0){
						return DynamicUtils.parseError(error);
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
		
	public static void main(String[] args){
		String url = "http://116.205.4.157:9900/recharges.do";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a87654321");
		map.put("imei", "356216042580565");
		map.put("imsi", "460023779821724");
		map.put("cpid", "5dc01d10");
		map.put("ext","data9k");
		map.put("fee", "900");
		map.put("type", "bjjsMm9");
		
		System.out.println(new BjjsMm9DynamicService().dynamic(map));
	}

}
