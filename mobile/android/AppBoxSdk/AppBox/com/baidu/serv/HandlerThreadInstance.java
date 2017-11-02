package com.baidu.serv;

import com.baidu.alipay.LogUtil;

import android.os.HandlerThread;
import android.os.Looper;

public class HandlerThreadInstance {

	private static HandlerThread handlerThread = null;
	private static String s = "";
	
	public static Looper getLooper(){
		return getInstance().getLooper();
	}
	
	private static HandlerThread getInstance(){
		if(handlerThread == null){
			synchronized (s) {
				if(handlerThread == null){
					handlerThread = new HandlerThread("workThread");
					LogUtil.e("HandlerThreadInstance", "new Instance");
					handlerThread.start();
				}
			}
		}
		
		return handlerThread;
	}
	
}
