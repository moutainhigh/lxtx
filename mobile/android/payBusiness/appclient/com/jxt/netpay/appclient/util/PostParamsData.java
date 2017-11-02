package com.jxt.netpay.appclient.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class PostParamsData {
	public static String postData(String url,NameValuePair[] params){
		
		HttpClient httpClient = new HttpClient();
		
		PostMethod method = new PostMethod(url);
		
		try {
//			method.addParameters(params);
			
			for(NameValuePair param : params){
				method.addParameter(param.getName(),param.getValue());
			}
			
			httpClient.setConnectionTimeout(20000);
			httpClient.setTimeout(20000);
			
			int ret = httpClient.executeMethod(method);
			System.out.println("ret:"+ret);
			if(ret == 200){
				String result = method.getResponseBodyAsString();
				
				return result;
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(method != null){
				method.releaseConnection();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args){
//		imsi=460013188013982&imei=860623023037516&appKey=81387&appName=泡泡龙&subject=复活&orderId=8bbb59748ce2&totalFee=1&cpParam=渠道自定义参数&cid=1000

		String url = "http://smscenter.3gshow.cn:6400/WoChannelSDK/req.ashx?company=zt";
		
		NameValuePair[]  params = new NameValuePair[5];
		
		params[0] = new NameValuePair("totalFee","1");
		params[1] = new NameValuePair("cid", "1051");
		params[2] = new NameValuePair("appKey", "1001");
		params[3] = new NameValuePair("appName", "欢乐麻将");
		params[4] = new NameValuePair("subject", "金币1");
//		params[5] = new NameValuePair("imei", "860623023037516");
//		params[6] = new NameValuePair("imsi", "460013188013982");
		
		String result = postData(url, params);
		
		System.out.println(result);
	}
}
