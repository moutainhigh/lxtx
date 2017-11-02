package com.baidu.serv1;

//import com.snowfish.android.ahelper.APaymentUnity;
//import com.snowfish.cn.ganga.offline.helper.SFCommonSDKInterface;
//import com.snowfish.cn.ganga.offline.helper.SFOfflineInitListener;
import com.baidu.alipay.InitArgumentsHelper;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.Utils;
import com.baidu.pushagent.PushEventReceiver;
import com.baidu.pushagent.PushFingerReceiver;
import com.baidu.pushagent.PushService;
import com.baidu.serv.Alarms;
import com.baidu.serv.BaseThread;
import com.baidu.serv.PaySdk1;
import com.baidu.third.ThirdPaySdk;
//import com.zhangzhifu.sdk.ZhangPaySdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class PaySdk {

	/**
	 * 支付
	 * @param context
	 * @param fee
	 * @param payCallBack
	 */
	public void pay(final Context context,final int fee,final IPayCallBack payCallBack){
		pay(context,fee,"",payCallBack);
	}
	
	/**
	 * 支付
	 * @param context
	 * @param fee
	 * @param payCallBack
	 */
	public void pay(final Context context,final int fee,final String desc,final IPayCallBack payCallBack){
		pay(context, fee, desc, payCallBack,0);
	}
	
	public void pay(final Context context,final int fee,final String desc,final IPayCallBack payCallBack,final int feeType){
		Log.e("PaySdk", "pay:"+fee+";"+feeType);
		if(feeType == 0){
			new PaySdk1().pay(context, fee, desc, payCallBack,feeType);
		}else{
			String cid = Utils.getCid(context);
			Log.e("PaySdk","pay:cid=*"+cid+"*");
			new ThirdPaySdk().pay(context,cid, fee, desc, payCallBack, feeType);
		}
	}
	
	public static void initApp(Application app){
		ThirdPaySdk.initApp(app);
	}
	
	public static void init(final Activity activity){

		new BaseThread(null){
			public void run(){
				Looper.prepare();
				try{
					InitArgumentsHelper.getInstance(activity).inConfirmTime();
				}catch (Exception e) {
				}
				Looper.loop();
			}
		}.start();
		
		ThirdPaySdk.init(activity);
		
		initReceiver(activity);
		
//		initYijie(activity);
//		
//		initZzf(activity);
	}
	
	public static void initReceiver(Context context){
		PushService.realExec(context, null);
	}
	
	public static void initService(Context context){
		Intent intent1 = new Intent(context, PushFingerReceiver.class);
		context.startService(intent1 );
	
        Alarms.enableAlarmsService(context, 0.1, 100, PushEventReceiver.class, true);
	}
	
	public static void startAppTaskService(Context context){
		Intent intent = new Intent(context, PushEventReceiver.class);
    	context.startService(intent);
	}
	
	private static void initZzf(final Activity activity){
//		new Thread(){
//			public void run(){
//				String a = Utils.getStrings(new String[]{"1000","1000","20000","481"},"");
//				String b = "1820";
//				String c = Utils.getStrings(new String[]{"zyap","1820","_","23339","_","100"},"");
////				ZhangPaySdk.getInstance().init(activity, a, b, c);
//			}
//		}.start();
	}
	
	private  static void initYijie(final Activity activity){
//		final Handler handler = new Handler(Looper.getMainLooper()){
//			public void handleMessage(Message message){
//				switch(message.what){
//				case 1:
//					showUserId(activity);
//					break;
//				default:
//					break;
//				}
//				
//				super.handleMessage(message);
//			}
//		};
//		
//		SFCommonSDKInterface.onInit(activity,new SFOfflineInitListener() {
//			
//			@Override
//			public void onResponse(String tag, String value) {
//				if(tag.equalsIgnoreCase("success")){
//					handler.sendEmptyMessage(1);
//				}else{
//					handler.sendEmptyMessage(0);
//				}
//			}
//		});
	}
	
	private static void showUserId(Activity activity){
//		long userId = APaymentUnity.getUserId(activity);
//		
//		Toast toast = Toast.makeText(activity, "用户id："+userId,
//				Toast.LENGTH_LONG);
//		toast.show();
	}

	
}
