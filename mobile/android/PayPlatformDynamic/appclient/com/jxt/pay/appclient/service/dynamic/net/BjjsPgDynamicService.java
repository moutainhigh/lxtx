package com.jxt.pay.appclient.service.dynamic.net;

import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;

import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.PostParamsData;

public class BjjsPgDynamicService implements IDynamicService{

	private static final String TYPE = "bjjsPg";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
		}else if(THENO_2.equals(theNo)){
			return secondDynamic(map);
		}
			
		return null;
	}
	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
		
			NameValuePair[] params = new NameValuePair[2];
			
			params[0] = new NameValuePair("mobile",map.get("mobile"));
			params[1] = new NameValuePair("jinbi",map.get("jinbi"));
			
			String responseHtml = new PostParamsData().postData(url, params);
		
			
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String,String> map){
		
		return null;
	}
	
	public static void main(String[] args){
		test3();
	}
	
	private static void test1(){
		
		String responseHtml = GetData.getData("http://113.11.195.6:6565/pcgame/");
		
		System.out.println(responseHtml);
	}
	
	private static void test2(){
		NameValuePair[] params = new NameValuePair[4];
		
		params[0] = new NameValuePair("mobile","13811155779");
		params[1] = new NameValuePair("jinbi","000086299001");
		params[2] = new NameValuePair("time","1415692834");
		params[3] = new NameValuePair("gameid","e9df36c6f33d74035bc4a9e8d27abd7f312af9c7");
		
		String url = "http://113.11.195.6:6565/pcgame/e.php";
		
		String responseHtml = new PostParamsData().postData(url, params);
		
		System.out.println(responseHtml);
	}
	
	private static void test3(){
		NameValuePair[] params = new NameValuePair[4];
		
		params[0] = new NameValuePair("oid","0300000f423af60b0004407f");
		params[1] = new NameValuePair("vid","135658");
		params[2] = new NameValuePair("time","1415692834");
		params[3] = new NameValuePair("gameid","91761af654781e189d7c34d5d31a50fdfb4f2c68");
		
		String url = "http://113.11.195.6:6565/pcgame/f.php";
		
		String responseHtml = new PostParamsData().postData(url, params);
		
		System.out.println(responseHtml);
	}
}
