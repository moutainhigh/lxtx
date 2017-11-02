package com.baidu.serv;

import com.baidu.alipay.AppProperty;
import com.baidu.alipay.LogUtil;

import android.os.Handler;

public class BaseThread extends Thread{

	public BaseThread(final Handler handler){
		this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread arg0, Throwable e) {
				e.printStackTrace();
				
				LogUtil.e("BaseThread", e.getClass().getName());
				if(handler != null){
					handler.sendEmptyMessage(AppProperty.BASE_ERROR);
				}
			}
		});
	}	
}
