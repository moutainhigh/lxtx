package com.baidu.alipay.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;


import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.NetWorkUtils;
import com.baidu.alipay.UpThread;
import com.baidu.crypto.AesCrypto;

public class MyPost {
    private static final String TAG = "MyPost";

    public String PostData(Context context, byte[] byt, String url_) {
    	
    	byte[] bytes = postDataBytes(context, byt, url_);
    	
    	if(bytes != null && bytes.length > 0){

        	try {
				return new String(AesCrypto.decrypt(bytes, UpThread.password),
				        "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	
    	}
    	
    
    	return null;
    }
    
    public String PostDataCommon(Context context, byte[] byt, String url_) {
    	byte[] bytes = postDataBytes(context, byt, url_);
    	
    	if(bytes != null && bytes.length >= 0){
    		if(bytes.length == 0){
    			return "";
    		}else{
	    		try {
					return new String(bytes, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    		}
    	}
    	return null;
    }
    
    public byte[] postDataBytes(Context context, byte[] byt, String url_) {
    	String result = null;
        HttpURLConnection httpUrlConnection = null;
        InputStream inStrm = null;
        ByteArrayOutputStream baos = null;
        BufferedInputStream bis = null;
        
        try {
            httpUrlConnection = NetWorkUtils
                    .getHttpURLConnection(context, url_);

            httpUrlConnection.setAllowUserInteraction(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-type",
                    "application/x-java-serialized-object");
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setConnectTimeout(20000);
            OutputStream outStrm = httpUrlConnection.getOutputStream();

            outStrm.write(byt);
            outStrm.flush();
            outStrm.close();

            inStrm = httpUrlConnection.getInputStream();
            
            baos = new ByteArrayOutputStream();
            
            bis = new BufferedInputStream(inStrm);
            byte[] buf = new byte[1024];
            int readSize = -1;
            
            while ((readSize = bis.read(buf)) != -1) {
            	baos.write(buf, 0, readSize);
            }
            
            byte[] data = baos.toByteArray();

            return data;
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception: " + e.toString());
            e.printStackTrace();
            result = null;
        } finally {
        	
        	if(baos != null){
        		try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(bis != null){
        		try {
					bis.close();
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
        	
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
                httpUrlConnection = null;
            }

            System.gc();
        }
      
        return null;
    }

}
