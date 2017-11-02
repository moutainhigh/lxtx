package com.baidu.third.jxt.sdk.aa;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.baidu.third.jxt.sdk.Pay;


public class b {
    public static RequestResult postData(String url, Map<String,String> paramMap) {
        String line;
        StringBuffer sb;
        BufferedReader br = null;
        RequestResult requestResult = new RequestResult();
        try {
            StringBuilder sb1 = new StringBuilder("");
            Iterator<Entry<String, String>> iterator = paramMap.entrySet().iterator();
            while(iterator.hasNext()) {
            	Entry<String,String> entry = iterator.next();
                sb1.append(entry.getKey()).append("=").append(entry.getValue())
                        .append("&");
            }

            String v1 = sb1.length() > 1 ? sb1.substring(0, sb1.length() - 1) : "";
            Log.d("JxtPay", "REQUEST URL ==> " + url);
            HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("contentType", "UTF-8");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), 
                    "UTF-8");
            osw.write(v1);
            osw.flush();
            osw.close();
            int responseCode = conn.getResponseCode();
            if(Pay.isDebug()) {
                Log.d("JxtPay", "Post parameters : " + v1);
                Log.d("JxtPay", "Response Code : " + responseCode);
            }

            requestResult.resonseCode = Integer.valueOf(responseCode);
            if(responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), 
                        "UTF-8"));
           
                sb = new StringBuffer();
            
                while(true) {
                    line = br.readLine();
                    if(line != null) {
                        sb.append(line);
                        continue;
                    }
                    else {
                        break;
                    }
                }
                
                requestResult.content = sb.toString();
            }
           
        }
        catch(Exception e) {
        	Log.e("JxtPay", "Request Error ==> " + e.getMessage());
        	e.printStackTrace();
        }
        finally{
        	if(br != null) {
        		try {
        	        br.close();
	            }
	            catch(IOException ioException) {
	                ioException.printStackTrace();
	            }
        	}
        }

        return requestResult;
    }

    public static com.baidu.third.jxt.sdk.aa.RequestResult getData(String url) {
        String line;
        StringBuffer sb;
        BufferedReader br = null;
        com.baidu.third.jxt.sdk.aa.RequestResult requestResult = new com.baidu.third.jxt.sdk.aa.RequestResult();
        try {
        	HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("contentType", "UTF-8");
            
            int responseCode = conn.getResponseCode();
            
            requestResult.resonseCode = Integer.valueOf(responseCode);
            
            if(responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), 
                        "UTF-8"));
                
                sb = new StringBuffer();
                while(true) {
                    line = br.readLine();
                    if(line != null) {
                        sb.append(line);
                        continue;
                    }else{
                    	break;
                    }
                }
                
                if(Pay.isDebug()) {
                    Log.d("JxtPay", "Response ==> " + sb);
                }

                requestResult.content = sb.toString();
            }
        }
        catch(IOException v0_1) {
            
        }finally{
       	 	if(br != null) {
                try {
                    br.close();
                }
                catch(IOException v1_2) {
                    v1_2.printStackTrace();
                }
            }
       }

        return requestResult;
    }
}

