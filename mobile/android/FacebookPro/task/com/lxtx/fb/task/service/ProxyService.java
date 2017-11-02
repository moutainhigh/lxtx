package com.lxtx.fb.task.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
import org.openqa.selenium.WebDriver;

import com.lxtx.fb.handler.ProxyHandler;
import com.lxtx.fb.pojo.Languages;
import com.lxtx.fb.pojo.Proxy;
import com.lxtx.fb.pojo.User;
import com.lxtx.fb.task.util.WebDriverFactory;

public class ProxyService {

	public boolean exec(){
		
		List<Proxy> list = proxyHandler.listUnCheck();
		
		if(list != null && list.size() > 0){
			
			for(Proxy proxy : list){
				
				boolean valid = checkProxy(proxy);
				
				proxy.setStatus(valid ? 1 : -1);
				
				proxyHandler.updateStatus(proxy);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean checkProxy(Proxy proxy) {
		StringBuffer sb = new StringBuffer();
		// 创建HttpClient实例
		HttpClient client = getHttpClient(proxy);
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

			if(sb.toString().contains(proxy.getIp())){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	// 设置代理
	private HttpClient getHttpClient(Proxy ipProxy) {  
	     DefaultHttpClient httpClient = new DefaultHttpClient();  
	     String proxyHost = ipProxy.getIp();  
	     int proxyPort = ipProxy.getPort1();  
	     String userName = ipProxy.getUserName();  
	     String password = ipProxy.getUserPass();  
	     httpClient.getCredentialsProvider().setCredentials(  
	       new AuthScope(proxyHost, proxyPort),  
	       new UsernamePasswordCredentials(userName, password));  
	     HttpHost proxy = new HttpHost(proxyHost,proxyPort);  
	     httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);  
	     return httpClient;  
    }
	
	public static void main(String[] args){
		ProxyService service = new ProxyService();
		
		Proxy proxy = new Proxy();
		proxy.setIp("185.173.34.131");
		proxy.setPort1(50054);
		proxy.setUserName("louis");
		proxy.setUserPass("yWYqE6ARu");
		
		System.out.println("proxy valid : "+service.checkProxy(proxy));
	}
	
	//ioc
	private ProxyHandler proxyHandler;

	public void setProxyHandler(ProxyHandler proxyHandler) {
		this.proxyHandler = proxyHandler;
	}

}
