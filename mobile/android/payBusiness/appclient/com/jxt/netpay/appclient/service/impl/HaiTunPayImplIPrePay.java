package com.jxt.netpay.appclient.service.impl;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.netpay.appclient.service.IOneJump;
import com.jxt.netpay.appclient.service.IPrePay;
import com.jxt.netpay.appclient.util.PostParamsData;

public class HaiTunPayImplIPrePay implements IPrePay{

	private String appId = "10710";
	
	private String appKey = "76885da4b54348d4ca7a86b1529656c8";

	@Override
	public String getType() {
		return "haitun";
	}

	@Override
	public String prepay(HttpServletRequest servletRequest) {
		 
		String orderId = servletRequest.getParameter("orderId");
		String fee = servletRequest.getParameter("fee");
		String notifyUrl = servletRequest.getParameter("notifyUrl");
		String callBackUrl = servletRequest.getParameter("callBackUrl");
		
		String url = "http://pay.ylsdk.com";
		
		NameValuePair[] params = new NameValuePair[16];
		params[0] = new NameValuePair("p0_Cmd","Buy");
		params[1] = new NameValuePair("p1_MerId",appId);
		params[2] = new NameValuePair("p2_Order",orderId);
		params[3] = new NameValuePair("p3_Amt",fee);
		params[4] = new NameValuePair("p4_Cur","CNY");
		params[5] = new NameValuePair("p5_Pid","0");
		params[6] = new NameValuePair("p6_Pcat","0");
		params[7] = new NameValuePair("p7_Pdesc","fee");
		params[8] = new NameValuePair("p8_Url",notifyUrl);
		params[9] = new NameValuePair("p9_SAF","0");
		params[10] = new NameValuePair("pa_MP",callBackUrl);
		params[11] = new NameValuePair("pd_FrpId","zsyh");
		params[12] = new NameValuePair("pr_NeedResponse","1");
		params[13] = new NameValuePair("Sjt_UserName",""+System.currentTimeMillis());
		params[14] = new NameValuePair("Sjt_Paytype","b");
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i <= 12 ; i ++){
			sb.append(params[i].getValue());
		}
		sb.append(appKey);
		
		String hmac = MD5Encrypt.MD5Encode(sb.toString()).toLowerCase();
		
		params[15] = new NameValuePair("hmac",hmac);
		
		System.out.println("s:"+sb.toString()+";hmac:"+hmac);
		
		String resp = PostParamsData.postData(url, params);
		
		System.out.println("resp:"+resp);
		
		if(resp != null){
			try{
				JSONObject jo = new JSONObject(resp);
				
				if("9999".equals(jo.getString("error"))){
					String message = jo.getString("message");
					
					return message;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return callBackUrl;
	}
	
	static class MD5Encrypt {

	    private Logger log=Logger.getLogger(MD5Encrypt.class);
	    
	    private final static String[] hexDigits = {
	      "0", "1", "2", "3", "4", "5", "6", "7",
	      "8", "9", "a", "b", "c", "d", "e", "f"};

	  /**
	   * 转换字节数组�?16进制字串
	   * @param b 字节数组
	   * @return 16进制字串
	   */
	  public static String byteArrayToString(byte[] b) {
	    StringBuffer resultSb = new StringBuffer();
	    for (int i = 0; i < b.length; i++) {
	      resultSb.append(byteToHexString(b[i]));//若使用本函数转换则可得到加密结果�?16进制表示，即数字字母混合的形�?
	      //resultSb.append(byteToNumString(b[i]));//使用本函数则返回加密结果�?10进制数字字串，即全数字形�?
	    }
	    return resultSb.toString();
	  }

	  private static String byteToNumString(byte b) {

	    int _b = b;
	    if (_b < 0) {
	      _b = 256 + _b;
	    }

	    return String.valueOf(_b);
	  }

	  private static String byteToHexString(byte b) {
	    int n = b;
	    if (n < 0) {
	      n = 256 + n;
	    }
	    int d1 = n / 16;
	    int d2 = n % 16;
	    return hexDigits[d1] + hexDigits[d2];
	  }

	  public static String MD5Encode(String origin) {
	    String resultString = null;

	    try {
	      resultString = new String(origin);
	      MessageDigest md = MessageDigest.getInstance("MD5");
	      resultString =
	byteArrayToString(md.digest(resultString.getBytes("utf-8")));
	      resultString=resultString.toUpperCase();
	    }
	    catch (Exception ex) {
	    	
	    }
	    return resultString;
	  }

	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	
	public static void main(String[] args){
		
		String orderId = System.currentTimeMillis()+"";
		String fee = "2.00";
		String notifyUrl = "http://www.baidu.com";
		String callBackUrl = "http://www.baidu.com";
		
		String url = "http://pay.ylsdk.com";
		
		NameValuePair[] params = new NameValuePair[16];
		params[0] = new NameValuePair("p0_Cmd","Buy");
		params[1] = new NameValuePair("p1_MerId","10710");
		params[2] = new NameValuePair("p2_Order",orderId);
		params[3] = new NameValuePair("p3_Amt",fee);
		params[4] = new NameValuePair("p4_Cur","CNY");
		params[5] = new NameValuePair("p5_Pid","0");
		params[6] = new NameValuePair("p6_Pcat","0");
		params[7] = new NameValuePair("p7_Pdesc","fee");
		params[8] = new NameValuePair("p8_Url",notifyUrl);
		params[9] = new NameValuePair("p9_SAF","0");
		params[10] = new NameValuePair("pa_MP",callBackUrl);
		params[11] = new NameValuePair("pd_FrpId","zsyh");
		params[12] = new NameValuePair("pr_NeedResponse","1");
		params[13] = new NameValuePair("Sjt_UserName",""+System.currentTimeMillis());
		params[14] = new NameValuePair("Sjt_Paytype","b");
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i <= 12 ; i ++){
			sb.append(params[i].getValue());
		}
		sb.append("76885da4b54348d4ca7a86b1529656c8");
		
		String hmac = MD5Encrypt.MD5Encode(sb.toString());
		
		params[15] = new NameValuePair("hmac",hmac);
		
		String resp = PostParamsData.postData(url, params);
		
		System.out.println(resp);
	}

	
	
}