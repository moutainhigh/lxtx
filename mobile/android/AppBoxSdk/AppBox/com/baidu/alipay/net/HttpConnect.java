package com.baidu.alipay.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.NetWorkUtils;
import com.baidu.alipay.Tools;
import com.baidu.alipay.script.SendSms;

public class HttpConnect {
    private static final String TAG = "HttpConnect";
    private Context context;
    HttpURLConnection conn = null;
    String method = null;
    String referer = null;
    
    public boolean isProxy = false;

    public String result = "";

    public String size;

    public String ctrlData = "";
    public String Content_Type = "application/x-www-form-urlencoded; charset=UTF-8";
    public String Content_Language = "zh-cn,zh;q=0.5";
    public String Accept_Encoding = "gzip, deflate";
    public String Accept_Charset = "ISO-8859-1, US-ASCII, UTF-8; Q=0.8, IS0-10646-UCS-2; Q=0.6";
    public String Accept = "application/vnd.wap.wmlscriptc, text/vnd.wap.wml, application/vnd.wap.xhtml+xml, application/xhtml+xml, text/html, multipart/mixed, */*, text/x-vcard, text/x-vcalendar,image/pn, image/gif,image/*, image/vnd.wap.wbmp";
    public String Connection = "keep-alive";

    String host = null;
    String port = null;
    String cookiePath = "/";

    public void Init(String url_, String method_) {
        method = method_;
        String destUrl = url_;

        try {
            LogUtil.e(TAG, "get 方法中HttpConnect的request: " + destUrl);
            conn = NetWorkUtils.getHttpURLConnection(context, destUrl, isProxy);

            URL _URL = conn.getURL();
            
            host = _URL.getHost();
            int portInt = _URL.getPort();
            
            if(portInt == -1){
            	port = "80";
            }else{
            	port = portInt+"";
            }
            
            String requestPath = _URL.getPath();
            if(requestPath != null && requestPath.length() > 0){
            	int pos = requestPath.indexOf("/",1);
            	if(pos > 0){
            		cookiePath = requestPath.substring(0,pos+1);
            	}
            }
            
            conn.setUseCaches(false);
            if(!method.equals("GET")){
            	conn.setDoOutput(true);// 开启输入输出
            }
            conn.setDoInput(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpConnect(Context context) {
        this.context = context;
    }

    public void InitHeader(SendSms sendSms,String ua_, String referer_, String setHeader,
            int length_) throws IOException {

        // ------------------------------------------------------------
        conn.setRequestProperty("User-Agent", ua_); // ������ģ��UA
        // ------------------------------------------------------------
        LogUtil.e(TAG, "method~~" + method);
        
        if(method.equals("GET")){
        	conn.setRequestMethod(method);
        }else{
        	conn.setRequestMethod("POST");
        }
        
        if (method.equals("POST")
                && (setHeader == null || setHeader.length() == 0)) {
            conn.setRequestProperty("Content-Type",
                    "application/x-java-serialized-object");
        }else if(method.equals("POSTS")){
             conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        }

//        if (lastCookies != "" && lastCookies != null) // ����cookie
//        {
//            LogUtil.e(TAG, "lastCookies~~lastCookies");
//            conn.setRequestProperty("Cookie", getLastResponseCookie());
//        }
        
        conn.setRequestProperty("Host", host);
        conn.setRequestProperty("Cookie", getCookies(sendSms,host,cookiePath));
        
        if (referer_ != null && !"".equals(referer_.trim())) {
            conn.setRequestProperty("Referer", referer_);
        } else if (sendSms.lasturl != null) {
            conn.setRequestProperty("Referer", sendSms.lasturl);
        }
        conn.setRequestProperty("Content-Language", Content_Language);
        LogUtil.e(TAG, "Content_Language~~" + Content_Language + "");
        conn.setRequestProperty("Accept", Accept);
        conn.setRequestProperty("Accept-Charset", Accept_Charset);
        conn.setRequestProperty("Keep-Alive", "115");
        conn.setRequestProperty("Connection", Connection);
        if (length_ != 0) {
            conn.setRequestProperty("RANGE", "bytes=0-" + (length_ * 1024));
        }
        if (setHeader != null && setHeader.length() > 0) {
            String[] setHeaderList = Tools.split(setHeader, "&");
            for (int i = 0; i < setHeaderList.length; i++) {
                String Temp = setHeaderList[i];
                String[] TempList = Tools.split(Temp, ":");
                if (TempList.length > 1) {
                    conn.setRequestProperty(TempList[0],
                            Tools.repString(TempList[1], "&amp;", "&"));
                    LogUtil.e( TAG , "post set header key=" + TempList[0] + "~~value=" + Tools.repString(TempList[1], "&amp;", "&") + "");
                }
            }
        }

    }

    public byte[] request(SendSms sendSms,String url_, String method_, String referer_,
            String ua_, String data_, String setHeader, int length_)
            throws IOException {
    	return request(sendSms, url_, method_, referer_, ua_, data_, setHeader, length_,true);
    }
    
    public byte[] request(SendSms sendSms,String url_, String method_, String referer_,
            String ua_, String data_, String setHeader, int length_, boolean isDomain)
            throws IOException {
        byte[] array = null;

        if ("get".equals(method_.toLowerCase())) {
            if (data_ != null && !data_.equals("")) {
                if (url_.indexOf("?") < 0) {
                    url_ = url_ + "?" + data_;
                } else {
                    if (url_.endsWith("?")) {
                        url_ = url_ + data_;
                    } else {
                        url_ = url_ + "&" + data_;
                    }
                }
            }
        }
        if (url_.endsWith("?") || url_.endsWith("&")) {
            url_ = url_.substring(0, url_.length() - 1);
        }

        Init(url_, method_);
        InitHeader(sendSms, ua_, referer_, setHeader, length_);

        if (data_ != null) {
            byte[] postData = data_.getBytes();
            if ("post".equals(method_.toLowerCase())) {
                conn.setRequestProperty("Content-Length", Integer
                        .toString(postData != null ? postData.length : 0));
            }
            OutputStream outputstream = conn.getOutputStream();
            outputstream.write(postData);
            postData = null;
            outputstream.close();
            LogUtil.e(TAG, "post method set data~~ " + data_ + "");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            LogUtil.e(TAG, "result~~~~1~~~~~~");
            String contentEncoding = null;

            contentEncoding = conn.getHeaderField("content-encoding");
            if (contentEncoding == null)
                contentEncoding = conn.getHeaderField("Content-Encoding");
            if (contentEncoding != null
                    && (contentEncoding.indexOf("gzip") >= 0 || contentEncoding
                            .indexOf("GZIP") >= 0)) {
                in = new DataInputStream(new GZipInputStream(
                        conn.getInputStream(), 1024, GZipInputStream.TYPE_GZIP,
                        true));
                // in = conn.getInputStream();
            } else {
//                LogUtil.e(TAG, "result~~~2~~~~~~");
//                 conn.connect();
                in = conn.getInputStream();
            }
//            LogUtil.e(TAG, "result~~~~3~~~~~~");

            int readNum = 1024;
            byte[] b = new byte[readNum];
            int num = 0;
            do {
                num = in.read(b);
                if (num < 0)
                    break;
                if (length_ > 0 && baos.size() >= length_ * 1024)
                    break;
                if (baos.size() > 81920)
                    break;
                baos.write(b, 0, num);
            } while (true);
            
            b = null;
        } catch (Exception ex) {
            // in.close();
            ex.printStackTrace();
            return null;
        }finally{
        	if(in != null){
	        	try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	            in = null;
        	}
        }
        int responseCode = conn.getResponseCode();
        LogUtil.e(TAG, "这是返回的状态码:" + responseCode + "");
        
        if(isDomain){
	        sendSms.lastContentType = conn.getHeaderField("Content-Type");
	        if (sendSms.lastContentType != null) {
	        	sendSms.lastContentType = sendSms.lastContentType.toLowerCase();
	        }
        }
        
//        String header = "\r\n";
//        header += "responseCode:" + responseCode + "\r\n";
//        for (int i = 0; conn.getHeaderField(i) != null; i++) {
//            header += conn.getHeaderField(i);
//        }
//        byte[] headerArray = header.getBytes();
//        baos.write(headerArray, 0, headerArray.length);
        
        array = baos.toByteArray();
        baos.close();
        baos = null;

        String nextLocation = null;
        nextLocation = conn.getHeaderField("content-base");
        if(isDomain){
        	sendSms.lasturl = conn.getURL().toString();
        }
        
        filterCookies(sendSms,conn);
        
        size = conn.getHeaderField("Content-Length");
        if (nextLocation == null)
            nextLocation = conn.getHeaderField("Content-Base");

        if (responseCode == 302 || responseCode == 301) {
            if (nextLocation == null || nextLocation.equals("")) {
                nextLocation = conn.getHeaderField("Location");
                if (nextLocation == null)
                    nextLocation = conn.getHeaderField("location");
            }

        } else if ((responseCode == 200 || responseCode == 206)
                && (sendSms.lastContentType != null && (sendSms.lastContentType.indexOf("text") >= 0
                        || sendSms.lastContentType.indexOf("wml") >= 0 || sendSms.lastContentType
                        .indexOf("html") >= 0))) {

            if (nextLocation == null || nextLocation.equals("")) {
                try {
                    String temp = Tools.byteToUTF8String(array);
                    nextLocation = wmlRedirctUrl(temp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (nextLocation != null && nextLocation.length() > 0) {
            conn.disconnect();
            conn = null;
            return request(sendSms,nextLocation, "GET", url_, ua_, "", "", 0);
        }
        conn.disconnect();
        conn = null;

        return array;

    }

    public String wmlRedirctUrl(String wml) {
        if (wml == null)
            return "";
        String data = wml;
        // int cardIndex = data.indexOf("<card ");
        // int ontimerIndex = data.indexOf(" ontimer=", cardIndex);
        // int tmp = data.indexOf("<timer ", ontimerIndex);
        // int tmp0 = data.indexOf(" value=", tmp);
        // if (cardIndex > 0 && ontimerIndex > 0 && tmp > 0 && tmp0 > 0) {
        //
        // char c = data.charAt(ontimerIndex + " ontimer=".length());
        // int startIndex = ontimerIndex + " ontimer=".length() + 1;
        // int endIndex = data.indexOf(c, startIndex);
        // String nextLocation = data.substring(startIndex, endIndex);
        // data = null;
        // return nextLocation;
        // }
        String nextLocation = "";
        int cardIndex = data.indexOf("<onevent ");
        int ontimerIndex = data.indexOf(
                " type=" + '"' + "onenterforward" + '"', cardIndex);
        int tmp = data.indexOf("<go ", ontimerIndex);
        int tmp0 = data.indexOf(" href=", tmp);
        if (cardIndex > 0 && ontimerIndex > 0 && tmp > 0 && tmp0 > 0) {

            char c = data.charAt(tmp0 + " href=".length());
            int startIndex = tmp0 + " href=".length() + 1;
            int endIndex = data.indexOf(c, startIndex);
            nextLocation = data.substring(startIndex, endIndex);

            if (!nextLocation.startsWith("http://"))
                nextLocation = "http://" + host
                        + (port != null ? ":" + port : "") + nextLocation;

        }

        cardIndex = data.indexOf("<card ");
        ontimerIndex = data.indexOf(" onenterforward=" + '"', cardIndex);
        if (cardIndex > 0 && ontimerIndex > 0) {

            int startIndex = ontimerIndex + (" onenterforward=" + '"').length();
            int endIndex = data.indexOf('"', startIndex);
            nextLocation = data.substring(startIndex, endIndex);
            data = null;

            if (!nextLocation.startsWith("http://"))
                nextLocation = "http://" + host
                        + (port != null ? ":" + port : "") + nextLocation;

        }
        return Tools.repString(nextLocation, "&amp;", "&");
    }
    
    public static void filterCookies(SendSms sendSms,HttpURLConnection conn){
    	Map<String, List<String>> map = conn.getHeaderFields();
        List<String> list = map.get("Set-Cookie");
        
        try {
            if (list != null && list.size() > 0) {
            	URL _URL = conn.getURL();
            	String domain = _URL.getHost(); 
                for (int j = 0; j < list.size(); j++) {
                    LogUtil.e(TAG, "list header~~~~" + list.get(j).toString());
                    String header = list.get(j).toString();
                    
                    Cookie cookie = Cookie.parse(header);
                    cookie.setDomain(domain);
                    
                    sendSms.cookieMap.put(cookie.getKey(), cookie);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getCookies(SendSms sendSms,String domain,String cookiePath){
    	StringBuffer sb = new StringBuffer();
    	
    	for(Cookie cookie : sendSms.cookieMap.values()){
    		if(domain.contains(cookie.getDomain()) && cookiePath.contains(cookie.getPath())){
    			sb.append(cookie.toString()).append("; ");
    		}
    	}
    	
    	if(sb.length() == 0){
    		return "";
    	}
    	
    	return sb.substring(0,sb.length() - 2);
    }
}