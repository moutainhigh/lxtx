package com.baidu.third;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.alipay.Utils;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class HeePaySdk implements IThirdPay{

	private Handler handler = null;
	
	private BroadcastReceiver receiver = null;

	private int sort = 0;
	
	public HeePaySdk(){
		
	}
	
	public HeePaySdk(int sort){
		this.sort = sort;
	}
	
	public int getSort(){
		return sort;
	}
	
	public void pay(final Context context, int fee, String refer,final IPayCallBack payCallBack) {
//		fee = (int)(fee/100);
//		
//		handler = new Handler(Looper.getMainLooper()){
//			public void handleMessage(Message msg){
//				super.handleMessage(msg);
//				
//				if(receiver != null){
//					context.unregisterReceiver(receiver);
//					receiver = null;
//				}
//				
//				if(msg.what == 3){
//					new SuccConfirmDialog(context,Utils.getResource(context, "selectorDialog", "style"),this);
//				}else if(msg.what == 0){
//					PayResult payResult = new PayResult();
//					payResult.setCode((String)msg.obj);
//					
//					payCallBack.onFail(payResult);
//				}else{
//					payCallBack.onSucc(new PayResult());
//				}
//			}
//		};
//		
//		receiver = new BroadcastReceiver() {			
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				handler.obtainMessage(intent.getBooleanExtra("result", false)?1:0,intent.getStringExtra("errCode")).sendToTarget();
//			}
//		};
//		
//		context.registerReceiver(receiver, new IntentFilter(ThirdConstants.ACTION_HEEPAY));
//		
//		try{
//			Intent intent = new Intent();
//			intent.setClass(context, HeePayActivity.class);
//			
//			Bundle bundle = new Bundle();
//			bundle.putString("param", fee+"");
//			bundle.putString("refer", refer);
//			intent.putExtras(bundle);
//			
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			
//			context.startActivity(intent);
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	class SuccConfirmDialog extends Dialog implements android.view.View.OnClickListener {
		
		private Handler okHandler;
		
		public SuccConfirmDialog(Context context, int theme,Handler okHandler) {
			super(context, theme);
			
			this.okHandler = okHandler;
			
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
			
			View view = LayoutInflater.from(context).inflate(
					Utils.getResource(context, "jxt_sdk_dialog_ok", "layout"), null);
			
			Button	ok = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_comfirm_btn_ok", "id"));
			TextView msg = (TextView) view.findViewById(Utils.getResource(context, "jxt_tv_hint_ok", "id"));
//			TextView cancel = (TextView) view
//					.findViewById(Utils.getResource(context, "jxt_sdk_dialog_cancel_btn_ok", "id"));
			ok.setOnClickListener(this);
//			cancel.setOnClickListener(this);
			msg.setText("支付成功！如有疑问请致电：4001558668。");
			setCanceledOnTouchOutside(false);
			setContentView(view);
			show();
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			
			return false;
		}
		
		@Override
		public void onClick(View arg0) {
			okHandler.sendEmptyMessage(3);
			
			dismiss();
		}

	}
}
