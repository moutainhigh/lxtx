package com.lxtx.util.tencent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PayWXUtil {
  public static String URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

  public static String URL_REPAY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
  
  private JSONObject getRemoteJson(String xml, String url) {
	  HttpClient httpClient = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
	    InputStream is = null;
	    PostMethod method = null;
	    try {
	      method = new PostMethod(url);
	      method.setRequestBody(xml);
	      httpClient.executeMethod(method);
	      // 读取响应
	      is = method.getResponseBodyAsStream();
	      JSONObject o = Xml2JsonUtil.xml2JSON(is);
	      return o;
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	      if (method != null) {
	        method.releaseConnection();
	      }
	      if (is != null) {
	        try {
	          is.close();
	        } catch (IOException e1) {
	          e1.printStackTrace();
	        }
	      }
	    }
	    return null;
  }
  
  @SuppressWarnings("deprecation")
  public JSONObject getPrepayJson(String xml) {
	  return getRemoteJson(xml, URL);
  }
  
  public String getPrepayid(String xml) {
    try {
      JSONObject jo = getPrepayJson(xml);
      JSONObject element = jo.getJSONObject("xml");
      String prepayid = ((JSONArray) element.get("prepay_id")).get(0).toString();
      return prepayid;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String orderNum() {
    String chars = "0123456789";
    String order = System.currentTimeMillis() + "";
    String res = "";
    for (int i = 0; i < 19; i++) {
      Random rd = new Random();
      res += chars.charAt(rd.nextInt(chars.length() - 1));
    }
    order += res;
    return order;
  }
  
  public static void main(String[] args) {
	File file = new File("D:/dev/code/apiclient_cert.p12");
	String name = file.getName();
	System.out.println(name.substring(0, name.lastIndexOf(".")));
  }
  
}
