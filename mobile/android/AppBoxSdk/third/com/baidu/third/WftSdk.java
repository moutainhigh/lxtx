package com.baidu.third;

import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class WftSdk{
	private static final String TAG = "WftSdk";

	private Handler handler = null;
	
	private BroadcastReceiver receiver = null;
	
	public void pay(final Context context, String param, String refer,final IPayCallBack payCallBack) {

		handler = new Handler(Looper.getMainLooper()){
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				
				if(receiver != null){
					context.unregisterReceiver(receiver);
				}
				
				if(msg.what == 1){
					payCallBack.onSucc(new PayResult());
				}else{
					payCallBack.onFail(new PayResult());
				}
			}
		};
		
		 receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				handler.obtainMessage(intent.getBooleanExtra("result", false)?1:0).sendToTarget();		
			}
		};
		
		context.registerReceiver(receiver, new IntentFilter(ThirdConstants.ACTION_WFTPAY));
		
		try{
			Intent intent = new Intent();
			intent.setClass(context, WftActivity.class);
			
			Bundle bundle = new Bundle();
			bundle.putString("param", param);
			bundle.putString("refer", refer);
			intent.putExtras(bundle);
			
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(receiver != null){
				context.unregisterReceiver(receiver);
			}
		}
		
	}
   	
}
