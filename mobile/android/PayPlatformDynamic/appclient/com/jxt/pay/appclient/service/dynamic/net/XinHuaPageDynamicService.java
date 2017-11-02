package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;

public class XinHuaPageDynamicService implements IDynamicService{

	private static final String TYPE = "xinhuapage";
	private static final String PARAMS1 = "linkid={linkid}&money={money}&mobile={mobile}&partner={partner}&pex={pex}&spPayType={spPayType}&terminalIP={terminalIP}";
	private static final String PARAMS2 = "linkid={linkid}&mobile={mobile}&partner={partner}&pex={pex}&usrRand={usrRand}";
	
	private static Logger logger = Logger.getLogger(XinHuaPageDynamicService.class);
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else if("2".equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}
	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String linkid = map.get("channel");
		String money = map.get("price");
		String mobile = map.get("mobile");
		mobile = transMobile(mobile);
		String partner = map.get("partner");
		String pex = map.get("pex");
		String spPayType = map.get("spPayType");
		String terminalIP = map.get(Constants.IPPARAM);
		
		String params = PARAMS1.replace("{linkid}",linkid).replace("{money}", money).replace("{mobile}", mobile).replace("{partner}", partner).replace("{pex}", pex).replace("{spPayType}", spPayType).replace("{terminalIP}", terminalIP);
		
		String resp = GetData.getData(url, params);
		
		return parseResult(resp);
	}

	private String parseResult(String resp){
		logger.info("resp:"+resp);
		
		if(resp != null && resp.length() > 0){
			try{
				JSONObject jo = new JSONObject(resp);
				
				int code = jo.getInt("code");
				
				if(code == 0){
					Sets sets = new Sets();
					sets.setKey("dynamicResult");
					sets.setValue("1");
					
					return XstreamHelper.toXml(sets).toString();
				}else{
					return DynamicUtils.parseError("Err"+code);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static String transMobile(String mobile){
		String mid = new String(mobile);
		mid = mid.replaceAll("0", "R");
		mid = mid.replaceAll("1", "I");
		mid = mid.replaceAll("2", "Z");
		mid = mid.replaceAll("3", "B");
		mid = mid.replaceAll("4", "H");
		mid = mid.replaceAll("5", "G");
		mid = mid.replaceAll("6", "E");
		mid = mid.replaceAll("7", "C");
		mid = mid.replaceAll("8", "F");
		mid = mid.replaceAll("9", "O");
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(mid.substring(0,5)).append("KAF").append(mid.substring(5));
		return strBuf.toString();
	}
	
	private String secondDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String linkid = map.get("channel");
		String mobile = map.get("mobile");
		mobile = transMobile(mobile);
		String partner = map.get("partner");
		String pex = map.get("pex");
		String usrRand = map.get("usrRand");
		
		String params = PARAMS2.replace("{linkid}",linkid).replace("{mobile}", mobile).replace("{partner}", partner).replace("{pex}", pex).replace("{usrRand}", usrRand);
		
		String resp = GetData.getData(url, params);
		
		return parseResult(resp);
	}
	
	public static void main(String[] args){
//		test1();
		test2();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","xinhuapage");
		map.put("theNo","1");
		map.put("url","http://120.24.177.7:9090/api/realwebpay/partner/WABP/partnerGetPayCode");
		map.put("channel","111135");
		map.put("price","100");
		map.put("mobile","13811155779");
		map.put("partner","XHS");
		map.put("pex","100");
		map.put("spPayType","0");
		map.put(Constants.IPPARAM,"61.48.44.205");
		
		XinHuaPageDynamicService service = new XinHuaPageDynamicService();
		
		String ret = service.dynamic(map);
		
		System.out.println(ret);
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","xinhuapage");
		map.put("theNo","2");
		map.put("url","http://120.24.177.7:9090/api/realwebpay/partner/WABP/partnerSendPayCode");
		map.put("channel","111135");
		map.put("mobile","13811155779");
		map.put("partner","XHS");
		map.put("pex","100");
		map.put("usrRand","414903");
		
		XinHuaPageDynamicService service = new XinHuaPageDynamicService();
		
		String ret = service.dynamic(map);
		
		System.out.println(ret);
	}
}
