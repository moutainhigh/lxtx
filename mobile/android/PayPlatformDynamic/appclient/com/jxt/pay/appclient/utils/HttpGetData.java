package com.jxt.pay.appclient.utils;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.json.JSONTokener;

public class HttpGetData {

	public static JSONObject getDataJson(String url){
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(url);
		
		
		try {
			HttpResponse res = httpClient.execute(httpGet);
		
			 if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				 HttpEntity entity = res.getEntity();
				 return new JSONObject(new JSONTokener(new InputStreamReader(entity.getContent(), HTTP.UTF_8)));
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//关闭连接 ,释放资源
			httpClient.getConnectionManager().shutdown();
		} 
		
		return null;
	}
	
	public static void main(String[] args){
		
		String url = "http://112.74.106.240:7878/port/SInit/";
		String json = "{\"appid\":\"5151017\",\"imsi\":\"460003183334151\",\"imei\":\"990002912002356\",\"ua\":\"HTCOne\"}";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appid", "5151017"));
	 	params.add(new BasicNameValuePair("imsi", "460003183334151")); 
	 	params.add(new BasicNameValuePair("imei", "990002912002356")); 
	 	params.add(new BasicNameValuePair("ua", "HTCOne")); 
		
	 	url += URLEncodedUtils.format(params, HTTP.UTF_8);
	 	
	 	JSONObject jo = getDataJson(url);
		
	 	System.out.println(jo.toString());
	 	
//		String response = GetData.getDataJson(url, json);
//		
//		System.out.println(response);
	}
}
