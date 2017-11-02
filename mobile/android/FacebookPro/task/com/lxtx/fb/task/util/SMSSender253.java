package com.lxtx.fb.task.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

/**
 *
 * @param url
 *            应用地址，类似于http://ip:port/msg/
 * @param un
 *            账号
 * @param pw
 *            密码
 * @param phone
 *            手机号码，多个号码使用","分割
 * @param msg
 *            短信内容
 * @param rd
 *            是否需要状态报告，需要1，不需要0
 * @return 返回值定义参见HTTP协议文档
 * @throws Exception
 */
public class SMSSender253 {
	
	private static final String url = "http://sms.253.com/msg/";// 应用地址
	private static final String un = "N9667938";// 账号 
	private static final String pw = "aKdGheof8";// 密码
	
	public static void batchSend(String phone){
		String msg = "【寄售宝】服务监控异常：0000，请检查";
		batchSend(phone, msg);
	}
	
	public static void batchSend(String phone, String msg){
		try {
			batchSend(url, un, pw, phone, msg, "0", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String batchSend(String url, String un, String pw, String phone, String msg, String rd, String ex)
			throws Exception {
		HttpClient client = new HttpClient();//new HttpClientParams(), new SimpleHttpConnectionManager(true));
		GetMethod method = new GetMethod();
		try {
			URI base = new URI(url, false);
			method.setURI(new URI(base, "send", false));
			method.setQueryString(new NameValuePair[] { new NameValuePair("un", un), new NameValuePair("pw", pw),
					new NameValuePair("phone", phone), new NameValuePair("rd", rd), new NameValuePair("msg", msg),
					new NameValuePair("ex", ex), });
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}
	}

	public static void main(String[] args) {
//		new Thread(new Runnable() {
//			String url = "http://sms.253.com/msg/";// 应用地址
//			String un = "N9667938";// 账号 
//			String pw = "aKdGheof8";// 密码
//			String phone = "13811155779";// 手机号码，多个号码使用","分割
//			String rd = "1";// 是否需要状态报告，需要1，不需要0
//			String ex = null;// 扩展码
//			int num = 1000;
////
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						String msg = "【寄售宝】您的验证码为" + num + "，请尽快验证";// 短信内容
//						System.out.println("send msg:" + msg);
//						String returnString = batchSend(url, un, pw, phone, msg, rd, ex);
//						System.out.println(returnString);
//						String[]lines= returnString.split("\n");
//						String[] states= lines[0].split(",");
//						int state=Integer.parseInt(states[1].trim());
//						num++;
//						Thread.sleep(1000);
//						if(num>1020){
//							break;
//						}
//						// TODO 处理返回值,参见HTTP协议文档
//					} catch (Exception e) {
//						// TODO 处理异常
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
		batchSend("18733135212,15801187224");
	}
}