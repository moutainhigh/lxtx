package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * http://124.172.232.224:8080/wo/jiuding/PayServerServlet?imei=35328506332906&imsi=46033481832&cpparam=010&game=mhxxl&fee=1
 * @author leoliu
 *
 */
public class GzqsDxDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(GzqsDxDynamicService.class);
	
	private static final String TYPE = "gzqsDx";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String DEST = "1065842410";
	
	private static final String REQUESTMODEL = "imei={imei}&imsi={imsi}&cpparam={cpparam}&game={game}&fee={fee}";
	
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
			
			String cpparam = channel;
			String game = map.get("game");
			String fee = map.get("fee");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String param = REQUESTMODEL.replace("{cpparam}",cpparam).replace("{game}",game).replace("{fee}",fee).replace("{imei}",imei).replace("{imsi}",imsi);
			
			String responseTxt = GetData.getData(url, param);
			
			xml = parse(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parse(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			logger.info(responseTxt);
			
			if("ok".equals(responseTxt)){
				Sets sets = new Sets();
				sets.setKey("succ");
				sets.setValue("1");
				return XstreamHelper.toXml(sets).toString();
			}else{
				return DynamicUtils.parseError(responseTxt);
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
		//http://211.154.162.11/recYdmmAction.action?companycode=W1001&pid=ttddz20c&statement=W100120140529174&imsi=460021281243687&imei=863388028868466&mobileip=202.108.36.125&sign=F0B9CB8094C477E328043EBBB9787362
		
		String url = "http://124.172.232.224:8080/wo/jiuding/PayServerServlet";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "10B101a087654321");
		map.put("imei", "353285061722906");
		map.put("imsi", "460036881804222");
		map.put("game", "mhxxl");
		map.put("fee","10");
		map.put("type","gzqsDx");
		
		System.out.println(new GzqsDxDynamicService().dynamic(map));
	}

}
