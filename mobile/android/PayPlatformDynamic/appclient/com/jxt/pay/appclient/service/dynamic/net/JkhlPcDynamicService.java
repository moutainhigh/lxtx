package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 
 * @author leoliu
 *
 */
public class JkhlPcDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(JkhlPcDynamicService.class);
	
	private static final String TYPE = "jkhlPc";
	
	private static final String PARAM1 = "method=applyforpurchase&feecode={feecode}&developer_id={developer_id}&tel={tel}&extdata={extdata}";
	private static final String PARAM2 = "method=confirmpurchase&orderid={orderid}&verifycode={verifycode}&timestamp={timestamp}";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private int timeOut = 60;
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
		}else if(THENO_2.equals(theNo)){
			return secondDynamic(map);
		}		
		
		return null;
	}

	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		String channel = map.get("channel");
		String mobile = map.get("dmobile");
		
		if(url != null && url.length() > 0){
			String feecode = map.get("feecode");
			String developer_id = map.get("developer_id");
			String tel = map.get("dmobile");
			String extdata = map.get("extdata");
			
			String param = PARAM1.replace("{feecode}", feecode).replace("{developer_id}",developer_id).replace("{tel}",tel).replace("{extdata}",extdata);
			
			String responseXml = GetData.getData(url, param);
			
			xml = parseFirst(map,responseXml);

			if(xml == null){
				Integer cnt = map1.get(channel);
				
				if(cnt == null){
					cnt = 0;
				}
				
				if(cnt >= 3){
					map1.remove(channel);
					xml = DynamicUtils.parseError("598");
				}else{
					cnt ++;
					map1.put(channel, cnt);
					
					xml = DynamicUtils.parseWait(10,map);//获取失败
				}
			}else{
				map1.remove(channel);
			}
			
		}
		
		return xml;
	}
	
	private String parseFirst(Map<String,String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			try{
				
				String resultCode = SingleXmlUtil.getNodeValue(responseXml,"resultCode");
				
				logger.info("parse first result code : "+resultCode);
				
				if("1000".equals(resultCode)){
					String orderid = SingleXmlUtil.getNodeValue(responseXml,"orderid");
					
					Sets sets = new Sets();
					sets.setKey("orderid");
					sets.setValue(orderid);
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(resultCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			String orderid = map.get("orderid");
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(orderid);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(orderid,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(orderid);
					return DynamicUtils.parseError("599");
				}
			}
			
			String verifycode = map.get("verifycode");
			String timestamp = sdf.format(new Date());
			
			String param = PARAM2.replace("{orderid}", orderid).replace("{verifycode}",verifycode).replace("{timestamp}",timestamp);
			
			String responseXml = GetData.getData(url, param);
			
			logger.info("responseJson2:"+responseXml);
			
			xml = parseSecond(map,responseXml);
			
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(10,map);//获取失败
		}
		
		return xml;
	}

	
	private String parseSecond(Map<String, String> map, String responseXml) {
		if(responseXml != null && responseXml.length() > 0){
			try{
				
				String resultCode = SingleXmlUtil.getNodeValue(responseXml,"resultCode");
				
				logger.info("parse second result code : "+resultCode);
				
				if("1000".equals(resultCode)){
					Sets sets = new Sets();
					sets.setKey("resultCode1");
					sets.setValue(resultCode);
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(resultCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void main(String[] args){
		test2();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "1");
		map.put("type","jkhlPc");
		map.put("url", "http://202.85.212.189:8088/pcgame/fee/fee_sync.php");
		map.put("feecode", "10200100");
		map.put("developer_id", "005");
		map.put("extdata","03");
		map.put("dmobile", "13811155779");
		map.put("channel", "10B101a012345678");
		
		System.out.println(new JkhlPcDynamicService().dynamic(map));
	}

	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo", "2");
		map.put("type","jkhlPc");
		map.put("url", "http://202.85.212.189:8088/pcgame/fee/fee_sync.php");
		
		map.put("orderid", "0f00000f1ee2d4ee004d9d83");
		map.put("verifycode","583026");
		
		System.out.println(new JkhlPcDynamicService().dynamic(map));
	}
}
