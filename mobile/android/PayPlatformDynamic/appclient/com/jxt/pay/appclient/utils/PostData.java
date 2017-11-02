package com.jxt.pay.appclient.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.lang.String;

import org.apache.commons.httpclient.NameValuePair;

public class PostData {
    private static final String TAG = "PostData";

    private String result = null;

    public PostData() {
    }

    public String PostData(byte[] byt,String url_,List<NameValuePair> nameValuePairs){
    	HttpURLConnection httpUrlConnection = null;
    	OutputStream outStrm = null;
    	InputStream inStrm = null;
    	ByteArrayOutputStream baos = null;
    	
    	try {
            URL url = new URL(url_);

            URLConnection rulConnection = url.openConnection();
            httpUrlConnection = (HttpURLConnection) rulConnection;

            httpUrlConnection.setAllowUserInteraction(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-type",
            		"text/xml;charset=utf-8");
//                    "application/x-java-serialized-object");
            
            if(nameValuePairs != null && nameValuePairs.size() > 0){
            	for(NameValuePair pair : nameValuePairs){
            		httpUrlConnection.setRequestProperty(pair.getName(),pair.getValue());
            	}
            }
            
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setConnectTimeout(10000);
            outStrm = httpUrlConnection.getOutputStream();

            outStrm.write(byt);
            outStrm.flush();

            inStrm = httpUrlConnection.getInputStream();
            int read = -1;
            baos = new ByteArrayOutputStream();
            while ((read = inStrm.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
           
//            result = Base64Util.encode(data);
            result = new String(data,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }finally{
        	if(outStrm != null){
        		try {
					outStrm.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(inStrm != null){
        		try {
					inStrm.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(httpUrlConnection != null){
        		httpUrlConnection.disconnect();
        		
        		httpUrlConnection = null;
        	}
        	
        	if(baos != null){
        		 try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }

        return result;
    }
    
    public String PostData(byte[] byt, String url_) {
    	return PostData(byt,url_,null);
    }
    
    public static void main(String[] args){
    	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><comic_ctcc><exinfo><behavior>order</behavior><trade_status>0</trade_status><trade_no>05171346390300435470</trade_no><mobile>15388719473</mobile><provinceid>0018</provinceid><product_id>B001eZ</product_id><product_name>\u5e78\u8fd0\u793c\u5305</product_name><app_name>\u5168\u6c11\u9177\u8dd1</app_name><price>10</price><app_id>dm_open_3599678278</app_id><ex_datetime>null</ex_datetime><content>131000N2000001B001eZ00100000120121212</content><chid>11802115001</chid><rep_times>1</rep_times></exinfo></comic_ctcc>";
    	String url = "http://115.28.52.43:9020/pay/synch/xmlSynch/zqadm";
    	
    	url = "http://localhost:8080/pay/synch/test.do";
    	
    	new PostData().PostData(xml.getBytes(), url);
    }

}