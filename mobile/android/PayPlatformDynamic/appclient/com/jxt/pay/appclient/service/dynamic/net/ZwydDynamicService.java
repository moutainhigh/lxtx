package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

//卓望阅读
public class ZwydDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(ZwydDynamicService.class);
	
	@Override
	public String getType() {
		return "zwyd";
	}

	private static Map<String, String> verifyMap = new HashMap<String, String>();
	
	@Override
	public String dynamic(final Map<String, String> map) {
		logMap(map);
		String optype = map.get("optype");
		
		if("fee".equals(optype)){
			new Thread(){
				public void run(){
					try {
						fee(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}else if("verify".equals(optype)){
			verify(map);
		}
		
		return "<wait>1</wait>";
	}
	
	private void logMap(Map<String,String> map){
//		for(String key : map.keySet()){
//			logger.info(key+":"+map.get(key));
//		}
	}
	
	private void verify(Map<String,String> map){
		String mobile = map.get("mobile");
		String verify = map.get("verify");
		
		verifyMap.put(mobile,verify);
	}

	private void fee(Map<String,String> map) throws Exception{
		logMap(map);
		
//		String url = "http://wap.cmread.com/r/394781652/394781719/index.htm?cm=M3700008";
		String url = map.get("url");
		String mobile = map.get("mobile");
		String password = getPassword(mobile);
		
		logger.info("fee:"+url+";"+mobile+";"+password);
		
		String method = "get";
		Header[] headers = null; 
		Cookie[] cookies = null;
		String param = "";
		NameValuePair[] nameValuePairs = null;
		
		ZwydHttpClientUtils utils = new ZwydHttpClientUtils(url, method, headers, cookies, param, nameValuePairs);
	
		String result1 = utils.send();
		Cookie[] cookies1 = utils.getReturnCookies();
		String referrer1 = utils.getUrl1();
		
//		logger.info(result1);
		
		if(result1.contains("账号登录")){
			String action = index(result1,"action=\"","\"");
			action = action.replace("&amp;","&");
			
			String url1 = "https://wap.cmread.com"+action;
			
			headers = new Header[1];
			headers[0] = new Header("Referer", referrer1);
			
			NameValuePair[] nameValuePairs1 = new NameValuePair[4];
			nameValuePairs1[0] = new NameValuePair("accountname", mobile);
			nameValuePairs1[1] = new NameValuePair("password", password);
			nameValuePairs1[2] = new NameValuePair("rememberUname","on");
			nameValuePairs1[3] = new NameValuePair("s_loginbypass","%E9%A9%AC%E4%B8%8A%E7%99%BB%E5%BD%95");
			
			ZwydHttpClientUtils utils2 = new ZwydHttpClientUtils(url1, "post", headers, cookies1, param, nameValuePairs1);
			
			String result2 = utils2.send();
			Cookie[] cookies2 = utils2.getReturnCookies();
			String referer2 = utils2.getUrl1();
			
//			logger.info(result2);
			
			if(result2 != null && result2.contains("已关联咪咕账号")){
				String aaa1 = index(result2,"已关联咪咕账号","a href=\"","\"");
				aaa1 = aaa1.replace("&amp;","&");
				
				headers[0] = new Header("Referer",referer2);
				
				utils2 = new ZwydHttpClientUtils("https://wap.cmread.com"+aaa1, "get", headers, cookies2, param, null);
				
				result2 = utils2.send();
				cookies2 = utils2.getReturnCookies();
				referer2 = utils2.getUrl1();
			}
			
			if(result2 != null && result2.contains("获取验证码")){
				String aaa2 = index(result2,"<form action=\"","\"");
				aaa2 = aaa2.replace("&amp;","&");
				String url3 = "http://wap.cmread.com"+aaa2;
				
				headers[0] = new Header("Referer",referer2); 
				
				NameValuePair[] nameValuePairs3 = new NameValuePair[3];
				nameValuePairs3[0] = new NameValuePair("pt", "1");
				nameValuePairs3[1] = new NameValuePair("s_smscode", "");
				nameValuePairs3[2] = new NameValuePair("s_getsmscode","%E8%8E%B7%E5%8F%96%E9%AA%8C%E8%AF%81%E7%A0%81");
				
				ZwydHttpClientUtils utils3 = new ZwydHttpClientUtils(url3, "post", headers, cookies2, param, nameValuePairs3);
				String result3 = utils3.send();
				Cookie[] cookies3 = utils3.getReturnCookies();
				String referer3 = utils3.getUrl1();
				
				aaa2 = index(result3,"<form action=\"","\"");
				aaa2 = aaa2.replace("&amp;", "&");
				String url4 = "http://wap.cmread.com"+aaa2;
			
				//waiting for verifycode
				String verifyCode = getVerifyCode(mobile);
				headers[0] = new Header("Referer",referer3);
				
				NameValuePair[] nameValuePairs4 = new NameValuePair[3];
				nameValuePairs4[0] = new NameValuePair("pt", "1");
				nameValuePairs4[1] = new NameValuePair("s_smscode", verifyCode);
				nameValuePairs4[2] = new NameValuePair("s_order", "%E7%A1%AE%E8%AE%A4%E8%AE%A2%E8%B4%AD");
				
				ZwydHttpClientUtils utils4 = new ZwydHttpClientUtils(url4, "post", headers, cookies3, param, nameValuePairs4);
				
				String result4 = utils4.send();
				Cookie[] cookies4 = utils4.getReturnCookies();
				String referer4 = utils4.getUrl1();
				
//				logger.info(result4);
				
				if(result4 != null && result4.contains("输入的验证码有误")){
					logger.info("计费失败");
				}else{
					logger.info("计费成功");
				}
			}		
		}
	}
	
	private static String[] ABCArr = new String[]{"A","B","C","D","E","F","G","H","I","J"}; 
	
	private static String getPassword(String mobile){

		String[] arr = mobile.split("");
		StringBuffer sb = new StringBuffer();
		sb.append(arr[0]);
		sb.append(arr[2]);
		sb.append(arr[4]);
		sb.append(arr[6]);
		sb.append(arr[8]);
		sb.append(arr[10]);
		sb.append(ABCArr[Integer.parseInt(arr[1])]);
		sb.append(ABCArr[Integer.parseInt(arr[3])]);
		sb.append(ABCArr[Integer.parseInt(arr[5])]);
		
		String password = sb.toString();
		
		return password;
	}
	
	public class ZwydHttpClientUtils {  

//	    private static final Logger log = Logger.getLogger(ZwydHttpClientUtils.class);  
	  
		private String url;
		private String method;
		private Cookie[] cookies;
		private String param;
		private NameValuePair[] nameValuePairs;
		private Header[] headers;
		private String url1;
		private int code;
		
		private Cookie[] returnCookies;
		
		public Cookie[] getReturnCookies(){
			return this.returnCookies;
		}
		
		public String getUrl1() {
			return url1;
		}
		
		public int getCode() {
			return code;
		}

		public ZwydHttpClientUtils(String url,String method,Header[] headers,Cookie[] cookies,String param,NameValuePair[] nameValuePairs){
			this.url = url;
			this.method = method;
			this.headers = headers;
			this.cookies = cookies;
			this.param = param;
			this.nameValuePairs = nameValuePairs;
		}
		
	    public String send() {  
	        String body = "error";  
	        
	        HttpClient client = new HttpClient();
	        
	        HttpMethod httpMethod = null;  
	        if (method.equalsIgnoreCase("post")) {  
	            httpMethod = new PostMethod(url); // 杈撳叆缃戝潃  
	        } else {
	        	httpMethod = new GetMethod(url); // 杈撳叆缃戝潃  
	        }  
	        
	        try {  
//	            if (url.startsWith("https:")) {  
//	                supportSSL(url, client);  
//	            }  
	            client.getParams().setContentCharset("UTF-8"); // 澶勭悊涓崍瀛楃涓?  
	            
	            
//	            client.getParams().setParameter("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 1.7; .NET CLR 1.1.4322; CIBA; .NET CLR 2.0.50727)");
	            client.getParams().setParameter("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.2.1;zh-cn; Lenovo_K860i/JOP40D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.2.1 Mobile Safari/534.30");
	            
	            client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY); 
	            client.getParams().setParameter("http.protocol.single-cookie-header", true); 
	          
		        if(cookies != null && cookies.length > 0){
		            client.getState().addCookies(cookies);
		        }
	        
		        if(param != null && param.length() > 0){
		        	if(httpMethod instanceof GetMethod){
		        		((GetMethod)httpMethod).setQueryString(param);
		        	}else{
		        		((PostMethod)httpMethod).setRequestContentLength(param.length());
		        		((PostMethod)httpMethod).setRequestBody(param);
		        	}
		        }else if(nameValuePairs != null && nameValuePairs.length > 0){
		        	((PostMethod)httpMethod).setRequestBody(nameValuePairs);
		        }
		        
		        if(headers != null && headers.length > 0){
		        	for(Header header : headers){
		        		httpMethod.setRequestHeader(header);
		        	}
		        }
		        
	            code = client.executeMethod(httpMethod);  
	           
	            Header locationHeader = httpMethod.getResponseHeader("location");
	            
	            returnCookies = client.getState().getCookies();
	            
	            if(locationHeader != null){
	            	this.url = locationHeader.getValue();
	            	
	            	this.method = "get";
	            	this.cookies = returnCookies;
	            	this.param = null;
	            	this.nameValuePairs = null;
	            	
	            	return this.send();
	            }else{
	            	body = httpMethod.getResponseBodyAsString();  
	            	url1 = httpMethod.getURI().getURI();
	            }
	            
	        }catch (Exception e) {  
	        	e.printStackTrace();  
	        }
	        return body;  
	    }  
	    
	      
//	    private static void supportSSL(String url, HttpClient client) {  
//	        if(StringUtils.isBlank(url)) {  
//	            return;  
//	        }  
//	        String siteUrl = StringUtils.lowerCase(url);  
//	        if (!(siteUrl.startsWith("https"))) {  
//	            return;  
//	        }  
//	         
//	        try {  
//	           setSSLProtocol(siteUrl, client);  
//	        } catch (Exception e) {  
//	            log.error("setProtocol error ", e);  
//	        }  
//	        Security.setProperty( "ssl.SocketFactory.provider","com.tool.util.DummySSLSocketFactory");  
//	    }  
//	      
//	    private static void setSSLProtocol(String strUrl, HttpClient client) throws Exception {          
//	        URL url = new URL(strUrl);  
//	        String host = url.getHost();  
//	        int port = url.getPort();  
	//  
//	        if (port <= 0) {  
//	            port = 443;  
//	        }  
//	        ProtocolSocketFactory factory = new DummySSLSocketFactory();  
//	        Protocol authhttps = new Protocol("https", factory, port);  
//	        Protocol.registerProtocol("https", authhttps);  
//	        client.getHostConfiguration().setHost(host, port, authhttps);  
//	    }  
	}  
	

	private static String index(String html,String start,String end){
		
		int pos0 = html.indexOf(start);
		int pos1 = html.indexOf(end,pos0+start.length());
		
		if(pos0 >= 0 && pos1 > 0){
			return html.substring(pos0+start.length(),pos1);
		}
		
		return "";
	}

	private static String index(String html,String keyword,String start,String end){
		
		int pos = html.indexOf(keyword);
		
		if(pos >= 0){
			int pos0 = html.indexOf(start,pos+keyword.length());
			int pos1 = html.indexOf(end,pos0+start.length());
			
			if(pos0 >= 0 && pos1 > 0){
				return html.substring(pos0+start.length(),pos1);
			}
		}
		
		return "";
	}
	
	private String getVerifyCode(String mobile){
		
		String s = null;
		
		long start = System.currentTimeMillis();
		
		while(true){
			
			s = verifyMap.get(mobile);
			
			if(s == null){
				if(System.currentTimeMillis() - start <= 60000){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					break;
				}
			}else{
				verifyMap.remove(mobile);
				return s;
			}
		}
		
		
		return "111111";
	}
	
	public static void main(String[] args){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","zwyd");
		map.put("optype","fee");
		map.put("url","http://wap.cmread.com/r/602241647/602241682/index.htm?cm=M3490001");
		map.put("mobile","13811155779");
				
		new ZwydDynamicService().dynamic(map);
	}
	
}
