package com.lxtx.fb.task.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

public class GetData {
	
	private static Logger logger = Logger.getLogger(GetData.class);
	
	public static String getData(String url,String param){
		return getData(url,param,new ArrayList<NameValuePair>());
	}
	
	public static String getData(String url,String param,List<NameValuePair> pairList){
		if(url != null && url.length() > 0){
			if(url.indexOf("?") > 0){
				url = url + "&"+param;
			}else{
				url = url+"?"+param;
			}
			
			return getData(url,pairList);
		}
		
		return null;
	}
	
	public static String getData(String url,List<NameValuePair> pairList){
		logger.info(url);
		
		HttpClient httpClient = new HttpClient();
		
		GetMethod method = new GetMethod(url);
		
		try {
			httpClient.setConnectionTimeout(30000);
			httpClient.setTimeout(30000);
			
			if(pairList != null && pairList.size() > 0){
				for(NameValuePair pair : pairList){
            		method.setRequestHeader(pair.getName(), pair.getValue());
            	}
			}
			
			int ret = httpClient.executeMethod(method);
			
			if(ret == 200){
//				long contentLen = method.getResponseContentLength();
				
//				System.out.println("contentLen:"+contentLen);
				
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
	
	public static String getData(String url){
		return getData(url,new ArrayList<NameValuePair>());
	}

	public static void main(String[] args){
		
		String url = "http://115.28.52.43:9020/pay/manage/cp2/dabaoTask!getOne.do?cpId=294";
		
		url = "http://114.55.86.82:8080/DownLoad/download_player.jsp?chan=105302";
		
		url = "http://jf.fzlmc.com/jf/wx.txt";
		
		System.out.println(getData(url));
		
	}
}
