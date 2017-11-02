package com.heepay.Commond;

import java.io.DataInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class WeiXinDataHelper {

	public final static String UTF8Encode="UTF-8";
	public final static String GBKEncode="GBK";
	
	
	public static String GetQueryString(Map<String , String> map){
		
		Iterator<Entry<String,String>> iter=map.entrySet().iterator();
		StringBuilder sb=new StringBuilder();
		while(iter.hasNext()){
			Entry<String, String> entry=iter.next();
			Object key=entry.getKey();
			Object value=entry.getValue();
			sb.append(key+"="+value+"&");					
		}
		if(sb.length()==0) return "";	
		return sb.substring(0,sb.length()-1);
	}

	public static void TransferCharsetEncode(Map<String, String> map) throws UnsupportedEncodingException{
		for(Entry<String, String> entry:map.entrySet()){
			if(entry.getValue()==null) continue;
			String utf8=URLEncoder.encode(entry.getValue(),UTF8Encode);
			entry.setValue(utf8);
		}
	}
	
	public static String GetSortQueryToLowerString(Map<String, String> map){
		List<Map.Entry<String, String>> keyValues=new ArrayList<Map.Entry<String,String>>(map.entrySet());
		Collections.sort(keyValues,new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,Map.Entry<String,String> o2){
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<keyValues.size();i++){
			if(keyValues.get(i).getValue()==null){
				sb.append(keyValues.get(i).getKey()+"= ");
			}else{
				sb.append(keyValues.get(i).getKey()+"="+keyValues.get(i).getValue().toLowerCase());
			}
			sb.append("&");
		}
		return sb.substring(0,sb.length()-1);
	}
	
	public static String GetSortQueryString(Map<String, String> map){
		List<Map.Entry<String, String>> keyValues=new ArrayList<Map.Entry<String,String>>(map.entrySet());
		Collections.sort(keyValues,new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,Map.Entry<String,String> o2){
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<keyValues.size();i++){
			sb.append(keyValues.get(i).getKey()+"="+keyValues.get(i).getValue());
			sb.append("&");
		}
		return sb.substring(0,sb.length()-1);
	}
	
	public static String GetPostUrl(String postData,String postUrl,String submitMethod){
		URL url=null;
		HttpURLConnection urlConn=null;
		try{
			url=new URL(postUrl);
			urlConn=(HttpURLConnection) url.openConnection();
			urlConn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			urlConn.setRequestMethod(submitMethod.toUpperCase());
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			if(submitMethod.equalsIgnoreCase("post")){
				urlConn.getOutputStream().write(postData.getBytes(WeiXinDataHelper.GBKEncode));
				urlConn.getOutputStream().flush();
				urlConn.getOutputStream().close();
			}
			int code=urlConn.getResponseCode();
			if(code==200){
				DataInputStream in=new DataInputStream(urlConn.getInputStream());
				int len=in.available();
				byte[] by=new byte[len];
				in.readFully(by);
				String rev=new String(by,GBKEncode);
				in.close();
				return rev;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(urlConn!=null){
				urlConn.disconnect();
			}
		}
		return null;
	}
	
}