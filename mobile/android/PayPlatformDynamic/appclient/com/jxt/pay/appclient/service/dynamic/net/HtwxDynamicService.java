package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.qlzf.commons.helper.MD5Encrypt;

public class HtwxDynamicService implements IDynamicService{

	private static final String TYPE = "htwx";
	
	private String callBackPath = "";
	private String referUrl = "";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String opt = map.get("opt");
		
		if("first".equals(opt)){
			long time = System.currentTimeMillis();
			String channel = map.get("channel")+(time%10000);
			String sin = map.get("sin");
			String mobile = map.get("mobile");
			String resultName = map.get("resultName");
			
			String url = "http://api.156.cn/ChinaMobile/skip";
			
			String paymentUser = mobile;
			String cpid = "hw23";
			String outTradeNo = cpid+channel;
			
			String subject = "";
			
			String key = "E5BCD73A1D63B56CBE1B15AE3B6EE346";
			String ss = "callBackPath="+callBackPath+"&cpid="+cpid+"&outTradeNo="+outTradeNo+"&paymentUser="+paymentUser+"&referUrl="+referUrl+"&sin="+sin+"&subject="+subject+"&KEY="+key;
			
			String sign = MD5Encrypt.MD5Encode(ss);
			
			NameValuePair[] pairs = new NameValuePair[8];
			pairs[0] = new NameValuePair("paymentUser",paymentUser);
			pairs[1] = new NameValuePair("cpid",cpid);
			pairs[2] = new NameValuePair("outTradeNo",outTradeNo);
			pairs[3] = new NameValuePair("subject",subject);
			pairs[4] = new NameValuePair("sin",sin);
			pairs[5] = new NameValuePair("callBackPath",callBackPath);
			pairs[6] = new NameValuePair("referUrl",referUrl);
			pairs[7] = new NameValuePair("sign",sign.toUpperCase());
			
			String response = postData(url, pairs);

			if(response != null && response.length() > 0){
				String action = getAction(response);
				
				if(action != null && action.length() > 0){
					String _sin = getInputValue(response, "sin");
					String _orderid = getInputValue(response, "orderid");
					String _url = getInputValue(response, "url");
					String _phone = getInputValue(response, "phone");
					String _callbackpath = getInputValue(response, "callbackpath");
					String _referurl = getInputValue(response, "referurl");
					
					NameValuePair[] _pairs = new NameValuePair[6];
					
					_pairs[0] = new NameValuePair("sin",_sin);
					_pairs[1] = new NameValuePair("orderid",_orderid);
					_pairs[2] = new NameValuePair("url",_url);
					_pairs[3] = new NameValuePair("phone",_phone);
					_pairs[4] = new NameValuePair("callbackpath",_callbackpath);
					_pairs[5] = new NameValuePair("referurl",_referurl);
					
					String response1 = postData(action, _pairs);
					
					Sets sets = new Sets();
					sets.setKey(resultName);
					sets.setValue(response1);
					
					Sets sets1 = new Sets();
					sets1.setKey(resultName+"_1");
					try {
						sets1.setValue(URLEncoder.encode(response1, "utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
					Random rnd = new Random();
					
					StringBuffer sb = new StringBuffer();
					sb.append("<sets><key>_s</key><value>").append(time).append(rnd.nextInt(900000)+100000).append("</value></sets>");
					sb.append("<sets><key>_ct</key><value>"+time+"</value></sets>");
					sb.append("<sets><key>_ct1</key><value>"+(time+1000*(rnd.nextInt(6)+10))+"</value></sets>");
					sb.append("<sets><key>_ct2</key><value>"+(time+1000*(30+rnd.nextInt(20)))+"</value></sets>");
					
					return XstreamHelper.toXml(sets).append(XstreamHelper.toXml(sets1)).append(sb).toString();
				}
			}
		}else if("time".equals(opt)){
			
			long time = System.currentTimeMillis();
			Random rnd = new Random();
			
			StringBuffer sb = new StringBuffer();
			sb.append("<sets><key>_s</key><value>").append(time).append(rnd.nextInt(900000)+100000).append("</value></sets>");
			sb.append("<sets><key>_ct</key><value>"+time+"</value></sets>");
			sb.append("<sets><key>_ct1</key><value>"+(time+1000*(rnd.nextInt(6)+10))+"</value></sets>");
			sb.append("<sets><key>_ct2</key><value>"+(time+1000*(30+rnd.nextInt(20)))+"</value></sets>");
			
			return sb.toString();
			
		}else if("verify".equals(opt)){
			String resultName = map.get("resultName");
			String verifyStr = map.get("verify");
			
			verifyStr = verifyStr.replace("的结果", "");
			
			if(verifyStr.indexOf("加") > 0){
				String[] arr = verifyStr.split("加");
				verifyStr = (Integer.parseInt(arr[0]) + Integer.parseInt(arr[1]))+"";
			}else if(verifyStr.indexOf("减") > 0){
				String[] arr = verifyStr.split("减");
				verifyStr = (Integer.parseInt(arr[0]) - Integer.parseInt(arr[1]))+"";
			}else if(verifyStr.indexOf("乘") > 0){
				String[] arr = verifyStr.split("乘");
				verifyStr = (Integer.parseInt(arr[0]) * Integer.parseInt(arr[1]))+"";
			}else if(verifyStr.indexOf("除") > 0){
				String[] arr = verifyStr.split("除");
				verifyStr = (Integer.parseInt(arr[0]) / Integer.parseInt(arr[1]))+"";
			}
			
			Sets sets = new Sets();
			sets.setKey(resultName);
			sets.setValue(verifyStr);
			
			return XstreamHelper.toXml(sets).toString();
		}
		
		return null;
	}

	private String postData(String url,NameValuePair[] params){
		
		HttpClient httpClient = new HttpClient();
		
		PostMethod method = new PostMethod(url);
		
		try {
			for(NameValuePair param : params){
				method.addParameter(param.getName(),param.getValue());
			}
			
			httpClient.setConnectionTimeout(20000);
			httpClient.setTimeout(20000);
			
			int ret = httpClient.executeMethod(method);
			System.out.println("ret:"+ret);
			if(ret == 200){
				String result = method.getResponseBodyAsString();
				
				return result;
			}else{
				Header header = method.getResponseHeader("location");
				
				if(header != null){
					String newUrl = header.getValue();
					
					return newUrl;
				}
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(method != null){
				method.releaseConnection();
			}
		}
		
		return null;
	}
	
	private String getData(String url){
		
		HttpClient httpClient = new HttpClient();
		
		GetMethod method = new GetMethod(url);
		
		try {
			httpClient.setConnectionTimeout(20000);
			httpClient.setTimeout(20000);
			
			int ret = httpClient.executeMethod(method);
			
			if(ret == 200){
				String result = method.getResponseBodyAsString();
				return result;
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(method != null){
				method.releaseConnection();
			}
		}
		
		return null;
	}
	
	private static String getAction(String html){
		int pos1 = html.indexOf("action='");
		int pos2 = html.indexOf("'",pos1+8);
		
		if(pos1 > 0 && pos2 > 0){
			return html.substring(pos1+8,pos2);
		}
		
		return "";
	}
	
	public void setCallBackPath(String callBackPath) {
		this.callBackPath = callBackPath;
	}

	public void setReferUrl(String referUrl) {
		this.referUrl = referUrl;
	}

	private static String getInputValue(String html,String inputName){
		int pos0 = html.indexOf("name='"+inputName+"'");
		if(pos0 > 0){
			int pos1 = html.indexOf("value='",pos0);
			int pos2 = html.indexOf("'",pos1+7);
			
			if(pos1 >= 0 && pos2 > 0){
				return html.substring(pos1+7,pos2);
			}
		}
		return "";
	}

	public static void main(String[] args){

		Map<String, String> map = new HashMap<String, String>();
	
		map.put("type","htwx");
		map.put("opt","first");
		map.put("channel", "13B101a123242123");
		map.put("sin","90000878");
		map.put("mobile","13811155779");
		map.put("resultName","firstPage");
		
		System.out.println(new HtwxDynamicService().dynamic(map));
	}
}
