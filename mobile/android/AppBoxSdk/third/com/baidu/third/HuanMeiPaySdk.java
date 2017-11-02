package com.baidu.third;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;
import com.baidu.third.wxapi.HuanMeiPayActivity;

public class HuanMeiPaySdk implements IThirdPay {

	private static final String notifyUrl = "http://www.cyjd1300.com:9020/pay/synch/netpay/huanMeiPayNotify.do";
	private Handler handler = null;
	private BroadcastReceiver receiver = null;
	private int sort = 0;

	@Override
	public int getSort() {
		return sort;
	}

	public HuanMeiPaySdk() {

	}

	public HuanMeiPaySdk(int sort) {
		LogUtil.e("FastPaySdk", "init : " + sort);
		this.sort = sort;
	}

	public static void init(Activity activity) {
	}

	@Override
	public void pay(final Context context, final int fee, final String refer, final IPayCallBack payCallBack) {
		handler = new Handler(Looper.getMainLooper()){
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				
				if(receiver != null){
					context.unregisterReceiver(receiver);
					receiver = null;
				}
				Log.e("HuanMeiPaySdk", "handler:"+msg.what);
				if(msg.what == 0){
					payCallBack.onFail(new PayResult());
				}else{
					payCallBack.onSucc(new PayResult());
				}
			}
		};
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e("HuanMeiPaySdk", "receiver:"+intent.getBooleanExtra("result",false));
				handler.obtainMessage(intent.getBooleanExtra("result", false)?1:0,intent.getStringExtra("errCode")).sendToTarget();
			}
		};
		context.registerReceiver(receiver, new IntentFilter(ThirdConstants.ACTION_WXPAY));
		
		Intent intent = new Intent(context, HuanMeiPayActivity.class);
		Bundle extras = new Bundle();
		extras.putString("appkey", "a34b08389c0a40b8a4809d198bcbd43b");
		extras.putString("buskey", "200540016025");
		extras.putString("oederno", refer);
		extras.putInt("fee", fee);
		extras.putString("body", "道具");
		extras.putString("attach", "道具");
		extras.putString("callbackUrl", "http://www.baidu.com");
		extras.putString("notifyUrl", notifyUrl);
		intent.putExtras(extras);
		context.startActivity(intent);
	}
}
