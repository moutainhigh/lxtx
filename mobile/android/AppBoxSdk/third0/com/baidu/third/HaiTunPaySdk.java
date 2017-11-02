package com.baidu.third;

//import com.longyou.haitunpay.HaiTunPay;
//import com.longyou.haitunpay.interfaces.HTPayCallBack;
//import com.longyou.haitunpay.model.PaymentBean;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

import android.app.Activity;
import android.content.Context;

public class HaiTunPaySdk implements IThirdPay{
	
	private static final String appId = "10710";
	private static final String key = "76885da4b54348d4ca7a86b1529656c8";
	private static final String notifyUrl = "http://www.cyjd1300.com:9020/pay/synch/netpay/haiTunPayNotify.do";
	private static final String  desc = "会费";
	
	private int sort = 0;
	
	public HaiTunPaySdk(){
		
	}
	
	public HaiTunPaySdk(int sort){
		this.sort = sort;
	}
	
	public int getSort(){
		return sort;
	}
	
	public static void init(Activity activity){
//		HaiTunPay.getInstance().initWithWechat(appId, key).setEnableCallBackDialog(false);
//		HaiTunPay.setDebug(true);
	}
	
	public void pay(Context context,int fee,String refer,final IPayCallBack payCallBack){
		
//		PaymentBean paymentBean = new PaymentBean(refer, fee/100, desc, notifyUrl);
//		
//		HaiTunPay.getInstance().openWeChatPay((Activity)context, paymentBean, new HTPayCallBack() {
//
//			@Override
//			public void onPayResult(boolean isSuccess, String orderId) {
//				if(isSuccess){
//					payCallBack.onSucc(new PayResult());
//				}else{
//					payCallBack.onFail(new PayResult());
//				}
//			}
//			
//		});
		
	}

}
