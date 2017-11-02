package com.jxt.pay.appclient.utils;

import java.net.URL;
import java.security.Security;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;


public class HttpClientUtils {
	private String url;
	private String method;
	private String param;
	private NameValuePair[] nameValuePairs;
	
	public HttpClientUtils(String url,String method,String param,NameValuePair[] nameValuePairs){
		this.url = url;
		this.method = method;
		this.param = param;
		this.nameValuePairs = nameValuePairs;
	}
	
    public String send() {  
        String body = "error";  
        
        HttpClient client = new HttpClient();
        
        HttpMethod httpMethod = null;  
        if (method.equalsIgnoreCase("post")) {  
            httpMethod = new PostMethod(url); // 输入网址  
        } else {
        	httpMethod = new GetMethod(url); // 输入网址  
        }  
        
        try {  
            if (url.startsWith("https:")) {  
                supportSSL(url, client);  
            }  
            client.getParams().setContentCharset("UTF-8"); // 处理中午字符串  
            
            
            client.getParams().setParameter("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 1.7; .NET CLR 1.1.4322; CIBA; .NET CLR 2.0.50727)");
            
            client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY); 
            client.getParams().setParameter("http.protocol.single-cookie-header", true); 
          
	        if(param != null && param.length() > 0){
	        	((GetMethod)httpMethod).setQueryString(param);
	        }else if(nameValuePairs != null && nameValuePairs.length > 0){
	        	((PostMethod)httpMethod).setRequestBody(nameValuePairs);
	        }
	        
            int code = client.executeMethod(httpMethod);  
           
            {
            	body = httpMethod.getResponseBodyAsString();  
            }
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return body;  
    }  
    
      
    private static void supportSSL(String url, HttpClient client) {  
        if(StringUtils.isBlank(url)) {  
            return;  
        }  
        String siteUrl = StringUtils.lowerCase(url);  
        if (!(siteUrl.startsWith("https"))) {  
            return;  
        }  
         
        try {  
        	setSSLProtocol(siteUrl, client);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        Security.setProperty( "ssl.SocketFactory.provider","com.tool.util.DummySSLSocketFactory");  
    }  
      
    private static void setSSLProtocol(String strUrl, HttpClient client) throws Exception {          
        URL url = new URL(strUrl);  
        String host = url.getHost();  
        int port = url.getPort();  
  
        if (port <= 0) {  
            port = 443;  
        }  
        ProtocolSocketFactory factory = new DummySSLSocketFactory();  
        Protocol authhttps = new Protocol("https", factory, port);  
        Protocol.registerProtocol("https", authhttps);  
        client.getHostConfiguration().setHost(host, port, authhttps);  
    }  
}
