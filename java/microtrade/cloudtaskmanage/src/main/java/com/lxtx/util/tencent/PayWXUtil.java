package com.lxtx.util.tencent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Random;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
  
  private JSONObject getRemoteJsonSecurely(String xml, String url) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException {
	  KeyStore keyStore  = KeyStore.getInstance("PKCS12");
	  WXPayConfig wxConfig = WXPayConfig.getInstance();
	  File certFile = new File(wxConfig.get("pay.cert"));
	  String name = certFile.getName();
	  String password = wxConfig.get("pay.mch_id");
	  String fileNameWithoutExt = name.substring(0, name.lastIndexOf("."));
      FileInputStream instream = new FileInputStream(certFile);
      
      try {
          keyStore.load(instream, password.toCharArray());
      } finally {
          instream.close();
      }

      // Trust own CA and all self-signed certs
      SSLContext sslcontext = SSLContexts.custom()
              .loadKeyMaterial(keyStore, password.toCharArray())
              .build();
      // Allow TLSv1 protocol only
      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
              sslcontext,
              new String[] { "TLSv1" },
              null,
              SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
      
      CloseableHttpClient httpclient = HttpClients.custom()
              .setSSLSocketFactory(sslsf)
              .build();
      try {
    	  HttpPost httppost = new HttpPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers");
    	  HttpEntity entity = new StringEntity(xml, "utf-8");
    	  httppost.setEntity(entity);
          CloseableHttpResponse response = httpclient.execute(httppost);
          
          try {
              entity = response.getEntity();
              System.out.println("----------------------------------------");
              System.out.println(response.getStatusLine());
              if (entity != null) {
                  System.out.println("Response content length: " + entity.getContentLength());
              }
              JSONObject o = Xml2JsonUtil.xml2JSON(entity.getContent());
              JSONObject obj = (JSONObject)o.get("xml");
              EntityUtils.consume(entity);
              return obj;
          } finally {
              response.close();
          }
      } finally {
          httpclient.close();
      }
  }
  
  
  @SuppressWarnings("deprecation")
  public JSONObject getPrepayJson(String xml) {
	  return getRemoteJson(xml, URL);
  }
  
  public JSONObject getRepayJson(String xml) {
	  try {
		return getRemoteJsonSecurely(xml, URL_REPAY);
	} catch (KeyManagementException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
			| CertificateException | IOException e) {
		e.printStackTrace();
		return null;
	}
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
