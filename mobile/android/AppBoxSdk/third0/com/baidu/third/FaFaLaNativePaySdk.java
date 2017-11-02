package com.baidu.third;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

//import com.fafala.paylib.FaFaLaPay;
//import com.fafala.paylib.callbacks.FFLPayCallback;
//import com.fafala.paylib.models.OrderBean;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class FaFaLaNativePaySdk implements IThirdPay{

	private static final int FAFALA_MERID = 50000088;
	private static final String FAFALA_KEY = "B393A7B1E68CB8A0";
	private static final String NOTIFYURL = "http://www.cyjd1300.com:9020/pay/synch/netpay/fafalaPayNotify.do";
	private int sort = 0;

//	private Handler handler = null;
//	private BroadcastReceiver receiver = null;
	
	public FaFaLaNativePaySdk(){
		
	}
	
	public FaFaLaNativePaySdk(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		return this.sort;
	}
	
	public static String getPostfix(String packageName){
		String postfix = "-fafala1";
		
		if(packageName.equals("com.fjytmzelg.lyb")){
			postfix = "-fafala2";
		}
		
		return postfix;
	}
	
	public static void init(Activity activity){
//		FaFaLaPay.getInstance().init(activity,FAFALA_MERID,FAFALA_KEY);
//        FaFaLaPay.setDebug(true);
	}

	@Override
	public void pay(final Context context, int fee, String refer,
			final IPayCallBack payCallBack) {
		
//		handler = new Handler(Looper.getMainLooper()){
//			public void handleMessage(android.os.Message msg) {
//				super.handleMessage(msg);
//				
//				context.unregisterReceiver(receiver);
//				
//				if(msg.what == 1){
//					payCallBack.onSucc(new PayResult());
//				}else{
//					payCallBack.onFail(new PayResult());
//				}
//			}
//		};
//		
//		receiver = new BroadcastReceiver() {
//			
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				boolean result = intent.getBooleanExtra("result", false);
//				handler.obtainMessage(result?1:0).sendToTarget();
//			}
//		};
//		
//		context.registerReceiver(receiver, new IntentFilter(ThirdConstants.ACTION_WXPAY));
		
//		OrderBean orderBean = new OrderBean();
//        //订单号
//        orderBean.setMchTradeNo(refer);
//        //异步回调地址
//        orderBean.setNotifyUrl(NOTIFYURL);
//        //商品名称
//        orderBean.setTradeContent("buy");
//        //自定义透传字符串
//        orderBean.setTradeAttach(refer);
//        //支付金额
//        orderBean.setTradeMoney(fee);
//        //调用支付接口进行支付
//        FaFaLaPay.getInstance().startPay((Activity)context,FaFaLaPay.TYPE_WECHAT, orderBean, new FFLPayCallback() {
//			
//			@Override
//			public void onPayResuilt(int arg0, String arg1) {
//				if(FaFaLaPay.PAY_SUCCESS == arg0){
//					payCallBack.onSucc(new PayResult());
//				}else{
//					payCallBack.onFail(new PayResult());
//				}
//			}
//		});		
	}

}
