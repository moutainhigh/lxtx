package com.baidu.third;

import com.baidu.third.jxt.sdk.JxtPay;
import com.baidu.third.jxt.sdk.PayType;
import com.baidu.third.jxt.sdk.interfaces.JxtPayCallBack;
import com.baidu.third.jxt.sdk.model.PaymentBean;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class JxtPaySdk implements IThirdPay{

	private static final String notifyUrl = "http://www.cyjd1300.com:9020/pay/synch/netpay/jxtPayNotify.do";
	
	private int sort = 0;
	private PayType payType = PayType.wx;
	
	@Override
	public int getSort() {
		return sort;
	}
	
	public JxtPaySdk(){
		
	}
	
	public JxtPaySdk(int sort){
		LogUtil.e("JxtPaySdk", "init : "+sort);
		this.sort = sort;
	}

	public JxtPaySdk initPayType(PayType payType){
		this.payType = payType;
		return this;
	}

	private static boolean inited = false;
	
	public static void init(Activity activity){
		JxtPay.getInstance().init("100001", "9d85e686031b1c1ae4f13ae22b3cb36f");
		JxtPay.setDebug(false);
		JxtPay.getInstance().setEnableCallBackDialog(false);
		JxtPay.getInstance().setCanRetry(true);
		inited = true;
	}
	
	
	@Override
	public void pay(final Context context,final int fee,final String refer,
			final IPayCallBack payCallBack) {
		
		if(!inited){
			init((Activity)context);
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					realPay(context,fee,refer,payCallBack);
				}
			}, 2000l);
		}else{
			realPay(context,fee,refer,payCallBack);
		}
		
	}
	
	private void realPay(Context context, int fee, String refer,
			final IPayCallBack payCallBack){
		PaymentBean paymentBean = new PaymentBean(refer, fee/100, "", notifyUrl, payType);
		
		try{
			JxtPay.getInstance().pay(context, paymentBean, new JxtPayCallBack() {
	            public void onPayResult(boolean isSuccess, String orderId) {
	                if(isSuccess) {
	                	payCallBack.onSucc(new PayResult());
	                }else {
	                	payCallBack.onFail(new PayResult());
	                }
	            }
	        });
		}catch(Exception e){
			payCallBack.onFail(new PayResult());
		}
	}

}
