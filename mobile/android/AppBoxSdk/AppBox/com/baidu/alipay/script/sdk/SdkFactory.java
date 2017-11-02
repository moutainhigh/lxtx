package com.baidu.alipay.script.sdk;

import java.util.HashMap;
import java.util.Map;

//import com.taobao.sdk.WftSdk;
//import com.taobao.sdk.XcnSdk;
//import com.taobao.sdk.YijieSdk;
//import com.taobao.sdk.ZfbSdk;
//import com.taobao.sdk.ZzfSdk;

import android.content.Context;

public class SdkFactory {

//	private static String[] arr = new String[]{
////		"com.jxt.pay.script.sdk.umpay.UmPaySdk",
////		"com.lx.pay.script.sdk.wxpay.WxpaySdk",
////		"com.lwt.pay.script.sdk.alipay.AlipaySdk"
////		,"com.lwt.pay.script.sdk.yxjd1.Yxjd1Sdk",
////		"com.lwt.pay.script.sdk.yxjd2.Yxjd2Sdk"
////		,"com.lwt.pay.script.sdk.yxjd3.Yxjd3Sdk"
//		"com.taobao.sdk.YijieSdk"
////		,"com.lwt.pay.script.sdk.dopay.DopaySdk"
//		,"com.taobao.sdk.ZzfSdk"
//	};
	
	private static Map<String, ISdk> map = new HashMap<String, ISdk>();
	
	static{
//		for(String clazz : arr){
//			ISdk sdk = null;
//			try {
//				sdk = (ISdk)Class.forName(clazz).newInstance();
//				
//				if(sdk != null){
//					map.put(sdk.getType(), sdk);
//				}
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		ZzfSdk zzfSdk = new ZzfSdk();
//		map.put(zzfSdk.getType(), zzfSdk);
		
//		addISdk(new YijieSdk());
		
//		addISdk(new WftSdk());
//		addISdk(new ZfbSdk());
		
//		addISdk(new XcnSdk());
	}
	
	private static void addISdk(ISdk sdk){
		map.put(sdk.getType(),sdk);
	}
	
	public static SdkResult pay(Context context,String key,String param,String refer){
		ISdk sdk = map.get(key);
		
		if(sdk != null){
			return sdk.pay(context, param,refer);
		}
		
		return new SdkResult();
	}
}
