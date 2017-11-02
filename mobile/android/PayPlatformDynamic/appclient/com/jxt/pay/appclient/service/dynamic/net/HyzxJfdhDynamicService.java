package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * 手游世纪电信
 * @author leoliu
 *
 */
public class HyzxJfdhDynamicService implements IDynamicService{
	
	private static final String TYPE = "hyzxJfdh";
	private static Logger logger = Logger.getLogger(HyzxJfdhDynamicService.class);
	
	private static final String PARAM1 = "type={type}&siteid={siteid}&codeid={codeid}&serial={serial}&i={i}&imsi={imsi}&vcode={vcode}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String type = map.get("Ttype");
		String siteid = map.get("siteid");
		String codeid = map.get("codeid");
		String serial = map.get("serial");
		String vcode = map.get("vcode");
		String i = map.get("i");
		String imsi = map.get("imsi");
		
		String param = PARAM1.replace("{type}", type).replace("{imsi}",imsi).replace("{siteid}",siteid).replace("{codeid}",codeid).replace("{serial}",serial).replace("{i}",i).replace("{vcode}",vcode);
		
		String responseJson = GetData.getData(url, param);
		logger.info("-----"+responseJson);
		
		
//		logger.info(responseJson);
		if(responseJson != null && responseJson.length() > 0){
			
			try {
				JSONObject jo = new JSONObject(responseJson);
//				String data = jo.getString("data");
//				if(data != null && data.length() > 0){
//					
//				JSONObject jo1 = new JSONObject(data);
				
				String code = jo.getString("hRet");
				
				if(code.equals("0")){
					
//					String msg = jo.getString("msg");
					Sets sets = new Sets();
					sets.setKey("_succ");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError(code);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			
		return null;
	}
	
	

	public static void main(String[] args){
		
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://ivas.iizhifu.com/init.php");
		map.put("type","hyzxJfdh");
		map.put("Ttype","101");
		map.put("siteid","191");
		map.put("codeid","1019001");
		map.put("serial","13A301");
		map.put("i","1");
		map.put("vcode","8512785850");
		map.put("imsi","460001421592091");
		System.out.println(new HyzxJfdhDynamicService().dynamic(map));
	}
	
	
}
