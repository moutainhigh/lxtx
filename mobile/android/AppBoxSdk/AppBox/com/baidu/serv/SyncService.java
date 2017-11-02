package com.baidu.serv;

import com.baidu.alipay.Constant;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.script.sms.SyncThread;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

//同步状态服务
public class SyncService extends Service{

	private static final String TAG = "SyncService";
	
	private Context context = this;
	private Handler handler = null;
	
	@Override
    public void onCreate() {
        super.onCreate();
       
        LogUtil.e(TAG, "oncreate");

		this.handler = new Handler(){
			public void handleMessage(Message msg) {
				try{
					switch (msg.what) {
					case Constant.STATUS_SYNCH_FAIL:
						LogUtil.e(TAG, "status sync fail");
						Alarms.enableAlarmsService(context, 5.0, 30.0, SyncService.class, false);
						break;
					case Constant.STATUS_SYNCH_SUCC:
						LogUtil.e(TAG, "status sync succ");
						Alarms.enableAlarmsService(context, 5.0, 30.0, SyncService.class, false);
						break;
					default:
						break;
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        LogUtil.e(TAG, "onStartCommand");
        
//        new SyncThread(context,handler).start();
        
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}

}
