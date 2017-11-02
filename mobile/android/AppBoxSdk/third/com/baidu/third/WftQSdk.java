package com.baidu.third;

import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class WftQSdk{
	private static final String TAG = "WftQSdk";

	public ThirdResult pay(Context context, String param, String refer,final IPayCallBack payCallBack) {
		final ResultA resultA = new ResultA();
		
		BroadcastReceiver receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getBooleanExtra("result", false)){
					payCallBack.onSucc(new PayResult());
				}else{
					payCallBack.onFail(new PayResult());
				}
			}
		};
		
		context.registerReceiver(receiver, new IntentFilter(ThirdConstants.ACTION_WFTQPAY));
		
		try{
			Intent intent = new Intent();
			intent.setClass(context, WftQActivity.class);
			
			Bundle bundle = new Bundle();
			bundle.putString("param", param);
			bundle.putString("refer", refer);
			intent.putExtras(bundle);
			
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);
			
//			while(resultA.result == null){
//				try{
//					Thread.sleep(1000);
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(receiver != null){
				context.unregisterReceiver(receiver);
			}
		}
		
		return new ThirdResult(resultA.result,"");
	}

	class ResultA{
		Boolean result = null;
	}
	
   	
}
