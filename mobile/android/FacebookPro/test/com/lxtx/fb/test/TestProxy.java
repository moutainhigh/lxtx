package com.lxtx.fb.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;

public class TestProxy {

	public static void main(String args[]) {
		StringBuffer sb = new StringBuffer();
		// 创建HttpClient实例
		HttpClient client = getHttpClient();
		// 创建httpGet
		HttpGet httpGet = new HttpGet("http://g.sjtuqypx.com/test.php");
		// 执行
		try {
			HttpResponse response = client.execute(httpGet);

			HttpEntity entry = response.getEntity();

			if (entry != null) {
				InputStreamReader is = new InputStreamReader(entry.getContent());
				BufferedReader br = new BufferedReader(is);
				String str = null;
				while ((str = br.readLine()) != null) {
					sb.append(str.trim());
				}
				br.close();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(sb.toString());
	}

	// 设置代理
	public static HttpClient getHttpClient() {  
	     DefaultHttpClient httpClient = new DefaultHttpClient();  
	     String proxyHost = "185.173.34.130";  
	     int proxyPort = 50054;  
	     String userName = "leoliu";  
	     String password = "ydAtuXysU";  
	     httpClient.getCredentialsProvider().setCredentials(  
	       new AuthScope(proxyHost, proxyPort),  
	       new UsernamePasswordCredentials(userName, password));  
	     HttpHost proxy = new HttpHost(proxyHost,proxyPort);  
	     httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);  
	     return httpClient;  
    }
}
