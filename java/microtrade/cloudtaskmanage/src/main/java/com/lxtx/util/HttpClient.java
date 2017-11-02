package com.lxtx.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


public class HttpClient {
	
	/**
	 * GET请求
	 * 
	 * @param url
	 *            请求url,参数拼在请求串中
	 */
	public static String get(String url) {
		String responseContent = null;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		HttpGet httpGet = new HttpGet(url);
		Logger.getRootLogger().info("--------------------------------------------");
		Logger.getRootLogger().info("GET " + httpGet.getURI());
		// 执行get请求.
		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			// 打印响应状态
			Logger.getRootLogger().info(response.getStatusLine());
			if (entity != null) {
				responseContent = EntityUtils.toString(entity);
				// 打印响应内容
				Logger.getRootLogger().info("Response content: "+ responseContent);
				Logger.getRootLogger().info("--------------------------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;

	}

	/**
	 * 
	 * @param url
	 * @param paramMap
	 */
	public static String post(String url, Map<String, String> paramMap) {
		String responseContent = null;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		Logger.getRootLogger().info("--------------------------------------------");
		Logger.getRootLogger().info("POST " + httppost.getURI());
		// 创建参数队列
		List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
		if (paramMap != null) {
			Set<String> key = paramMap.keySet();
			for (Iterator<String> it = key.iterator(); it.hasNext();) {
				String paramKey = (String) it.next();
				formparams.add(new BasicNameValuePair(paramKey, paramMap
						.get(paramKey)));
				Logger.getRootLogger().info(paramKey+" = "+paramMap.get(paramKey));
			}
		}

		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				Logger.getRootLogger().info(response.getStatusLine());
				if (entity != null) {
					responseContent = EntityUtils.toString(entity, "gbk");
					Logger.getRootLogger().info("Response content: "+ responseContent);
					Logger.getRootLogger().info("--------------------------------------------");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;
	}

	// /**
	// * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
	// */
	// public void post() {
	// // 创建默认的httpClient实例.
	// CloseableHttpClient httpclient = HttpClients.createDefault();
	// // 创建httppost
	// HttpPost httppost = new HttpPost();
	// // 创建参数队列
	// List<BasicNameValuePair> formparams = new
	// ArrayList<BasicNameValuePair>();
	// formparams.add(new BasicNameValuePair("type", "house"));
	// UrlEncodedFormEntity uefEntity;
	// try {
	// uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
	// httppost.setEntity(uefEntity);
	// System.out.println("executing request " + httppost.getURI());
	// CloseableHttpResponse response = httpclient.execute(httppost);
	// try {
	// HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// System.out
	// .println("--------------------------------------");
	// System.out.println("Response content: "
	// + EntityUtils.toString(entity, "UTF-8"));
	// System.out
	// .println("--------------------------------------");
	// }
	// } finally {
	// response.close();
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (UnsupportedEncodingException e1) {
	// e1.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// // 关闭连接,释放资源
	// try {
	// httpclient.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// /**
	// * 发送 get请求
	// */
	// public void get() {
	// CloseableHttpClient httpclient = HttpClients.createDefault();
	// try {
	// // 创建httpget.
	// HttpGet httpget = new HttpGet("http://www.baidu.com/");
	//
	// System.out.println("executing request " + httpget.getURI());
	// // 执行get请求.
	// CloseableHttpResponse response = httpclient.execute(httpget);
	// try {
	// // 获取响应实体
	// HttpEntity entity = response.getEntity();
	// System.out.println("--------------------------------------");
	// // 打印响应状态
	// System.out.println(response.getStatusLine());
	// if (entity != null) {
	// // 打印响应内容长度
	// System.out.println("Response content length: "
	// + entity.getContentLength());
	// // 打印响应内容
	// System.out.println("Response content: "
	// + EntityUtils.toString(entity));
	// }
	// System.out.println("------------------------------------");
	// } finally {
	// response.close();
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (ParseException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// // 关闭连接,释放资源
	// try {
	// httpclient.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// /**
	// * HttpClient连接SSL
	// */
	// public void ssl() {
	// CloseableHttpClient httpclient = null;
	// try {
	// KeyStore trustStore = KeyStore.getInstance(KeyStore
	// .getDefaultType());
	// FileInputStream instream = new FileInputStream(new File(
	// "d:\\tomcat.keystore"));
	// try {
	// // 加载keyStore d:\\tomcat.keystore
	// trustStore.load(instream, "123456".toCharArray());
	// } catch (CertificateException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// instream.close();
	// } catch (Exception ignore) {
	// }
	// }
	// // 相信自己的CA和所有自签名的证书
	// SSLContext sslcontext = SSLContexts
	// .custom()
	// .loadTrustMaterial(trustStore,
	// new TrustSelfSignedStrategy()).build();
	// // 只允许使用TLSv1协议
	// SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	// sslcontext,
	// new String[] { "TLSv1" },
	// null,
	// SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	// httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
	// .build();
	// // 创建http请求(get方式)
	// HttpGet httpget = new HttpGet(
	// "https://localhost:8443/myDemo/Ajax/serivceJ.action");
	// System.out.println("executing request" + httpget.getRequestLine());
	// CloseableHttpResponse response = httpclient.execute(httpget);
	// try {
	// HttpEntity entity = response.getEntity();
	// System.out.println("----------------------------------------");
	// System.out.println(response.getStatusLine());
	// if (entity != null) {
	// System.out.println("Response content length: "
	// + entity.getContentLength());
	// System.out.println(EntityUtils.toString(entity));
	// EntityUtils.consume(entity);
	// }
	// } finally {
	// response.close();
	// }
	// } catch (ParseException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (KeyManagementException e) {
	// e.printStackTrace();
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// } catch (KeyStoreException e) {
	// e.printStackTrace();
	// } finally {
	// if (httpclient != null) {
	// try {
	// httpclient.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }

	// /**
	// * post方式提交表单（模拟用户登录请求）
	// */
	// public void postForm() {
	// // 创建默认的httpClient实例.
	// CloseableHttpClient httpclient = HttpClients.createDefault();
	// // 创建httppost
	// HttpPost httppost = new HttpPost(
	// "http://localhost:8080/myDemo/Ajax/serivceJ.action");
	// // 创建参数队列
	// List<BasicNameValuePair> formparams = new
	// ArrayList<BasicNameValuePair>();
	// formparams.add(new BasicNameValuePair("username", "admin"));
	// formparams.add(new BasicNameValuePair("password", "123456"));
	// UrlEncodedFormEntity uefEntity;
	// try {
	// uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
	// httppost.setEntity(uefEntity);
	// System.out.println("executing request " + httppost.getURI());
	// CloseableHttpResponse response = httpclient.execute(httppost);
	// try {
	// HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// System.out
	// .println("--------------------------------------");
	// System.out.println("Response content: "
	// + EntityUtils.toString(entity, "UTF-8"));
	// System.out
	// .println("--------------------------------------");
	// }
	// } finally {
	// response.close();
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (UnsupportedEncodingException e1) {
	// e1.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// // 关闭连接,释放资源
	// try {
	// httpclient.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// /**
	// * 上传文件
	// */
	// public void upload() {
	// CloseableHttpClient httpclient = HttpClients.createDefault();
	// try {
	// HttpPost httppost = new HttpPost(
	// "http://localhost:8080/myDemo/Ajax/serivceFile.action");
	//
	// FileBody bin = new FileBody(new File("F:\\image\\sendpix0.jpg"));
	// StringBody comment = new StringBody("A binary file of some kind",
	// ContentType.TEXT_PLAIN);
	//
	// HttpEntity reqEntity = MultipartEntityBuilder.create()
	// .addPart("bin", bin).addPart("comment", comment).build();
	//
	// httppost.setEntity(reqEntity);
	//
	// System.out
	// .println("executing request " + httppost.getRequestLine());
	// CloseableHttpResponse response = httpclient.execute(httppost);
	// try {
	// System.out.println("----------------------------------------");
	// System.out.println(response.getStatusLine());
	// HttpEntity resEntity = response.getEntity();
	// if (resEntity != null) {
	// System.out.println("Response content length: "
	// + resEntity.getContentLength());
	// }
	// EntityUtils.consume(resEntity);
	// } finally {
	// response.close();
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// httpclient.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
}