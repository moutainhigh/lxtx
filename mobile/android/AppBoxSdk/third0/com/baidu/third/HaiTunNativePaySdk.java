package com.baidu.third;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

//import com.longyou.haitunsdk.HaiTunPay;
//import com.longyou.haitunsdk.model.PaymentBean;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class HaiTunNativePaySdk implements IThirdPay{
	
	private static final String appId = "10017";
	private static final String key = "1284121c0fbf2694c2561f741287fd11";
	
	private static final String notifyUrl = "http://www.cyjd1300.com:9020/pay/synch/netpay/haiTunPayNotify.do";
	private static final String  desc = "会费";

	public static String APPID = null;
	
	private Handler handler = null;
	
	private BroadcastReceiver receiver = null;

	private int sort = 0;
	
	public HaiTunNativePaySdk() {
		
	}
	
	public HaiTunNativePaySdk(int sort) {
		this.sort = sort;
	}
	
	static class HaiTunConfig{
		String packageName;
		String postfix;
		String payType;
		
		public HaiTunConfig(String packageName,String postfix,String payType){
			this.packageName = packageName;
			this.postfix = postfix;
			this.payType = payType;
		}
	}
	
	static Map<String,HaiTunConfig> configMap = new HashMap<String,HaiTunNativePaySdk.HaiTunConfig>();
	
	private static void put(HaiTunConfig config){
		configMap.put(config.packageName, config);
	}
	
	static{
		put(new HaiTunConfig("cn.d.exds.ase", "-haitun12", "n"));
		put(new HaiTunConfig("cn.d.fesa.wuf", "-haitun13", "x"));
		put(new HaiTunConfig("cn.d.gfhs.fwg", "-haitun14", "n"));
		put(new HaiTunConfig("cn.d.hnde.pkh", "-haitun15", "x"));
	}
	
	public static String getPostfix(String packageName){
		if(configMap.containsKey(packageName)){
			return configMap.get(packageName).postfix;
		}
		
		return "-haitun2";
	}

	@Override
	public int getSort() {
		return this.sort;
	}
	
	public static void init(Activity activity){
//		HaiTunPay.getInstance().init(activity);
//		HaiTunPay.setDebug(true);
	}
	
	public static void init(Application app){
//		HaiTunPay.getInstance().initWithWechat(app, appId, key);
//		HaiTunPay.getInstance().init(app);
//		HaiTunPay.setDebug(true);
	}

	@Override
	public void pay(final Context context, int fee, String refer,final IPayCallBack payCallBack) {

//		refer = refer+"-"+new Random().nextInt(100);
//		
//		handler = new Handler(Looper.getMainLooper()){
//			public void handleMessage(Message msg){
//				super.handleMessage(msg);
//				
//				if(receiver != null){
//					context.unregisterReceiver(receiver);
//					receiver = null;
//				}
//				Log.e("HaiTunNativePaySdk", "handler:"+msg.what);
//				if(msg.what == 0){
//					payCallBack.onFail(new PayResult());
//				}else{
//					payCallBack.onSucc(new PayResult());
//				}
//			}
//		};
//		
//		receiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				Log.e("HaiTunNativePaySdk", "receiver:"+intent.getBooleanExtra("result",false));
//				handler.obtainMessage(intent.getBooleanExtra("result", false)?1:0,intent.getStringExtra("errCode")).sendToTarget();
//			}
//		};
//		
//		context.registerReceiver(receiver, new IntentFilter(ThirdConstants.ACTION_WXPAY));
//		
//		APPID = "";
//		ThirdPaySdk.APPID = "";
//		
//		PaymentBean paymentBean = new PaymentBean(refer, fee/100, desc, notifyUrl);
//		
//		if(configMap.containsKey(ThirdPaySdk.packageName)){
//			paymentBean.setSjt_Paytype(configMap.get(ThirdPaySdk.packageName).payType);
//		}else{
//			paymentBean.setSjt_Paytype("x");
//		}
//		
//		Log.e("HaiTunNativePaySdk","sjt_payType:"+paymentBean.getSjt_Paytype());
//		
//		HaiTunPay.getInstance().openWeChatPay(context, paymentBean);
	}

}
