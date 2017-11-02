package com.jxt.netpay.appclient.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.iapppay.HttpUtils;
import com.jxt.netpay.appclient.pojo.CallBackAndNotify;
import com.jxt.netpay.appclient.pojo.CallBackResult;
import com.jxt.netpay.appclient.pojo.NotifyParam;
import com.jxt.netpay.appclient.pojo.PayParam;
import com.jxt.netpay.appclient.service.IPay;
import com.jxt.netpay.appclient.util.XmlUtils;

public class ZxsmPayImplIPay implements IPay{

	private static Logger logger = Logger.getLogger(ZxsmPayImplIPay.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyyMMddHHmmss");
	}
	
	class Config{
		private String appId;
		private String key;
		private String jumpUrl;
	
		public Config(String accountTxt){
			
			String[] arr = accountTxt.split("\\|");
			
			if(arr.length == 3){
				appId = arr[0];
				key = arr[1];
				jumpUrl = arr[2];
			}
		}

		public String getAppId() {
			return appId;
		}

		public String getKey() {
			return key;
		}
		
		public String getJumpUrl(){
			return jumpUrl;
		}
	}
	
	private Config config = null;	

	@Override
	public String getTn(PayParam payParam, CallBackAndNotify callBackAndNotify) {
		return null;
	}

	@Override
	public String getUrl(PayParam payParam, CallBackAndNotify callBackAndNotify) {
		
		StringBuffer data = new StringBuffer();
		boolean succ = false;
		
		JSONObject jo = new JSONObject();
		
		try{
			jo.put("out_trade_no", payParam.getTradeNo());
			
			Map<String,String> map = new HashMap<String, String>();
			
			int total_fee = payParam.getFee().intValue();
			
			map.put("body","计费");
			map.put("mch_create_ip","127.0.0.1");
			map.put("mch_id",config.appId);
			map.put("nonce_str",genNonceStr());
			map.put("notify_url",callBackAndNotify.getNotifyUrl());
			map.put("out_trade_no",payParam.getTradeNo());
			
			map.put("service","pay.weixin.native");
			map.put("total_fee",total_fee+"");
		
			data.append("?total_fee=").append(map.get("total_fee"));
			data.append("&body=").append(URLEncoder.encode(map.get("body"),"utf-8"));
			data.append("&out_trade_no=").append(payParam.getSubject());
			data.append("&tradeId=").append(payParam.getTradeNo());
			
			StringBuffer sb = new StringBuffer();
			sb.append("body=").append(map.get("body")).append("&mch_create_ip=").append(map.get("mch_create_ip"));
			sb.append("&mch_id=").append(map.get("mch_id")).append("&nonce_str=").append(map.get("nonce_str"));
			sb.append("&notify_url=").append(map.get("notify_url")).append("&out_trade_no=").append(map.get("out_trade_no"));
			sb.append("&service=").append(map.get("service")).append("&total_fee=").append(map.get("total_fee"));
			sb.append("&key=").append(config.getKey());
			
			String sign =  MD5Util.MD5Encode(sb.toString(),"utf-8").toUpperCase();

	        map.put("sign",sign);
	        
	        String xml = XmlUtils.toXml(map);

	        logger.info("sb:"+sb.toString()+";sign:"+sign);
	        
	        String url = "https://pay.swiftpass.cn/pay/gateway";
	        
	        String resp = HttpUtils.sentPost(url, xml);
	        
	        logger.info("resp:"+resp);
	        
	        if(resp != null && resp.length() > 0){
	        	String status = XmlUtils.getNodeValue(resp, "status");
	        	String result_code = XmlUtils.getNodeValue(resp,"result_code");
	        	
	        	if("0".equals(status) && "0".equals(result_code)){
	        		succ = true;
	        		
	        		String code_img_url = XmlUtils.getNodeValue(resp,"code_img_url");
                    
	        		jo.put("info",code_img_url);
	        		
	        		data.append("&code_img_url=").append(URLEncoder.encode(code_img_url,"utf-8"));	        			    
	        	}
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		data.append("&status=").append(succ?"1":"0");
		
		String type = callBackAndNotify.getRequest().getParameter("type");
		if(type != null && "json".equals(type)){
			String callback = callBackAndNotify.getRequest().getParameter("callback");
			return callback+"("+jo.toString()+")";
		}else{
			return config.getJumpUrl()+data.toString();
		}
	}

	@Override
	public Long getTradeNoFromCallBack(HttpServletRequest request) {
		
		return null;
	}

	private String requestBody= "";
	
	@Override
	public Long getTradeNoFromNotify(HttpServletRequest request) {
		
		requestBody = parseRequst(request);
		
		logger.info("requestBody:"+requestBody);
		
		return Long.parseLong(XmlUtils.getNodeValue(requestBody, "out_trade_no"));
	}

	@Override
	public CallBackResult callBack(HttpServletRequest request,
			HttpServletResponse response) {
		
		return null;
	}

	@Override
	public NotifyParam notify(HttpServletRequest request,
			HttpServletResponse response) {
		
		NotifyParam notifyParam = new NotifyParam();
		
		try{
			notifyParam.setNotifyData(requestBody);
			notifyParam.setSucc(true);
			
			String status = XmlUtils.getNodeValue(requestBody, "status");
			
			if("0".equals(status)){
				String result_code = XmlUtils.getNodeValue(requestBody, "result_code");
				
				if("0".equals(result_code)){
					notifyParam.setStatus(1);
					notifyParam.setTransactionNumber(XmlUtils.getNodeValue(requestBody, "transaction_id"));
		
					notifyParam.setPaymentTime(sdf.parse(XmlUtils.getNodeValue(requestBody, "time_end")));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return notifyParam;
	}

	@Override
	public String getNotifyMsg(Boolean succ) {		
		return "success";
	}

	@Override
	public void initProp(String accountTxt) {
		this.config = new Config(accountTxt);
	}

	private static String genNonceStr(){
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)),"utf-8");
    }
	
	public static String parseRequst(HttpServletRequest request){
        String body = "";
        try {
            ServletInputStream inputStream = request.getInputStream(); 
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while(true){
                String info = br.readLine();
                if(info == null){
                    break;
                }
                if(body == null || "".equals(body)){
                    body = info;
                }else{
                    body += info;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }            
        return body;
    }
	
	static class MD5Util {

		private static String byteArrayToHexString(byte b[]) {
			StringBuffer resultSb = new StringBuffer();
			for (int i = 0; i < b.length; i++)
				resultSb.append(byteToHexString(b[i]));

			return resultSb.toString();
		}

		private static String byteToHexString(byte b) {
			int n = b;
			if (n < 0)
				n += 256;
			int d1 = n / 16;
			int d2 = n % 16;
			return hexDigits[d1] + hexDigits[d2];
		}

		public static String MD5Encode(String origin, String charsetname) {
			String resultString = null;
			try {
				resultString = new String(origin);
				MessageDigest md = MessageDigest.getInstance("MD5");
				if (charsetname == null || "".equals(charsetname))
					resultString = byteArrayToHexString(md.digest(resultString
							.getBytes()));
				else
					resultString = byteArrayToHexString(md.digest(resultString
							.getBytes(charsetname)));
			} catch (Exception exception) {
			}
			return resultString;
		}

		private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
		
	}
}
