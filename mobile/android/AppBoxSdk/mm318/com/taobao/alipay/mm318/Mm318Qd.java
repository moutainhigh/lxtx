package com.taobao.alipay.mm318;

import com.mm.Business.BillNative;

import android.content.Context;

public class Mm318Qd {

	private String appid = "";
	private String paycode = "";
	private String imsi = "";
	private String imei = "";
	private String channel = "";
	private String data = "";
	private String ip;
	private String port;
	private Context context = null;

	public Mm318Qd(Context context, String ip, String port, String imsi,
			String imei, String appid, String paycode, String channel,
			String data) {
		this.context = context;

		this.ip = ip;
		this.port = port;

		this.imsi = imsi;
		this.imei = imei;
		this.appid = appid;
		this.paycode = paycode;
		this.channel = channel;
		this.data = data;
	}

	public void work(){
		
		BillNative.ActiveDAUser(context, ip, port, appid, paycode, imsi, imei, channel, 1, data);
		
	}
}
