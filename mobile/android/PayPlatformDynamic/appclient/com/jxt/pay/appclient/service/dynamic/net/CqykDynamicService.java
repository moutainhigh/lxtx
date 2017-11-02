package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.qlzf.commons.helper.MD5Encrypt;

public class CqykDynamicService implements IDynamicService{

	private static final String TYPE = "cqyk";
	
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
		}else if("3".equals(theNo)){
			return thirdDynamic(map);
		}else if("4".equals(theNo)){
			return fourthDynamic(map);
		}
		
		return null;
	}
	
	private String firstDynamic(Map<String,String> map){
		String imsi = map.get("imsi");
		
		String imsiMd5 = MD5Encrypt.MD5Encode(imsi).toUpperCase();
		
		Sms sms = new Sms();
		sms.setSmsDest("1065843601");
		sms.setSmsContent("CMO_S="+imsiMd5);
		sms.setSuccessTimeOut(2);
		
		return XstreamHelper.toXml(sms).append("<wait>6</wait>").toString();
	}

	private String secondDynamic(Map<String,String> map){
		String url = map.get("url");
		String imei = map.get("imei");
		String price = map.get("price");
		
		String responseJson = GetData.getData(url, "imei="+imei+"&price="+price);
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String c_user_id = jo.getString("c_user_id");
				String service_id_price = jo.getString("service_id_price");
				String c_client_token = jo.getString("c_client_token");
				
				Sets sets = new Sets();
				sets.setKey("c_user_id");
				sets.setValue(c_user_id);
				
				Sets sets1 = new Sets();
				sets1.setKey("service_id_price");
				sets1.setValue(service_id_price);
				
				Sets sets2 = new Sets();
				sets2.setKey("c_client_token");
				sets2.setValue(c_client_token);
				
				return XstreamHelper.toXml(sets).append(XstreamHelper.toXml(sets1)).append(XstreamHelper.toXml(sets2)).toString();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String thirdDynamic(Map<String,String> map){
		String url = map.get("url");
		String imsi = map.get("imsi");
		String appId = map.get("appId");
		String pubKey = map.get("pubKey");
		String packName = map.get("packName");
		String imei = map.get("imei");
		String channel_id = map.get("channel_id");
//		String c_user_id = map.get("c_user_id");
//		String c_client_token = map.get("c_client_token");
		
		String param = "imsi="+MD5Encrypt.MD5Encode(imsi)+"&appId="+appId+"&pubKey="+pubKey+"&netMode=WIFI&packName="+packName;
		
		List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		
		pairList.add(new NameValuePair("version", "5.2.1"));
		pairList.add(new NameValuePair("client_token", imei));
		pairList.add(new NameValuePair("platform", "2"));
		pairList.add(new NameValuePair("channel_id", channel_id));
//		pairList.add(new NameValuePair("c_user_id", c_user_id));
//		pairList.add(new NameValuePair("c_client_token", c_client_token));
		pairList.add(new NameValuePair("User-Agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; Nexus 5 Build/KTU84P)"));
		pairList.add(new NameValuePair("Connection","Keep-Alive"));
		pairList.add(new NameValuePair("Accept-Encoding","gzip"));
		
		String responseXml = GetData.getData(url, param, pairList);
		
		System.out.println("responseXml3:"+responseXml);
		
		if(responseXml != null && responseXml.length() > 0){
			String resCode = SingleXmlUtil.getNodeValue(responseXml, "resCode");
			
			if("301001".equals(resCode)){
				return "<wait>1</wait>";
			}else{
				return DynamicUtils.parseError(resCode);
			}
		}
		
		return null;
	}
	
	private String fourthDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String imsi = map.get("imsi");
		String appId = map.get("appId");
		String pubKey = map.get("pubKey");
		String packName = map.get("packName");
		String imei = map.get("imei");
		String channel_id = map.get("channel_id");
//		String c_user_id = map.get("c_user_id");
//		String c_client_token = map.get("c_client_token");
		String service_id_price = map.get("service_id_price");
		
		String param = "imsi="+MD5Encrypt.MD5Encode(imsi)+"&appId="+appId+"&pubKey="+pubKey+"&netMode=WIFI&packName="+packName+"&serviceId="+service_id_price+"&grade=0&userId=0&imei="+imei;
		
		List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		
		pairList.add(new NameValuePair("version", "5.2.1"));
		pairList.add(new NameValuePair("client_token", imei));
		pairList.add(new NameValuePair("platform", "2"));
		pairList.add(new NameValuePair("channel_id", channel_id));
//		pairList.add(new NameValuePair("c_user_id", c_user_id));
//		pairList.add(new NameValuePair("c_client_token", c_client_token));
		pairList.add(new NameValuePair("User-Agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; Nexus 5 Build/KTU84P)"));
		pairList.add(new NameValuePair("Connection","Keep-Alive"));
		pairList.add(new NameValuePair("Accept-Encoding","gzip"));
		
		String responseXml = GetData.getData(url, param, pairList);

		System.out.println("responseXml4:"+responseXml);
		
		if(responseXml != null && responseXml.length() > 0){
			String resCode = SingleXmlUtil.getNodeValue(responseXml, "resCode");
			
			if("000000".equals(resCode)){
				return "<wait>1</wait>";
			}else{
				return DynamicUtils.parseError(resCode);
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
//		test1();
//		test2();
		test3();
//		test4();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("imsi", "460001163136078");
		map.put("theNo","1");
		
		String result = new CqykDynamicService().dynamic(map);
		
		System.out.println(result);
	}
	
	private static void test2(){
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("imei", "352171066058648");
		map.put("theNo","2");
		map.put("url","http://crack.ugmars.com/crackygd/project/ygd");
		map.put("price","5");
		
		String result = new CqykDynamicService().dynamic(map);
		
		System.out.println(result);
	}
	
	private static void test3(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo","3");
		map.put("url","http://radio.gomumu.com.cn:8080/AppPlatform/user/query");
		map.put("imsi","460001163136078");
		map.put("imei", "352171066058648");
		map.put("appId","003551453989011029");
		map.put("pubKey", "0737cb12c0eb2bc78ca93eebc499de0f");
		map.put("channel_id","109155");
//		map.put("c_user_id", "ffff010b0000000000000000000000000568f273f23188c623167304e8156b9e");
//		map.put("c_client_token", "ffff0105000000000000000000000000ff819f736df25eb50f085e5c3b3d0fca");
		map.put("packName","com.app.hero.ui");
		
		String result = new CqykDynamicService().dynamic(map);
		
		System.out.println(result);
		
	}
	
	private static void test4(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("theNo","4");
		map.put("url","http://radio.gomumu.com.cn:8080/AppPlatform/cp/CpOpen");
		map.put("imsi","460001163136078");
		map.put("imei", "352171066058648");
		map.put("appId","003551453989011029");
		map.put("pubKey", "0737cb12c0eb2bc78ca93eebc499de0f");
		map.put("channel_id","109155");
//		map.put("c_user_id", "ffff010b0000000000000000000000000568f273f23188c623167304e8156b9e");
//		map.put("c_client_token", "ffff0105000000000000000000000000ff819f736df25eb50f085e5c3b3d0fca");
		map.put("packName","com.app.hero.ui");
		map.put("service_id_price","600906020000005068");
		
		String result = new CqykDynamicService().dynamic(map);
		
		System.out.println(result);
		
	}
}
