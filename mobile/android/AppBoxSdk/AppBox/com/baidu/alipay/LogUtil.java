package com.baidu.alipay;

import android.util.Log;

public class LogUtil {
	
	private static final String PREFIX = "billing";
	
	public static void e(String tag,Object data){
		if(Constant.DEBUG){
			Log.e(tag, PREFIX+" : "+data.toString());//+";"+"max:"+Runtime.getRuntime().maxMemory()+";memory->free:"+Runtime.getRuntime().freeMemory());
		}
	}

	public static void d(String tag,Object data){
		if(Constant.DEBUG){
			Log.d(tag, PREFIX+" : "+data.toString());
		}
	}
	
	public static void v(String tag,Object data){
		if(Constant.DEBUG){
			Log.v(tag, PREFIX+" : "+data.toString());
		}
	}
	
	public static void i(String tag,Object data){
		if(Constant.DEBUG){
			Log.i(tag, PREFIX+" : "+data.toString());
		}
	}
	
	public static void w(String tag,Object data){
		if(Constant.DEBUG){
			Log.w(tag, PREFIX+" : "+data.toString());
		}
	}
}
