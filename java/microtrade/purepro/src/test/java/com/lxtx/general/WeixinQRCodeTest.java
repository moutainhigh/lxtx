package com.lxtx.general;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;

public class WeixinQRCodeTest {
	public static void main(String[] args) {
		String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQHi8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL0UweTNxNi1sdlA3RklyRnNKbUFvAAIELdnUUgMEAAAAAA%3D%3D";
		HttpClient client = new HttpClient();
	    client.getParams().setParameter("User-Agent",
	        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 1.7; .NET CLR 1.1.4322; CIBA; .NET CLR 2.0.50727)");
	    client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
	    client.getParams().setParameter("http.protocol.single-cookie-header", true);
//		    client.getParams().setParameter("", value);
	    client.getHttpConnectionManager().getParams().setConnectionTimeout(8000);
	    client.getHttpConnectionManager().getParams().setSoTimeout(8000);

	    Map<String, String> map = new HashMap<String, String>();
	    map.put("content_type", "image/jpg");
	    map.put("http_code","200");
	    map.put("header_size","162");
	    map.put("request_size","181");
	    map.put("filetime","-1");
	    map.put("ssl_verify_result","20");
	    map.put("redirect_count","0");
	    map.put("total_time","0.509");
	    map.put("namelookup_time","0");
	    map.put("connect_time","0.058");
	    map.put("pretransfer_time","0.343");
	    map.put("size_upload","0");
	    map.put("size_download","28497");
	    map.put("speed_download","55986");
	    map.put("speed_upload","0");
	    map.put("download_content_length","28497");
	    map.put("upload_content_length","0");
	    map.put("starttransfer_time","0.481");
	    map.put("redirect_time","0");
	    
	    GetMethod method = new GetMethod(url);
	    for (String key: map.keySet()) {
	    	method.addRequestHeader(key, map.get(key));
	    }
		
	    try {
			client.executeMethod(method);
		} catch (IOException e) {
			e.printStackTrace();
		}
//	    System.out.println(WeixinQRCodeTest.getResponseString(method, "utf-8"));
	    getResponseData(method);
	}
	
	public static void getResponseData(HttpMethod method) {
		InputStream resStream = null;
	    
	    try {
			resStream = method.getResponseBodyAsStream();
			BufferedImage bi = ImageIO.read(resStream);
			FileOutputStream fos = new FileOutputStream(new File("d:/qr.png"));
            ImageIO.write((RenderedImage)bi, "GIF", fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	      
	}
	
	public static String getResponseString(HttpMethod method, String charset) {
	    InputStream resStream = null;
	    
	    try {
	      resStream = method.getResponseBodyAsStream();
	      BufferedReader br = new BufferedReader(new InputStreamReader(resStream, charset));  
	      StringBuffer resBuffer = new StringBuffer();  
	      String resTemp = "";  
	      while((resTemp = br.readLine()) != null){  
	          resBuffer.append(resTemp);  
	      }  
	      return resBuffer.toString();
	    } catch (IOException e) {
	      e.printStackTrace();
	    } finally {
	      if (resStream != null) {
	        try {
	          resStream.close();
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	    
	    return "";
	  
	  }
}
